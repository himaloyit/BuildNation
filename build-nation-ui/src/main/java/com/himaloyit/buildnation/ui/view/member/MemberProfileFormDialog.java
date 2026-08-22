package com.himaloyit.buildnation.ui.view.member;

import com.himaloyit.buildnation.ui.client.member.MemberProfileClient;
import com.himaloyit.buildnation.ui.client.support.GatewayApiException;
import com.himaloyit.buildnation.ui.dto.member.MemberDTO;
import com.himaloyit.buildnation.ui.dto.member.MemberProfileDTO;
import com.himaloyit.buildnation.ui.dto.member.UpdateMemberProfileRequest;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

/**
 * Edit dialog for a Member's Profile sub-resource. There is no create/delete here — every Member
 * always has a profile (auto-created alongside it server-side), so this is update-only, fetching
 * the current profile fresh via GET when opened.
 */
public class MemberProfileFormDialog extends Dialog {

    private final Binder<MemberProfileDTO> binder = new Binder<>(MemberProfileDTO.class);

    public MemberProfileFormDialog(MemberProfileClient client, MemberDTO member, Runnable onSaved) {
        setHeaderTitle("Profile: " + member.getFullName());

        MemberProfileDTO working = client.getByMemberId(member.getId());

        TextField dob = new TextField("Date of Birth");
        TextField gender = new TextField("Gender");
        TextField nationality = new TextField("Nationality");
        TextField streetAddress = new TextField("Street Address");
        TextField city = new TextField("City");
        TextField state = new TextField("State");
        TextField country = new TextField("Country");
        TextField postalCode = new TextField("Postal Code");
        TextField facebookUrl = new TextField("Facebook URL");
        TextField twitterUrl = new TextField("Twitter URL");
        TextField linkedinUrl = new TextField("LinkedIn URL");
        TextField instagramUrl = new TextField("Instagram URL");
        TextField websiteUrl = new TextField("Website URL");

        binder.forField(dob).bind(MemberProfileDTO::getDob, MemberProfileDTO::setDob);
        binder.forField(gender).bind(MemberProfileDTO::getGender, MemberProfileDTO::setGender);
        binder.forField(nationality).bind(MemberProfileDTO::getNationality, MemberProfileDTO::setNationality);
        binder.forField(streetAddress).bind(MemberProfileDTO::getStreetAddress, MemberProfileDTO::setStreetAddress);
        binder.forField(city).bind(MemberProfileDTO::getCity, MemberProfileDTO::setCity);
        binder.forField(state).bind(MemberProfileDTO::getState, MemberProfileDTO::setState);
        binder.forField(country).bind(MemberProfileDTO::getCountry, MemberProfileDTO::setCountry);
        binder.forField(postalCode).bind(MemberProfileDTO::getPostalCode, MemberProfileDTO::setPostalCode);
        binder.forField(facebookUrl).bind(MemberProfileDTO::getFacebookUrl, MemberProfileDTO::setFacebookUrl);
        binder.forField(twitterUrl).bind(MemberProfileDTO::getTwitterUrl, MemberProfileDTO::setTwitterUrl);
        binder.forField(linkedinUrl).bind(MemberProfileDTO::getLinkedinUrl, MemberProfileDTO::setLinkedinUrl);
        binder.forField(instagramUrl).bind(MemberProfileDTO::getInstagramUrl, MemberProfileDTO::setInstagramUrl);
        binder.forField(websiteUrl).bind(MemberProfileDTO::getWebsiteUrl, MemberProfileDTO::setWebsiteUrl);
        binder.readBean(working);

        FormLayout form = new FormLayout(dob, gender, nationality, streetAddress, city, state, country, postalCode,
                facebookUrl, twitterUrl, linkedinUrl, instagramUrl, websiteUrl);
        add(form);

        Button cancel = new Button("Cancel", e -> close());
        Button save = new Button("Save", e -> save(client, member, working, onSaved));
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(cancel, save);
    }

    private void save(MemberProfileClient client, MemberDTO member, MemberProfileDTO working, Runnable onSaved) {
        if (!binder.writeBeanIfValid(working)) {
            return;
        }
        try {
            client.update(member.getId(), new UpdateMemberProfileRequest(working.getDob(), working.getGender(),
                    working.getNationality(), working.getStreetAddress(), working.getCity(), working.getState(),
                    working.getCountry(), working.getPostalCode(), working.getFacebookUrl(), working.getTwitterUrl(),
                    working.getLinkedinUrl(), working.getInstagramUrl(), working.getWebsiteUrl()));
            Notification.show("Profile saved");
            onSaved.run();
            close();
        } catch (GatewayApiException ex) {
            Notification errorNotification = Notification.show("Could not save profile: " + ex.getMessage(),
                    5000, Notification.Position.MIDDLE);
            errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}
