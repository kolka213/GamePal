package com.example.application.views.masterdetail;

import com.example.application.data.entity.CapitalCity;
import com.example.application.data.service.CapitalCityService;
import com.example.application.security.SecurityService;
import com.example.application.views.MainLayout;
import com.vaadin.collaborationengine.CollaborationAvatarGroup;
import com.vaadin.collaborationengine.CollaborationBinder;
import com.vaadin.collaborationengine.UserInfo;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.lang.NonNull;

import javax.annotation.security.RolesAllowed;
import java.util.Optional;
import java.util.UUID;

@PageTitle("City Details")
@Route(value = "cities/:cityID?/:action?(edit)", layout = MainLayout.class)
@Uses(Icon.class)
@RolesAllowed("ADMIN")
public class CitiesAdminView extends Div implements BeforeEnterObserver {

    private final String cityID = "cityID";
    private final String CITY_EDIT_ROUTE_TEMPLATE = "cities/%s/edit";

    private final Grid<CapitalCity> grid = new Grid<>(CapitalCity.class, false);

    CollaborationAvatarGroup avatarGroup;

    private TextField name;
    private NumberField latitude;
    private NumberField longitude;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");
    private final Button delete = new Button("delete");

    private final CollaborationBinder<CapitalCity> binder;

    private CapitalCity capitalCity;

    private final CapitalCityService cityService;
    @NonNull
    private final SecurityService securityService;

    @Autowired
    public CitiesAdminView(CapitalCityService cityService, SecurityService securityService) {
        this.cityService = cityService;
        this.securityService = securityService;
        addClassNames("master-detail-view");

        // UserInfo is used by Collaboration Engine and is used to share details
        // of users to each other to able collaboration. Replace this with
        // information about the actual user that is logged, providing a user
        // identifier, and the user's real name. You can also provide the users
        // avatar by passing an url to the image as a third parameter, or by
        // configuring an `ImageProvider` to `avatarGroup`.
        UserInfo userInfo = new UserInfo(String.valueOf(securityService.getAuthenticatedUser().hashCode()),
                securityService.getAuthenticatedUser().getUsername());

        // Create UI
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSplitterPosition(70);

        avatarGroup = new CollaborationAvatarGroup(userInfo, null);
        avatarGroup.getStyle().set("visibility", "hidden");

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("name").setAutoWidth(true);
        grid.addColumn("latitude").setAutoWidth(true);
        grid.addColumn("longitude").setAutoWidth(true);

        grid.setItems(query -> cityService.getAll(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(CITY_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
                delete.setVisible(true);
            } else {
                clearForm();
                UI.getCurrent().navigate(CitiesAdminView.class);
                delete.setVisible(false);
            }
        });

        // Configure Form
        binder = new CollaborationBinder<>(CapitalCity.class, userInfo);

        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        delete.addClickListener(buttonClickEvent ->{
            cityService.delete(this.capitalCity);
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.capitalCity == null) {
                    this.capitalCity = new CapitalCity();
                }
                binder.writeBean(this.capitalCity);

                cityService.update(this.capitalCity);
                clearForm();
                refreshGrid();
                Notification.show("City details stored.");
                UI.getCurrent().navigate(CitiesAdminView.class);
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the city details.");
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<UUID> cityId = event.getRouteParameters().get(cityID).map(UUID::fromString);
        if (cityId.isPresent()) {
            Optional<CapitalCity> capitalCityFromBackend = cityService.get(cityId.get());
            if (capitalCityFromBackend.isPresent()) {
                populateForm(capitalCityFromBackend.get());
                delete.setVisible(true);
            } else {
                Notification.show(
                        String.format("The requested city was not found, ID = %d", cityId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                delete.setVisible(false);
                event.forwardTo(CitiesAdminView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        name = new TextField("City");
        longitude = new NumberField("Latitude");
        latitude = new NumberField("Longitude");

        formLayout.add(name, longitude, latitude);

        editorDiv.add(avatarGroup, formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
        delete.setVisible(false);
        buttonLayout.add(save, cancel, delete);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(CapitalCity value) {
        this.capitalCity = value;
        String topic = null;
        if (this.capitalCity != null && this.capitalCity.getId() != null) {
            topic = "cities/" + this.capitalCity.getId();
            avatarGroup.getStyle().set("visibility", "visible");
        } else {
            avatarGroup.getStyle().set("visibility", "hidden");
        }
        binder.setTopic(topic, () -> this.capitalCity);
        avatarGroup.setTopic(topic);

    }
}
