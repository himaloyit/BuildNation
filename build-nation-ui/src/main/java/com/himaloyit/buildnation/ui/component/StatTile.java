package com.himaloyit.buildnation.ui.component;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

/**
 * A small "label + big number" card for dashboard summaries. Every value shown in one of these must
 * come from a real API response — never fabricate a number here (see Doc/Prompt/
 * Build_Nation_Vaadin_UI_Prompt.docx §9).
 */
public class StatTile extends Div {

    private final Span valueSpan = new Span();

    public StatTile(String label, String initialValue) {
        addClassNames(LumoUtility.Background.CONTRAST_5, LumoUtility.BorderRadius.MEDIUM,
                LumoUtility.Padding.MEDIUM);
        getStyle().set("flex", "1 1 12em").set("min-width", "12em");

        Span labelSpan = new Span(label);
        labelSpan.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.SMALL);

        valueSpan.setText(initialValue);
        valueSpan.addClassNames(LumoUtility.FontSize.XXLARGE, LumoUtility.FontWeight.BOLD);

        VerticalLayout content = new VerticalLayout(labelSpan, valueSpan);
        content.setPadding(false);
        content.setSpacing(false);
        add(content);
    }

    public void setValue(String value) {
        valueSpan.setText(value);
    }
}
