package com.himaloyit.buildnation.ui.view.audit;

import com.himaloyit.buildnation.ui.dto.audit.AuditLogDTO;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

/**
 * Read-only detail view for one AuditLog entry — shows the full old/new value snapshots that don't
 * fit in a grid column. There is no edit form: audit logs are append-only on the backend.
 */
public class AuditLogDetailDialog extends Dialog {

    public AuditLogDetailDialog(AuditLogDTO auditLog) {
        setHeaderTitle("Audit Log Detail");
        setWidth("40em");

        TextField entityType = readOnlyField("Entity Type", auditLog.getEntityType());
        TextField entityId = readOnlyField("Entity ID", String.valueOf(auditLog.getEntityId()));
        TextField action = readOnlyField("Action", auditLog.getAction() == null ? "-" : auditLog.getAction().name());
        TextField performedBy = readOnlyField("Performed By", auditLog.getPerformedBy());
        TextField performedAt = readOnlyField("Performed At", String.valueOf(auditLog.getPerformedAt()));

        TextArea oldValue = new TextArea("Old Value");
        oldValue.setValue(auditLog.getOldValue() == null ? "" : auditLog.getOldValue());
        oldValue.setReadOnly(true);
        oldValue.setWidthFull();
        oldValue.setMinHeight(6, Unit.EM);

        TextArea newValue = new TextArea("New Value");
        newValue.setValue(auditLog.getNewValue() == null ? "" : auditLog.getNewValue());
        newValue.setReadOnly(true);
        newValue.setWidthFull();
        newValue.setMinHeight(6, Unit.EM);

        FormLayout form = new FormLayout(entityType, entityId, action, performedBy, performedAt, oldValue, newValue);
        form.setColspan(oldValue, 2);
        form.setColspan(newValue, 2);
        add(form);

        Button close = new Button("Close", e -> close());
        getFooter().add(close);
    }

    private TextField readOnlyField(String label, String value) {
        TextField field = new TextField(label);
        field.setValue(value == null ? "-" : value);
        field.setReadOnly(true);
        return field;
    }
}
