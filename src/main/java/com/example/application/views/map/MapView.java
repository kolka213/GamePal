package com.example.application.views.map;

import com.example.application.data.entity.MapGame;
import com.example.application.data.entity.Players;
import com.example.application.data.service.MapGameService;
import com.example.application.data.service.PlayersService;
import com.example.application.security.SecurityService;
import com.example.application.views.MainLayout;
import com.example.application.views.gamebrowser.GameBrowserView;
import com.vaadin.collaborationengine.*;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.map.Map;
import com.vaadin.flow.component.map.configuration.Coordinate;
import com.vaadin.flow.component.map.configuration.feature.MarkerFeature;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.router.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;

import javax.annotation.security.PermitAll;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@PageTitle("Map")
@Route(value = "map/:mapGameID?", layout = MainLayout.class)
@PermitAll
public class MapView extends VerticalLayout implements BeforeEnterObserver, BeforeLeaveObserver {

    private final SecurityService securityService;
    private final MapGameService mapGameService;
    private final PlayersService playersService;
    private Map map;
    private CollaborationBinder<MapGame> binder;

    private CollaborationAvatarGroup avatarGroup;

    private CollaborationMessageList list;

    private static String MAPGAME_ID = "mapGameID";

    private UUID gameId;

    private UserInfo userInfo;

    private Players player;

    private MapGame mapGame;

    private List<Players> opponentPlayers;

    private HashMap<String, MarkerFeature> playerPositions;


    public MapView(SecurityService securityService, MapGameService mapGameService, PlayersService playersService) {
        this.securityService = securityService;
        this.mapGameService = mapGameService;
        this.playersService = playersService;
        this.playerPositions = new HashMap<>();
        initGame();
        addClassName("chat-view");
    }

    private void initGame(){
        map = new Map();

        var splitLayout = new SplitLayout();
        splitLayout.addToPrimary(map);
        splitLayout.setOrientation(SplitLayout.Orientation.HORIZONTAL);
        splitLayout.setSizeFull();
        splitLayout.setSplitterPosition(90.0);

        userInfo = new UserInfo(String.valueOf(securityService.getAuthenticatedUser().hashCode()), null);
        avatarGroup = new CollaborationAvatarGroup(userInfo, null);
        binder = new CollaborationBinder<>(MapGame.class, userInfo);

        list = new CollaborationMessageList(userInfo, null);

        list.setWidthFull();
        list.addClassNames("chat-view-message-list");

        CollaborationMessageInput input = new CollaborationMessageInput(list);
        input.addClassNames("chat-view-message-input");
        input.setWidthFull();

        avatarGroup.setOwnAvatarVisible(true);

        var verticalLayout = new VerticalLayout(avatarGroup, list, input);
        verticalLayout.setSizeFull();
        verticalLayout.expand(list);
        splitLayout.addToSecondary(verticalLayout);

        add(splitLayout);

        setSize();
        setEventListeners();
    }

    private void setSize(){
        setHeight(100f, Unit.PERCENTAGE);
        map.setHeight(100f, Unit.PERCENTAGE);
    }

    private void setEventListeners(){
        map.addClickEventListener(mapClickEvent -> {
            if (mapClickEvent.getFeatures().isEmpty()) {
                if (!map.getFeatureLayer().getFeatures().isEmpty() && playerPositions.get(userInfo.getName()) != null) {
                    map.getFeatureLayer().removeFeature(playerPositions.get(userInfo.getName()));
                }

                player.setCoordinate(mapClickEvent.getCoordinate());
                playersService.update(player);
                playerPositions.put(userInfo.getName(), new MarkerFeature(mapClickEvent.getCoordinate()));

                map.getFeatureLayer().addFeature(playerPositions.get(userInfo.getName() ));
            }
            else {
                var tooltipDialog = getTooltipDialog(mapClickEvent.getCoordinate());
                tooltipDialog.open();
            }
        });
    }

