package com.example.application.views.gamebrowser;

import com.example.application.data.entity.MapGame;
import com.example.application.data.service.CapitalCityService;
import com.example.application.data.service.MapGameService;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;

public class EditSession extends Dialog {
    private TextField nameField;

    private IntegerField playerCountField;

    private Checkbox isPrivateCheckbox;

    private final MapGameService gameService;
    private final CapitalCityService capitalCityService;
    private MapGame game;


    public EditSession(MapGameService gameService, CapitalCityService capitalCityService, MapGame mapGame) {
        this.gameService = gameService;
        this.capitalCityService = capitalCityService;
        this.game = mapGame;
        initComponents();
        setHeaderTitle("Edit Game");
    }

    public EditSession(MapGameService gameService, CapitalCityService capitalCityService){
        this.gameService = gameService;
        this.capitalCityService = capitalCityService;
        initComponents();
        setHeaderTitle("Create Game");
    }

    private void initComponents(){
        setCloseOnEsc(false);
        setCloseOnOutsideClick(false);

        if (game == null) game = new MapGame();
        nameField = new TextField("Name:");
        nameField.setSuffixComponent(VaadinIcon.TEXT_LABEL.create());
        nameField.setValueChangeMode(ValueChangeMode.EAGER);
        nameField.setRequired(true);
        nameField.setRequiredIndicatorVisible(true);

        playerCountField = new IntegerField("Max. Player Count:");
        playerCountField.setValue(2);
        playerCountField.setMin(2);
        playerCountField.setStepButtonsVisible(true);
        playerCountField.setSuffixComponent(VaadinIcon.GAMEPAD.create());
        playerCountField.setWidth(75f, Unit.PERCENTAGE);
        playerCountField.setValueChangeMode(ValueChangeMode.EAGER);
        playerCountField.setRequiredIndicatorVisible(true);

        isPrivateCheckbox = new Checkbox("Private?");
        var labelIcon = new Icon(VaadinIcon.LOCK);
        labelIcon.setSize("var(--lumo-icon-size-s)");
        labelIcon.addClickListener(iconClickEvent -> isPrivateCheckbox.setValue(!isPrivateCheckbox.getValue()));
        var checkBoxSpan = new Span(labelIcon, new Label("Private?"));
        checkBoxSpan.addClickListener(spanClickEvent -> isPrivateCheckbox.setValue(!isPrivateCheckbox.getValue()));
        isPrivateCheckbox.setLabelComponent(checkBoxSpan);

        var binder = new Binder<>(MapGame.class);
        binder.forField(nameField).withValidator(s -> !s.isBlank(), "Cannot be empty")
                .bind(MapGame::getGameName, MapGame::setGameName);
        binder.forField(playerCountField).withValidator(integer -> integer > 1, "At least 2 players required")
                .bind(MapGame::getMaxPLayerCount, MapGame::setMaxPLayerCount);
        binder.forField(isPrivateCheckbox).bind(MapGame::isPrivate, MapGame::setPrivate);
        binder.setBean(game);

        var notification = new Notification("", 5000, Notification.Position.BOTTOM_CENTER);

        var saveButton = new Button("Save", VaadinIcon.CHECK_CIRCLE.create(), buttonClickEvent -> {
            try {
                game.setCapitalCity(capitalCityService.getRandomCapitalCity());
                binder.writeBean(game);
                gameService.save(binder.getBean());
                notification.setText("Game created successfully");
                notification.open();
                close();
            } catch (Exception e) {
                notification.setText("Something went wrong");
                notification.open();
                throw new RuntimeException(e);
            }
        });
        saveButton.setEnabled(false);
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        binder.addStatusChangeListener(statusChangeEvent -> saveButton.setEnabled(!statusChangeEvent.hasValidationErrors()
        && !nameField.getValue().isBlank()));

        var closeButton = new Button(VaadinIcon.CLOSE_BIG.create(), buttonClickEvent -> close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        var verticalLayout = new VerticalLayout(nameField, playerCountField, isPrivateCheckbox);
        verticalLayout.setPadding(false);
        verticalLayout.setMargin(false);

        getHeader().add(closeButton);
        add(verticalLayout);
        getFooter().add(saveButton);

    }
}
