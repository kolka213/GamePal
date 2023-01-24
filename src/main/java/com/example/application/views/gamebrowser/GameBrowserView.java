package com.example.application.views.gamebrowser;

import com.example.application.components.card.Card;
import com.example.application.data.entity.GuessingGame;
import com.example.application.data.entity.MapGame;
import com.example.application.data.service.CapitalCityService;
import com.example.application.data.service.GuessingGameService;
import com.example.application.data.service.MapGameService;
import com.example.application.data.service.WordsService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.contextmenu.HasMenuItems;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.security.PermitAll;
import java.util.ArrayList;
import java.util.List;

@PageTitle("Game Browser")
@Route(value = "gamebrowser", layout = MainLayout.class)
@PermitAll
public class GameBrowserView extends VerticalLayout {

    private final MapGameService mapGameService;
    private final CapitalCityService cityService;
    private final GuessingGameService guessingGameService;

    private final WordsService wordsService;

    private List<MapGame> mapGameList;

    private List<GuessingGame> guessingGameList;

    private VerticalLayout verticalLayout;

    public GameBrowserView(MapGameService mapGameService, CapitalCityService cityService,
                           GuessingGameService guessingGameService, WordsService wordsService) {
        this.mapGameService = mapGameService;
        this.cityService = cityService;
        this.guessingGameService = guessingGameService;
        this.wordsService = wordsService;
        mapGameList = new ArrayList<>();
        guessingGameList = new ArrayList<>();

        initComponents();
    }


    private void initComponents(){
        var topButtonLayout = new HorizontalLayout();
        topButtonLayout.setWidthFull();
        topButtonLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        topButtonLayout.setPadding(false);

        var newGameButton = new Button("New Game", VaadinIcon.PLUS_CIRCLE_O.create(), buttonClickEvent -> {
            EditSession editSession = new EditSession(mapGameService, cityService, guessingGameService, wordsService);
            editSession.open();
            editSession.addDetachListener(detachEvent -> refresh());
        });

        var refreshButton = new Button(VaadinIcon.REFRESH.create(), buttonClickEvent -> refresh());
        refreshButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY);
        Tooltip.forComponent(refreshButton).setText("Refresh");

        setAlignSelf(Alignment.END, refreshButton);

        verticalLayout = new VerticalLayout();

        topButtonLayout.add(newGameButton, refreshButton);

        add(topButtonLayout, verticalLayout);

        refresh();
    }

    public void refresh() {
        verticalLayout.removeAll();
        verticalLayout.setPadding(true);

        mapGameList = mapGameService.getAll();
        guessingGameList = guessingGameService.getAll();
        createMapGameCards();
        createGuessingGameCards();

    }

    private void createMapGameCards(){
        for (MapGame mapGame : mapGameList) {
            var card = new Card(mapGameService, mapGame, mapGame.getPlayers().size());
            var settings = new MenuBar();
            settings.addThemeVariants(MenuBarVariant.LUMO_SMALL, MenuBarVariant.LUMO_TERTIARY);
            var cogWheel = createIconItem(settings, VaadinIcon.COG, null,
                    null, false, null);

            var subMenu = cogWheel.getSubMenu();

            createIconItem(subMenu, VaadinIcon.EDIT, "Edit", "", true, menuItemClickEvent -> {
                var editSession = new EditSession(mapGameService, cityService, guessingGameService, wordsService, mapGame);
                editSession.open();
                editSession.addDetachListener(detachEvent -> refresh());
            });
            createIconItem(subMenu, VaadinIcon.TRASH, "Delete", "", true, menuItemClickEvent -> {
                var confirmDialog = new ConfirmDialog("Delete Session?",
                        "Are you sure you want to delete this session?", "Delete", confirmEvent -> {
                    mapGameService.delete(mapGame);
                    refresh();
                });
                confirmDialog.setConfirmButtonTheme(ButtonVariant.LUMO_ERROR.getVariantName());
                confirmDialog.setCancelable(true);
                confirmDialog.open();
            });

            card.getJoinLayout().addComponentAsFirst(settings);

            verticalLayout.add(card);
        }
    }

    private void createGuessingGameCards(){
        for (GuessingGame guessingGame : guessingGameList) {
            var card = new Card(guessingGameService, guessingGame, guessingGame.getPlayers().size());
            var settings = new MenuBar();
            settings.addThemeVariants(MenuBarVariant.LUMO_SMALL, MenuBarVariant.LUMO_TERTIARY);
            var cogWheel = createIconItem(settings, VaadinIcon.COG, null,
                    null, false, null);

            var subMenu = cogWheel.getSubMenu();

            createIconItem(subMenu, VaadinIcon.EDIT, "Edit", "", true, menuItemClickEvent -> {
                var editSession = new EditSession(mapGameService, cityService, guessingGameService, wordsService, guessingGame);
                editSession.open();
                editSession.addDetachListener(detachEvent -> refresh());
            });
            createIconItem(subMenu, VaadinIcon.TRASH, "Delete", "", true, menuItemClickEvent -> {
                var confirmDialog = new ConfirmDialog("Delete Session?",
                        "Are you sure you want to delete this session?", "Delete", confirmEvent -> {
                    guessingGameService.delete(guessingGame);
                    refresh();
                });
                confirmDialog.setConfirmButtonTheme(ButtonVariant.LUMO_ERROR.getVariantName());
                confirmDialog.setCancelable(true);
                confirmDialog.open();
            });

            card.getJoinLayout().addComponentAsFirst(settings);

            verticalLayout.add(card);
        }
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
}
