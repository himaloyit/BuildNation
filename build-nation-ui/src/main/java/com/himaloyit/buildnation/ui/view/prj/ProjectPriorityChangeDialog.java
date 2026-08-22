package com.himaloyit.buildnation.ui.view.prj;

import com.himaloyit.buildnation.ui.client.prj.ProjectClient;
import com.himaloyit.buildnation.ui.client.support.GatewayApiException;
import com.himaloyit.buildnation.ui.dto.prj.ProjectDTO;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.IntegerField;

/** Small dialog driving a Project's priority score via PATCH /{id}/priority. */
public class ProjectPriorityChangeDialog extends Dialog {

    public ProjectPriorityChangeDialog(ProjectClient client, ProjectDTO project, Runnable onSaved) {
        setHeaderTitle("Change Priority Score: " + project.getName());

        IntegerField priorityScore = new IntegerField("Priority Score");
        priorityScore.setMin(0);
        priorityScore.setValue(project.getPriorityScore() == null ? 0 : project.getPriorityScore());
        priorityScore.setWidthFull();
        add(priorityScore);

        Button cancel = new Button("Cancel", e -> close());
        Button save = new Button("Save", e -> {
            Integer value = priorityScore.getValue();
            if (value == null) {
                return;
            }
            try {
                client.updatePriority(project.getId(), value);
                Notification.show("Priority score updated");
                onSaved.run();
                close();
            } catch (GatewayApiException ex) {
                Notification errorNotification = Notification.show("Could not update priority: " + ex.getMessage(),
                        5000, Notification.Position.MIDDLE);
                errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(cancel, save);
    }
}
