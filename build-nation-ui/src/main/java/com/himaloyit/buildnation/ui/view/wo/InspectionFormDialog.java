package com.himaloyit.buildnation.ui.view.wo;

import com.himaloyit.buildnation.ui.client.support.GatewayApiException;
import com.himaloyit.buildnation.ui.client.wo.InspectionClient;
import com.himaloyit.buildnation.ui.client.wo.WorkOrderClient;
import com.himaloyit.buildnation.ui.dto.wo.CreateInspectionRequest;
import com.himaloyit.buildnation.ui.dto.wo.InspectionDTO;
import com.himaloyit.buildnation.ui.dto.wo.UpdateInspectionRequest;
import com.himaloyit.buildnation.ui.dto.wo.WorkOrderDTO;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import java.util.List;
import java.util.UUID;

/**
 * Add/Edit dialog for an Inspection. Work Order can only be chosen at creation (the backend's
 * UpdateInspectionRequest has no workOrderId field — only progressPercentage/quality/remarks), so
 * that ComboBox is read-only when editing. Inspection date is likewise create-only per the backend
 * contract. Status is changed separately via {@link InspectionStatusChangeDialog}.
 */
public class InspectionFormDialog extends Dialog {

    private static final int REFERENCE_DATA_PAGE_SIZE = 500;

    private final Binder<InspectionDTO> binder = new Binder<>(InspectionDTO.class);
    private final List<WorkOrderDTO> workOrders;

    public InspectionFormDialog(InspectionClient client, WorkOrderClient workOrderClient, InspectionDTO existing,
                                 Runnable onSaved) {
        setHeaderTitle(existing == null ? "Add Inspection" : "Edit Inspection");
        this.workOrders = workOrderClient.getAll(0, REFERENCE_DATA_PAGE_SIZE).getContent();

        ComboBox<WorkOrderDTO> workOrder = new ComboBox<>("Work Order");
        workOrder.setItemLabelGenerator(WorkOrderDTO::getWorkOrderNumber);
        workOrder.setItems(workOrders);

        TextField inspectorName = new TextField("Inspector Name");
        IntegerField progressPercentage = new IntegerField("Progress %");
        progressPercentage.setMin(0);
        progressPercentage.setMax(100);
        TextField quality = new TextField("Quality");
        TextArea remarks = new TextArea("Remarks");
        DatePicker inspectionDate = new DatePicker("Inspection Date");

        binder.forField(workOrder).asRequired("Work order is mandatory")
                .bind(this::findWorkOrderOf, (dto, selected) -> dto.setWorkOrderId(selected == null ? null : selected.getId()));
        binder.forField(inspectorName).asRequired("Inspector name is mandatory")
                .bind(InspectionDTO::getInspectorName, InspectionDTO::setInspectorName);
        binder.forField(progressPercentage).asRequired("Progress percentage is mandatory")
                .bind(InspectionDTO::getProgressPercentage, InspectionDTO::setProgressPercentage);
        binder.forField(quality).asRequired("Quality is mandatory").bind(InspectionDTO::getQuality, InspectionDTO::setQuality);
        binder.forField(remarks).bind(InspectionDTO::getRemarks, InspectionDTO::setRemarks);
        binder.forField(inspectionDate).asRequired("Inspection date is mandatory")
                .bind(InspectionDTO::getInspectionDate, InspectionDTO::setInspectionDate);

        InspectionDTO working = new InspectionDTO();
        if (existing != null) {
            working.setId(existing.getId());
            working.setWorkOrderId(existing.getWorkOrderId());
            working.setInspectorName(existing.getInspectorName());
            working.setProgressPercentage(existing.getProgressPercentage());
            working.setQuality(existing.getQuality());
            working.setRemarks(existing.getRemarks());
            working.setStatus(existing.getStatus());
            working.setInspectionDate(existing.getInspectionDate());

            workOrder.setReadOnly(true);
            inspectionDate.setReadOnly(true);
        }
        binder.readBean(working);

        FormLayout form = new FormLayout(workOrder, inspectorName, progressPercentage, quality, remarks, inspectionDate);
        add(form);

        Button cancel = new Button("Cancel", e -> close());
        Button save = new Button("Save", e -> save(client, existing, working, onSaved));
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(cancel, save);
    }

    private WorkOrderDTO findWorkOrderOf(InspectionDTO dto) {
        if (dto.getWorkOrderId() == null) {
            return null;
        }
        return workOrders.stream().filter(w -> w.getId().equals(dto.getWorkOrderId())).findFirst().orElse(null);
    }

    private void save(InspectionClient client, InspectionDTO existing, InspectionDTO working, Runnable onSaved) {
        if (!binder.writeBeanIfValid(working)) {
            return;
        }
        try {
            if (existing == null) {
                client.create(new CreateInspectionRequest(working.getWorkOrderId(), working.getInspectorName(),
                        working.getProgressPercentage(), working.getQuality(), working.getRemarks(), working.getInspectionDate()));
            } else {
                client.update(existing.getId(), new UpdateInspectionRequest(working.getProgressPercentage(),
                        working.getQuality(), working.getRemarks()));
            }
            Notification.show("Inspection saved");
            onSaved.run();
            close();
        } catch (GatewayApiException ex) {
            Notification errorNotification = Notification.show("Could not save inspection: " + ex.getMessage(),
                    5000, Notification.Position.MIDDLE);
            errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}