    private Dialog getTooltipDialog(Coordinate coordinate){
        var tooltipDialog = new Dialog();
        tooltipDialog.setHeaderTitle("Marker");
        tooltipDialog.getHeader().add(new Button(VaadinIcon.CLOSE_BIG.create(), buttonClickEvent -> tooltipDialog.close()));
        tooltipDialog.setCloseOnEsc(false);
        tooltipDialog.setCloseOnOutsideClick(false);

        var coordLayoutY = new HorizontalLayout(
                new H5(new Label("Latitude:")),
                new Label(String.valueOf(coordinate.getY()))
        );
        coordLayoutY.setJustifyContentMode(JustifyContentMode.BETWEEN);
        coordLayoutY.setAlignItems(Alignment.BASELINE);

        var coordLayoutX = new HorizontalLayout(
                new H5(new Label("Longitude:")),
                new Label(String.valueOf(coordinate.getX()))
        );
        coordLayoutX.setJustifyContentMode(JustifyContentMode.BETWEEN);
        coordLayoutX.setAlignItems(Alignment.BASELINE);

        tooltipDialog.add(coordLayoutY, coordLayoutX);

        return tooltipDialog;
    }

    private ConfirmDialog noGameFoundDialog(){
        var dialog = new ConfirmDialog();
        dialog.setCloseOnEsc(false);
        dialog.setCancelable(false);
        dialog.setHeader("Oops..");

        var verticalLayout = new VerticalLayout();
        verticalLayout.add(new Label("Looks like the game has ended"));
        dialog.addConfirmListener(confirmEvent -> UI.getCurrent().navigate(GameBrowserView.class));
        dialog.add(verticalLayout);

        return dialog;
    }

    @Scheduled(fixedRate = 1000)
    public void fetchOpponentMarkers() {
        if (mapGame != null) {
            opponentPlayers = playersService.fetchAllPlayersFromGame(mapGame)
                    .stream()
                    .filter(players -> !players.getPlayer().equals(userInfo.getName()))
                    .toList();
            addOpponentMarkersToMap();
        }
    }

    private void addOpponentMarkersToMap(){
        if (opponentPlayers != null){
            opponentPlayers.stream()
                    .filter(opponentPlayer -> opponentPlayer.getCoordinate() != null)
                    .forEach(opponentPlayer -> map.getUI()
                    .ifPresent(ui -> ui.access(() -> {
                        if (playerPositions.containsKey(opponentPlayer.getPlayer())) {
                            map.getFeatureLayer().removeFeature(playerPositions.get(opponentPlayer.getPlayer()));
                        }
                        playerPositions.put(opponentPlayer.getPlayer(), new MarkerFeature(opponentPlayer.getCoordinate()));
                        map.getFeatureLayer().addFeature(playerPositions.get(opponentPlayer.getPlayer()));
                    })));
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        UserDetails user = securityService.getAuthenticatedUser();
        Optional<UUID> mapGameID = beforeEnterEvent.getRouteParameters().get(MAPGAME_ID).map(UUID::fromString);
        if (mapGameID.isPresent()){
            Optional<MapGame> mapGame = mapGameService.get(mapGameID.get());
            mapGame.ifPresentOrElse(game -> {
                player = playersService.save(user.getUsername(), null, game);
                mapGameService.addPlayer(game, player);
                userInfo.setName(user.getUsername());
                this.gameId = game.getId();
                this.mapGame = game;
                playerPositions.put(userInfo.getName(), null);

                String topic = gameId.toString();
                avatarGroup.setTopic(topic);
                binder.setTopic(topic, () -> game);
                list.setTopic(topic);
            }, () -> noGameFoundDialog().open());
        }
        else {
            noGameFoundDialog().open();
        }
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent beforeLeaveEvent) {
        Optional<MapGame> mapGame = mapGameService.get(gameId);
        mapGame.ifPresent(game -> mapGameService.removePlayer(game, player));
    }
}
