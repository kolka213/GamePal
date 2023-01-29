package com.example.application.components.card;

import com.example.application.data.entity.GuessingGame;
import com.example.application.data.entity.MapGame;
import com.example.application.data.entity.Players;
import com.example.application.data.service.GuessingGameService;
import com.example.application.data.service.MapGameService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.avatar.AvatarGroup;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.data.value.ValueChangeMode;

public class Card extends VerticalLayout {

    private MapGameService gameService;
    private GuessingGameService guessingGameService;
    private MapGame mapGame;

    private GuessingGame guessingGame;
    private int size;

    private Span gameTypeSpan;

    private Span isPrivateSpan;

    private H3 gameTitle;

    private HorizontalLayout joinLayout;

    private Integer currentPlayerCount;

    private Integer maxPlayerCount;

    private static String MAP_ROUTE_PREFIX = "map/";

    private static String GUESS_ROUTE_PREFIX = "guessing/";

    private Button joinButton;

    private PasswordField passwordField;

    public Card(MapGameService gameService, MapGame mapGame, int size) {
        this.gameService = gameService;
        this.mapGame = mapGame;
        this.size = size;

        this.gameTypeSpan = new Span("Map Game");
        this.isPrivateSpan = new Span("Private");
        this.isPrivateSpan.setVisible(mapGame.isPrivate());
        this.gameTitle = new H3(mapGame.getGameName());
        this.currentPlayerCount = mapGame.getPlayers().size();
        this.maxPlayerCount = mapGame.getMaxPLayerCount();
        this.joinButton = new Button("Join", VaadinIcon.SIGN_IN.create(), buttonClickEvent -> {
            if (this.mapGame.getPlayers().size() < this.mapGame.getMaxPLayerCount()) {
                if (!this.mapGame.isPrivate()) {
                    UI.getCurrent().navigate(MAP_ROUTE_PREFIX + mapGame.getId());
                }
                passwordField.setVisible(true);
                joinButton.setEnabled(false);
                if (passwordField.getValue().equals(mapGame.getPassword())) {
                    UI.getCurrent().navigate(MAP_ROUTE_PREFIX + mapGame.getId());
                }
                passwordField.setInvalid(true);
            }
            joinButton.setVisible(this.mapGame.getPlayers().size() < this.mapGame.getMaxPLayerCount());
        });
        initComponents();
    }

    public Card(GuessingGameService guessingGameService, GuessingGame guessingGame, int size) {
        this.guessingGameService = guessingGameService;
        this.guessingGame = guessingGame;
        this.size = size;

        this.gameTypeSpan = new Span("Guessing Game");
        this.isPrivateSpan = new Span("Private");
        this.isPrivateSpan.setVisible(guessingGame.isPrivate());
        this.gameTitle = new H3(guessingGame.getGameName());
        this.currentPlayerCount = guessingGame.getPlayers().size();
        this.maxPlayerCount = guessingGame.getMaxPLayerCount();
        this.joinButton = new Button("Join", VaadinIcon.SIGN_IN.create(), buttonClickEvent -> {
            if (this.guessingGame.getPlayers().size() < this.guessingGame.getMaxPLayerCount()) {

                if (!this.guessingGame.isPrivate()) {
                    UI.getCurrent().navigate(GUESS_ROUTE_PREFIX + guessingGame.getId());
                }
                passwordField.setVisible(true);
                joinButton.setEnabled(false);
                if (passwordField.getValue().equals(guessingGame.getPassword())) {
                    UI.getCurrent().navigate(GUESS_ROUTE_PREFIX + guessingGame.getId());
                }
                passwordField.setInvalid(true);
            }
            joinButton.setVisible(this.guessingGame.getPlayers().size() < this.guessingGame.getMaxPLayerCount());
        });
        initComponents();
    }

    private void initComponents(){
        joinLayout = new HorizontalLayout();
        joinButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);

        var avatarGroup = new AvatarGroup();
        avatarGroup.setMaxItemsVisible(10);
        int colorIdx = 0;

        if(mapGame != null) {
            for (Players player : mapGame.getPlayers()) {
                AvatarGroup.AvatarGroupItem avatarGroupItem = new AvatarGroup.AvatarGroupItem(player.getPlayerName());
                avatarGroupItem.setColorIndex(colorIdx++);
                avatarGroup.add(avatarGroupItem);
            }
        }

        if (guessingGame != null){
            for (Players player : guessingGame.getPlayers()) {
                AvatarGroup.AvatarGroupItem avatarGroupItem = new AvatarGroup.AvatarGroupItem(player.getPlayerName());
                avatarGroupItem.setColorIndex(colorIdx++);
                avatarGroup.add(avatarGroupItem);
            }
        }

        gameTitle.setSizeUndefined();

        gameTypeSpan.getElement().getThemeList().add("badge pill");
        gameTypeSpan.setSizeUndefined();

        isPrivateSpan.getElement().getThemeList().add("badge contrast pill");

        joinLayout.add(joinButton);
        joinLayout.setJustifyContentMode(JustifyContentMode.END);
        joinLayout.setWidthFull();
        joinLayout.setAlignItems(Alignment.BASELINE);
        joinLayout.setSpacing(false);

        passwordField = getPasswordField();
        passwordField.setVisible(false);

        var headerLayout = new HorizontalLayout(gameTitle, gameTypeSpan, isPrivateSpan);
        headerLayout.setMaxHeight(50f, Unit.PIXELS);
        headerLayout.setAlignItems(Alignment.BASELINE);
        headerLayout.setSpacing(false);
        add(
                headerLayout,
                new H4(new Span(new Label(String.format("Player (%s/%s):", currentPlayerCount,
                        maxPlayerCount)), avatarGroup)),
                joinLayout,
                passwordField);

        setAlignSelf(Alignment.END, passwordField);

        setStyle();
    }

    public HorizontalLayout getJoinLayout() {
        return joinLayout;
    }

    private void setStyle(){
        getStyle().set("border", "1px solid lightgrey");
        getStyle().set("border-radius", "var(--lumo-border-radius)");
        getStyle().set("box-shadow", "0 4px 8px 0 rgba(0, 0, 0, 0.2), 0 6px 20px 0 rgba(0, 0, 0, 0.19)");
    }

    private PasswordField getPasswordField(){
        var passwordField = new PasswordField();
        passwordField.setRequired(true);
        passwordField.setPrefixComponent(VaadinIcon.LOCK.create());
        passwordField.focus();
        passwordField.setErrorMessage("Wrong password or empty.");
        if (passwordField.isVisible()){
            passwordField.addValueChangeListener(event -> joinButton.setEnabled(!event.getValue().isBlank()));
            passwordField.setValueChangeMode(ValueChangeMode.EAGER);
        }
        return passwordField;
    }
}
