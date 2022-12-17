package com.example.application.components.card;

import com.example.application.data.entity.MapGame;
import com.example.application.data.service.MapGameService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.AvatarGroup;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class Card extends VerticalLayout {

    private final MapGameService gameService;
    private MapGame mapGame;
    private int size;

    private HorizontalLayout joinLayout;

    private static String MAP_ROUTE_PREFIX = "map/";

    public Card(MapGameService gameService, MapGame mapGame, int size) {
        this.gameService = gameService;
        this.mapGame = mapGame;
        this.size = size;
        initComponents();
    }

    private void initComponents(){
        joinLayout = new HorizontalLayout();
        var joinButton = new Button("Join", VaadinIcon.SIGN_IN.create(), buttonClickEvent ->
                UI.getCurrent().navigate(MAP_ROUTE_PREFIX + mapGame.getId()));
        joinButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);

        var avatarGroup = new AvatarGroup();
        avatarGroup.setMaxItemsVisible(10);
        int colorIdx = 0;
        for (String player : mapGame.getPlayers()) {
            AvatarGroup.AvatarGroupItem avatarGroupItem = new AvatarGroup.AvatarGroupItem(player);
            avatarGroupItem.setColorIndex(colorIdx++);
            avatarGroup.add(avatarGroupItem);
        }

        joinLayout.add(joinButton);
        joinLayout.setJustifyContentMode(JustifyContentMode.END);
        joinLayout.setWidthFull();
        joinLayout.setAlignItems(Alignment.BASELINE);
        joinLayout.setSpacing(false);
        add(
                new H2(mapGame.getGameName()),
                new H4(new Span(new Label("Player: "), avatarGroup)),
                joinLayout);

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
}
