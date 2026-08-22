package com.himaloyit.buildnation.ui.view.prj;

import com.himaloyit.buildnation.ui.client.prj.ProjectClient;
import com.himaloyit.buildnation.ui.client.support.GatewayApiException;
import com.himaloyit.buildnation.ui.dto.prj.ProjectDTO;
import com.himaloyit.buildnation.ui.dto.prj.ProjectStatus;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

/** Small dialog driving the Project status lifecycle via PATCH /{id}/status. */
public class ProjectStatusChangeDialog extends Dialog {

    public ProjectStatusChangeDialog(ProjectClient client, ProjectDTO project, Runnable onSaved) {
        setHeaderTitle("Change Status: " + project.getName());

        ComboBox<ProjectStatus> status = new ComboBox<>("New Status");
        status.setItems(ProjectStatus.values());
        status.setValue(project.getStatus());
        status.setWidthFull();
        add(status);

        Button cancel = new Button("Cancel", e -> close());
        Button save = new Button("Save", e -> {
            ProjectStatus selected = status.getValue();
            if (selected == null) {
                return;
            }
            try {
                client.updateStatus(project.getId(), selected);
                Notification.show("Project status updated");
                onSaved.run();
                close();
            } catch (GatewayApiException ex) {
                Notification errorNotification = Notification.show("Could not update status: " + ex.getMessage(),
                        5000, Notification.Position.MIDDLE);
                errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(cancel, save);
    }
}
