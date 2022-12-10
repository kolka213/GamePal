package com.example.application.views.gamebrowser;

import com.example.application.components.card.Card;
import com.example.application.data.entity.MapGame;
import com.example.application.data.service.MapGameService;
import com.example.application.security.SecurityService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.security.PermitAll;
import java.util.List;

@PageTitle("Game Browser")
@Route(value = "gamebrowser", layout = MainLayout.class)
@PermitAll
public class GameBrowserView extends VerticalLayout {

    private final SecurityService securityService;
    private final MapGameService mapGameService;

    private final String user;

    public GameBrowserView(SecurityService securityService, MapGameService mapGameService) {
        this.securityService = securityService;
        this.mapGameService = mapGameService;
        this.user = securityService.getAuthenticatedUser().getUsername();

        initComponents();
    }

    private void initComponents() {
        List<MapGame> allGames = mapGameService.getAll();
        for (int i = 0; i < allGames.size(); i++) {
            MapGame mapGame = allGames.get(i);
            add(new Card(i, mapGame, mapGame.getPlayers().size()));
        }
        setPadding(true);
    }
}
