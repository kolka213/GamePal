package com.example.application.views.gamebrowser;

import com.example.application.data.entity.CapitalCity;
import com.example.application.data.entity.Game;
import com.example.application.data.entity.GuessingGame;
import com.example.application.data.entity.MapGame;
import com.example.application.data.service.CapitalCityService;
import com.example.application.data.service.GuessingGameService;
import com.example.application.data.service.MapGameService;
import com.example.application.data.service.WordsService;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;

import java.util.List;
import java.util.UUID;

public class EditSession extends Dialog {
    private TextField nameField;

    private IntegerField playerCountField;

    private Checkbox isPrivateCheckbox;

    private final MapGameService mapGameService;
    private final CapitalCityService capitalCityService;
    private final GuessingGameService guessingGameService;
    private final WordsService wordsService;
    private Game game;

    Binder<Game> binder;


    public EditSession(MapGameService mapGameService, CapitalCityService capitalCityService, GuessingGameService guessingGameService, WordsService wordsService, MapGame game) {
        this.mapGameService = mapGameService;
        this.capitalCityService = capitalCityService;
        this.guessingGameService = guessingGameService;
        this.wordsService = wordsService;
        this.game = game;
        initComponents();
        setHeaderTitle("Edit Game");
    }

    public EditSession(MapGameService mapGameService, CapitalCityService capitalCityService, GuessingGameService guessingGameService, WordsService wordsService){
        this.mapGameService = mapGameService;
        this.capitalCityService = capitalCityService;
        this.guessingGameService = guessingGameService;
        this.wordsService = wordsService;
        initComponents();
        setHeaderTitle("Create Game");
    }

    private void initComponents(){
        setCloseOnEsc(false);
        setCloseOnOutsideClick(false);

        binder = new Binder<>();

        var gameTypeSelect = new Select<String>();
        gameTypeSelect.setLabel("Game Type:");
        gameTypeSelect.setItems("Map Game", "Guessing Game");
        gameTypeSelect.setValue("Map Game");
        gameTypeSelect.setEnabled(game == null);

        if (game == null) game = new Game();

        nameField = new TextField("Name:");
        nameField.setSuffixComponent(VaadinIcon.TEXT_LABEL.create());
        nameField.setValueChangeMode(ValueChangeMode.EAGER);
        nameField.setRequired(true);
        nameField.setRequiredIndicatorVisible(true);
        nameField.focus();

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
        var checkBoxSpan = new Span(labelIcon);
        Tooltip.forComponent(checkBoxSpan).withText("Private?");
        checkBoxSpan.addClickListener(spanClickEvent -> isPrivateCheckbox.setValue(!isPrivateCheckbox.getValue()));
        isPrivateCheckbox.setLabelComponent(checkBoxSpan);

        binder = new Binder<>(Game.class);
        binder.forField(nameField).withValidator(s -> !s.isBlank(), "Cannot be empty")
                .bind(Game::getGameName, Game::setGameName);
        binder.forField(playerCountField).asRequired("At least 2 players required")
                .bind(Game::getMaxPLayerCount, Game::setMaxPLayerCount);
        binder.forField(isPrivateCheckbox).bind(Game::isPrivate, Game::setPrivate);
        binder.setBean(game);

        var notification = new Notification("", 5000, Notification.Position.BOTTOM_CENTER);

        var saveButton = new Button("Save", VaadinIcon.CHECK_CIRCLE.create(), buttonClickEvent -> {
            try {
                if(game instanceof MapGame){
                    List<CapitalCity> capitalCities = capitalCityService.getRandomCapitalCities();
                    ((MapGame) game).setCapitalCities(capitalCities);
                    ((MapGame) game).setGameCapitalCity(capitalCities.get(0));
                    mapGameService.save((MapGame) game);

                    notification.setText("Game edited successfully");
                    notification.open();
                    close();
                    return;
                }

                if (game instanceof GuessingGame){
                    List<String> words = wordsService.getRandomWords();
                    ((GuessingGame) game).setWords(words);
                    ((GuessingGame) game).setCurrentWord(words.get(0));
                    guessingGameService.save((GuessingGame) game);

                    notification.setText("Game edited successfully");
                    notification.open();
                    close();
                    return;
                }

                switch (gameTypeSelect.getValue()) {
                    case "Map Game" -> {
                        MapGame mapGame = new MapGame();
                        mapGame.setId(UUID.randomUUID());
                        List<CapitalCity> capitalCities = capitalCityService.getRandomCapitalCities();
                        mapGame.setCapitalCities(capitalCities);
                        mapGame.setGameCapitalCity(capitalCities.get(0));
                        mapGame.setGameName(binder.getBean().getGameName());
                        mapGame.setMaxPLayerCount(binder.getBean().getMaxPLayerCount());
                        mapGame.setPrivate(binder.getBean().isPrivate());

                        mapGameService.save(mapGame);
                    }
                    case "Guessing Game" -> {
                        GuessingGame guessingGame = new GuessingGame();
                        guessingGame.setId(UUID.randomUUID());
                        List<String> words = wordsService.getRandomWords();
                        guessingGame.setWords(words);
                        guessingGame.setCurrentWord(words.get(0));
                        guessingGame.setGameName(binder.getBean().getGameName());
                        guessingGame.setMaxPLayerCount(binder.getBean().getMaxPLayerCount());
                        guessingGame.setPrivate(binder.getBean().isPrivate());

                        guessingGameService.save(guessingGame);
                    }
                    default -> gameTypeSelect.setInvalid(true);
                }


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
        saveButton.addClickShortcut(Key.ENTER);

        binder.addStatusChangeListener(statusChangeEvent -> saveButton.setEnabled(!statusChangeEvent.hasValidationErrors()
        && !nameField.getValue().isBlank()));

        var closeButton = new Button(VaadinIcon.CLOSE_BIG.create(), buttonClickEvent -> close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        var verticalLayout = new VerticalLayout(gameTypeSelect, nameField, playerCountField, isPrivateCheckbox);
        verticalLayout.setPadding(false);
        verticalLayout.setMargin(false);

        getHeader().add(closeButton);
        add(verticalLayout);
        getFooter().add(saveButton);

    }
}