package com.himaloyit.buildnation.sac.rbac.config;

import com.himaloyit.buildnation.sac.rbac.domain.entities.Permission;
import com.himaloyit.buildnation.sac.rbac.domain.entities.Principal;
import com.himaloyit.buildnation.sac.rbac.domain.entities.PrincipalRole;
import com.himaloyit.buildnation.sac.rbac.domain.entities.Resource;
import com.himaloyit.buildnation.sac.rbac.domain.entities.Role;
import com.himaloyit.buildnation.sac.rbac.domain.entities.RolePermission;
import com.himaloyit.buildnation.sac.rbac.domain.enums.Action;
import com.himaloyit.buildnation.sac.rbac.domain.enums.CredentialType;
import com.himaloyit.buildnation.sac.rbac.domain.enums.HttpMethod;
import com.himaloyit.buildnation.sac.rbac.domain.enums.PrincipalType;
import com.himaloyit.buildnation.sac.rbac.domain.enums.ResourceType;
import com.himaloyit.buildnation.sac.rbac.domain.enums.Status;
import com.himaloyit.buildnation.sac.rbac.domain.repositories.iRepositories.IPermissionRepository;
import com.himaloyit.buildnation.sac.rbac.domain.repositories.iRepositories.IPrincipalRepository;
import com.himaloyit.buildnation.sac.rbac.domain.repositories.iRepositories.IPrincipalRoleRepository;
import com.himaloyit.buildnation.sac.rbac.domain.repositories.iRepositories.IResourceRepository;
import com.himaloyit.buildnation.sac.rbac.domain.repositories.iRepositories.IRolePermissionRepository;
import com.himaloyit.buildnation.sac.rbac.domain.repositories.iRepositories.IRoleRepository;
import com.himaloyit.buildnation.sac.rbac.security.CurrentPrincipalContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/*
 * Author: Rajib Kumer Ghosh
 *
 * Idempotent startup seeder (Prompt-1 §7, adapted — the spec's sample data is generic
 * banking-domain example content, not BuildNation-specific). Scoped to what this pass
 * can actually protect: security-access-control's own new RBAC admin API. Seeding
 * permissions for other services (member-management, cdm) is deferred until
 * cross-service enforcement actually checks them.
 */

@Slf4j
@Component
public class RbacSeeder implements CommandLineRunner {

    private record RoleDef(String code, String name, String description) {
    }

    private record ResourceDef(String entity, Action action, HttpMethod httpMethod, String apiPath) {
    }

    private static final List<RoleDef> ROLE_DEFS = List.of(
            new RoleDef("ADMIN", "Administrator", "Full RBAC administration access"),
            new RoleDef("MP_MINISTER", "MP / Minister", "Constituency MP or Minister"),
            new RoleDef("PROJECT_OFFICER", "Project Officer", "Manages constituency development projects"),
            new RoleDef("ENGINEER", "Engineer", "Technical inspection and progress verification"),
            new RoleDef("FINANCE_OFFICER", "Finance Officer", "Fund allocation and payment approval"),
            new RoleDef("VIEWER", "Viewer", "Read-only access")
    );

    private static final List<String> RBAC_ENTITIES = List.of("principal", "role", "permission", "resource");

    private final IRoleRepository roleRepository;
    private final IResourceRepository resourceRepository;
    private final IPermissionRepository permissionRepository;
    private final IRolePermissionRepository rolePermissionRepository;
    private final IPrincipalRepository principalRepository;
    private final IPrincipalRoleRepository principalRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminEmail;
    private final String adminPassword;

    public RbacSeeder(IRoleRepository roleRepository,
                       IResourceRepository resourceRepository,
                       IPermissionRepository permissionRepository,
                       IRolePermissionRepository rolePermissionRepository,
                       IPrincipalRepository principalRepository,
                       IPrincipalRoleRepository principalRoleRepository,
                       PasswordEncoder passwordEncoder,
                       @Value("${rbac.admin.email:admin@buildnation.local}") String adminEmail,
                       @Value("${rbac.admin.password:Admin@123}") String adminPassword) {
        this.roleRepository = roleRepository;
        this.resourceRepository = resourceRepository;
        this.permissionRepository = permissionRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.principalRepository = principalRepository;
        this.principalRoleRepository = principalRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Map<String, Role> roles = seedRoles();
        Map<String, Permission> permissions = seedResourcesAndPermissions();
        seedRolePermissions(roles.get("ADMIN"), permissions.values());
        List<Permission> viewOnly = permissions.values().stream()
                .filter(p -> p.getAction() == Action.VIEW)
                .toList();
        seedRolePermissions(roles.get("VIEWER"), viewOnly);
        seedBootstrapAdmin(roles.get("ADMIN"));
        log.info("RBAC seed complete: {} roles, {} resources, {} permissions", roles.size(),
                resourceRepository.count(), permissions.size());
    }

