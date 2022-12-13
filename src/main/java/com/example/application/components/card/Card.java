package com.example.application.components.card;

import com.example.application.data.entity.MapGame;
import com.example.application.data.service.MapGameService;
import com.example.application.views.gamebrowser.EditSession;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.AvatarGroup;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.HasMenuItems;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class Card extends VerticalLayout {

    private final MapGameService gameService;
    private MapGame mapGame;
    private int size;

    private static String MAP_ROUTE_PREFIX = "map/";

    public Card(MapGameService gameService, MapGame mapGame, int size) {
        this.gameService = gameService;
        this.mapGame = mapGame;
        this.size = size;
        initComponents();
    }

    private void initComponents(){
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

        var settings = new MenuBar();
        settings.addThemeVariants(MenuBarVariant.LUMO_SMALL);
        var cogWheel = createIconItem(settings, VaadinIcon.COG, null,
                null, false, null);

        var subMenu = cogWheel.getSubMenu();

        createIconItem(subMenu, VaadinIcon.EDIT, "Edit", "", true, menuItemClickEvent ->
                new EditSession(gameService, mapGame).open());
        createIconItem(subMenu, VaadinIcon.TRASH, "Delete", "", true, menuItemClickEvent ->
                gameService.delete(mapGame));

        var joinLayout = new HorizontalLayout(settings, joinButton);
        joinLayout.setJustifyContentMode(JustifyContentMode.END);
        joinLayout.setWidthFull();
        joinLayout.setAlignItems(Alignment.BASELINE);

        add(
                new H2(mapGame.getGameName()),
                new H4(new Span(new Label("Player: "), avatarGroup)),
                joinLayout);

        setStyle();
    }

    private MenuItem createIconItem(HasMenuItems menu, VaadinIcon iconName,
                                    String label, String ariaLabel, boolean isChild,
                                    ComponentEventListener<ClickEvent<MenuItem>> clickEventComponent) {
        Icon icon = new Icon(iconName);

        if (isChild) {
            icon.getStyle().set("width", "var(--lumo-icon-size-s)");
            icon.getStyle().set("height", "var(--lumo-icon-size-s)");
            icon.getStyle().set("marginRight", "var(--lumo-space-s)");
        }

        MenuItem item = menu.addItem(icon, clickEventComponent);

        if (ariaLabel != null) {
            item.getElement().setAttribute("aria-label", ariaLabel);
        }

        if (label != null) {
            item.add(new Text(label));
        }

        return item;
    }

    private void setStyle(){
        getStyle().set("border", "1px solid lightgrey");
        getStyle().set("border-radius", "var(--lumo-border-radius)");
        getStyle().set("box-shadow", "0 4px 8px 0 rgba(0, 0, 0, 0.2), 0 6px 20px 0 rgba(0, 0, 0, 0.19)");
    }
}
