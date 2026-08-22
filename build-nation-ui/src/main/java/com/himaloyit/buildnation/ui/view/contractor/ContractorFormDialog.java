package com.himaloyit.buildnation.ui.view.contractor;

import com.himaloyit.buildnation.ui.client.contractor.ContractorClient;
import com.himaloyit.buildnation.ui.client.support.GatewayApiException;
import com.himaloyit.buildnation.ui.dto.contractor.ContractorDTO;
import com.himaloyit.buildnation.ui.dto.contractor.ContractorType;
import com.himaloyit.buildnation.ui.dto.contractor.CreateContractorRequest;
import com.himaloyit.buildnation.ui.dto.contractor.UpdateContractorRequest;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

/**
 * Add/Edit dialog for a Contractor. Status is set to ACTIVE by the backend on create and is only
 * ever changed afterward via the separate PATCH /{id}/status endpoint (see
 * {@link ContractorStatusChangeDialog}), so it's not a field on this form.
 */
public class ContractorFormDialog extends Dialog {

    private final Binder<ContractorDTO> binder = new Binder<>(ContractorDTO.class);

    public ContractorFormDialog(ContractorClient client, ContractorDTO existing, Runnable onSaved) {
        setHeaderTitle(existing == null ? "Add Contractor" : "Edit Contractor");

        TextField name = new TextField("Name");
        ComboBox<ContractorType> type = new ComboBox<>("Type");
        type.setItems(ContractorType.values());
        TextField contactNumber = new TextField("Contact Number");
        TextField address = new TextField("Address");
        TextField license = new TextField("License");
        TextField keyPersonName = new TextField("Key Person Name");
        TextField keyPersonContact = new TextField("Key Person Contact");
        TextField bankName = new TextField("Bank Name");
        TextField bankAccountNumber = new TextField("Bank Account Number");
        TextField bankBranch = new TextField("Bank Branch");

        binder.forField(name).asRequired("Name is mandatory").bind(ContractorDTO::getName, ContractorDTO::setName);
        binder.forField(type).asRequired("Type is mandatory").bind(ContractorDTO::getType, ContractorDTO::setType);
        binder.forField(contactNumber).asRequired("Contact number is mandatory")
                .bind(ContractorDTO::getContactNumber, ContractorDTO::setContactNumber);
        binder.forField(address).asRequired("Address is mandatory").bind(ContractorDTO::getAddress, ContractorDTO::setAddress);
        binder.forField(license).bind(ContractorDTO::getLicense, ContractorDTO::setLicense);
        binder.forField(keyPersonName).asRequired("Key person name is mandatory")
                .bind(ContractorDTO::getKeyPersonName, ContractorDTO::setKeyPersonName);
        binder.forField(keyPersonContact).asRequired("Key person contact is mandatory")
                .bind(ContractorDTO::getKeyPersonContact, ContractorDTO::setKeyPersonContact);
        binder.forField(bankName).bind(ContractorDTO::getBankName, ContractorDTO::setBankName);
        binder.forField(bankAccountNumber).bind(ContractorDTO::getBankAccountNumber, ContractorDTO::setBankAccountNumber);
        binder.forField(bankBranch).bind(ContractorDTO::getBankBranch, ContractorDTO::setBankBranch);

        ContractorDTO working = new ContractorDTO();
        if (existing != null) {
            working.setId(existing.getId());
            working.setName(existing.getName());
            working.setType(existing.getType());
            working.setStatus(existing.getStatus());
            working.setContactNumber(existing.getContactNumber());
            working.setAddress(existing.getAddress());
            working.setLicense(existing.getLicense());
            working.setKeyPersonName(existing.getKeyPersonName());
            working.setKeyPersonContact(existing.getKeyPersonContact());
            working.setBankName(existing.getBankName());
            working.setBankAccountNumber(existing.getBankAccountNumber());
            working.setBankBranch(existing.getBankBranch());
        }
        binder.readBean(working);

        FormLayout form = new FormLayout(name, type, contactNumber, address, license, keyPersonName,
                keyPersonContact, bankName, bankAccountNumber, bankBranch);
        add(form);

        Button cancel = new Button("Cancel", e -> close());
        Button save = new Button("Save", e -> save(client, existing, working, onSaved));
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(cancel, save);
    }

    private void save(ContractorClient client, ContractorDTO existing, ContractorDTO working, Runnable onSaved) {
        if (!binder.writeBeanIfValid(working)) {
            return;
        }
        try {
            if (existing == null) {
                client.create(new CreateContractorRequest(working.getName(), working.getType(), working.getContactNumber(),
                        working.getAddress(), working.getLicense(), working.getKeyPersonName(), working.getKeyPersonContact(),
                        working.getBankName(), working.getBankAccountNumber(), working.getBankBranch()));
            } else {
                client.update(existing.getId(), new UpdateContractorRequest(working.getName(), working.getType(),
                        working.getContactNumber(), working.getAddress(), working.getLicense(), working.getKeyPersonName(),
                        working.getKeyPersonContact(), working.getBankName(), working.getBankAccountNumber(),
                        working.getBankBranch()));
            }
            Notification.show("Contractor saved");
            onSaved.run();
            close();
        } catch (GatewayApiException ex) {
            Notification errorNotification = Notification.show("Could not save contractor: " + ex.getMessage(),
                    5000, Notification.Position.MIDDLE);
            errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}