    /**
     * Without this, nobody could ever reach ADMIN: assigning a role requires
     * principal:update, which only ADMIN holds, and no Principal exists yet on a
     * fresh database. Seeds exactly one bootstrap admin, idempotent on principalCode.
     */
    private void seedBootstrapAdmin(Role adminRole) {
        if (adminRole == null || principalRepository.existsByPrincipalCode(adminEmail)) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        Principal admin = Principal.builder()
                .principalCode(adminEmail)
                .principalName("System Administrator")
                .principalType(PrincipalType.USER)
                .email(adminEmail)
                .password(passwordEncoder.encode(adminPassword))
                .credentialType(CredentialType.PASSWORD)
                .enabled(true)
                .accountNonLocked(true)
                .status(Status.ACTIVE)
                .createdBy("system")
                .createdDate(now)
                .updatedBy("system")
                .updatedDate(now)
                .build();
        Principal saved = principalRepository.save(admin);

        PrincipalRole principalRole = PrincipalRole.builder()
                .principal(saved)
                .role(adminRole)
                .status(Status.ACTIVE)
                .createdBy("system")
                .createdDate(now)
                .updatedBy("system")
                .updatedDate(now)
                .build();
        principalRoleRepository.save(principalRole);
        log.warn("Seeded bootstrap admin principal '{}' with the configured/default password — " +
                "change it immediately in a real deployment (rbac.admin.password).", adminEmail);
    }

    private Map<String, Role> seedRoles() {
        Map<String, Role> result = new LinkedHashMap<>();
        String caller = CurrentPrincipalContext.currentPrincipalCode();
        for (RoleDef def : ROLE_DEFS) {
            Role role = roleRepository.findByRoleCode(def.code()).orElseGet(() -> {
                LocalDateTime now = LocalDateTime.now();
                Role created = Role.builder()
                        .roleCode(def.code())
                        .roleName(def.name())
                        .description(def.description())
                        .status(Status.ACTIVE)
                        .createdBy(caller)
                        .createdDate(now)
                        .updatedBy(caller)
                        .updatedDate(now)
                        .build();
                return roleRepository.save(created);
            });
            result.put(def.code(), role);
        }
        return result;
    }

    private Map<String, Permission> seedResourcesAndPermissions() {
        String caller = CurrentPrincipalContext.currentPrincipalCode();
        Map<String, Permission> permissions = new LinkedHashMap<>();

        for (String entity : RBAC_ENTITIES) {
            String basePath = "/api/v1/" + entity + "s";
            List<ResourceDef> defs = List.of(
                    new ResourceDef(entity, Action.CREATE, HttpMethod.POST, basePath + "/create"),
                    new ResourceDef(entity, Action.VIEW, HttpMethod.GET, basePath + "/**"),
                    new ResourceDef(entity, Action.UPDATE, HttpMethod.PUT, basePath + "/*"),
                    new ResourceDef(entity, Action.DELETE, HttpMethod.DELETE, basePath + "/*")
            );

            for (ResourceDef def : defs) {
                String resourceCode = entity + "-" + def.action().name().toLowerCase();
                String permissionCode = entity + ":" + def.action().name().toLowerCase();
                LocalDateTime now = LocalDateTime.now();

                Resource resource = resourceRepository.findByResourceCode(resourceCode).orElseGet(() -> {
                    Resource created = Resource.builder()
                            .resourceCode(resourceCode)
                            .resourceName(entity + " " + def.action().name().toLowerCase())
                            .serviceName("security-access-control")
                            .apiPath(def.apiPath())
                            .httpMethod(def.httpMethod())
                            .resourceType(ResourceType.API)
                            .status(Status.ACTIVE)
                            .createdBy(caller)
                            .createdDate(now)
                            .updatedBy(caller)
                            .updatedDate(now)
                            .build();
                    return resourceRepository.save(created);
                });

                Permission permission = permissionRepository.findByPermissionCode(permissionCode).orElseGet(() -> {
                    Permission created = Permission.builder()
                            .permissionCode(permissionCode)
                            .permissionName(entity + " " + def.action().name().toLowerCase())
                            .action(def.action())
                            .resource(resource)
                            .status(Status.ACTIVE)
                            .createdBy(caller)
                            .createdDate(now)
                            .updatedBy(caller)
                            .updatedDate(now)
                            .build();
                    return permissionRepository.save(created);
                });
                permissions.put(permissionCode, permission);
            }
        }
        return permissions;
    }

    private void seedRolePermissions(Role role, Iterable<Permission> permissions) {
        if (role == null) {
            return;
        }
        String caller = CurrentPrincipalContext.currentPrincipalCode();
        for (Permission permission : permissions) {
            boolean exists = rolePermissionRepository
                    .existsByRole_RoleIdAndPermission_PermissionId(role.getRoleId(), permission.getPermissionId());
            if (exists) {
                continue;
            }
            LocalDateTime now = LocalDateTime.now();
            RolePermission rolePermission = RolePermission.builder()
                    .role(role)
                    .permission(permission)
                    .status(Status.ACTIVE)
                    .createdBy(caller)
                    .createdDate(now)
                    .updatedBy(caller)
                    .updatedDate(now)
                    .build();
            rolePermissionRepository.save(rolePermission);
        }
    }
}
