package com.example.application.components.card;

import com.example.application.data.entity.MapGame;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.AvatarGroup;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class Card extends VerticalLayout {

    private Integer roomNr;
    private MapGame mapGame;
    private int size;

    private static String MAP_ROUTE_PREFIX = "map/";

    public Card(Integer roomNr, MapGame mapGame, int size) {
        this.roomNr = roomNr;
        this.mapGame = mapGame;
        this.size = size;
        initComponents();
    }

    private void initComponents(){
        var joinButton = new Button("Join", VaadinIcon.SIGN_IN.create(), buttonClickEvent ->
                UI.getCurrent().navigate(MAP_ROUTE_PREFIX + mapGame.getId()));
        joinButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);

        AvatarGroup avatarGroup = new AvatarGroup();
        avatarGroup.setMaxItemsVisible(10);
        int colorIdx = 0;
        for (String player : mapGame.getPlayers()) {
            AvatarGroup.AvatarGroupItem avatarGroupItem = new AvatarGroup.AvatarGroupItem(player);
            avatarGroupItem.setColorIndex(colorIdx++);
            avatarGroup.add(avatarGroupItem);
        }


        add(
                new H2("Room #"+roomNr),
                new H4(new Span(new Label("Player: "), avatarGroup)),
                joinButton);
        setAlignSelf(Alignment.END, joinButton);

        setStyle();
    }

    private void setStyle(){
        getStyle().set("border", "1px solid lightgrey");
        getStyle().set("border-radius", "var(--lumo-border-radius)");
        getStyle().set("box-shadow", "0 4px 8px 0 rgba(0, 0, 0, 0.2), 0 6px 20px 0 rgba(0, 0, 0, 0.19)");
    }
}
