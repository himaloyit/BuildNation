package com.himaloyit.buildnation.ui.component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

/**
 * Prev/next control for a backend-paginated {@code Page<T>} endpoint. Deliberately not a
 * lazy/infinite-scroll data provider — the region (and later domain) list endpoints are
 * plain {@code ?page=&size=} REST calls, so one page is fetched and shown at a time.
 */
public class PaginationBar extends HorizontalLayout {

    private final Span label = new Span();
    private final Button previous = new Button("Previous");
    private final Button next = new Button("Next");

    public PaginationBar(Runnable onPrevious, Runnable onNext) {
        previous.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        next.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        previous.addClickListener(e -> onPrevious.run());
        next.addClickListener(e -> onNext.run());
        setWidthFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        add(previous, label, next);
    }

    public void update(int pageNumber, int totalPages) {
        previous.setEnabled(pageNumber > 0);
        next.setEnabled(pageNumber < totalPages - 1);
        label.setText(totalPages == 0 ? "No results" : "Page " + (pageNumber + 1) + " of " + totalPages);
    }
}
