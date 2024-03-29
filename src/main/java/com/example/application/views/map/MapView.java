package com.example.application.views.map;

import com.example.application.components.custommarker.CustomMarker;
import com.example.application.data.entity.CapitalCity;
import com.example.application.data.entity.MapGame;
import com.example.application.data.entity.Players;
import com.example.application.data.service.CapitalCityService;
import com.example.application.data.service.MapGameService;
import com.example.application.data.service.PlayersService;
import com.example.application.helper.CoordinateHelper;
import com.example.application.helper.State;
import com.example.application.security.SecurityService;
import com.example.application.views.MainLayout;
import com.example.application.views.gamebrowser.GameBrowserView;
import com.vaadin.collaborationengine.*;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.map.Map;
import com.vaadin.flow.component.map.configuration.Coordinate;
import com.vaadin.flow.component.map.configuration.feature.MarkerFeature;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.router.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;

import javax.annotation.security.PermitAll;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

@PageTitle("Map")
@Route(value = "map/:mapGameID?", layout = MainLayout.class)
@PermitAll
public class MapView extends VerticalLayout implements BeforeEnterObserver, BeforeLeaveObserver {

    private final SecurityService securityService;
    private final MapGameService mapGameService;
    private final PlayersService playersService;
    private final CapitalCityService cityService;
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

    private CustomMarker cityFeature;

    private State state = State.RUNNING;

    private int waitCounter = 5;

    private Notification cityNameNotification;

    private CapitalCity capitalCity;

    private  Iterator<java.util.Map.Entry<String, Coordinate>> cityIterator;


    public MapView(SecurityService securityService, MapGameService mapGameService, PlayersService playersService,
                   CapitalCityService cityService) {
        this.securityService = securityService;
        this.mapGameService = mapGameService;
        this.playersService = playersService;
        this.cityService = cityService;
        this.playerPositions = new HashMap<>();
        initGame();
        addClassName("chat-view");
    }

    private void initGame(){
        map = new Map();

        cityNameNotification = new Notification();

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
        if (state.equals(State.RUNNING)){
            map.addClickEventListener(mapClickEvent -> {
                if (mapClickEvent.getFeatures().isEmpty()) {
                    if (!map.getFeatureLayer().getFeatures().isEmpty() && playerPositions.get(userInfo.getName()) != null) {
                        map.getFeatureLayer().removeFeature(playerPositions.get(userInfo.getName()));
                    }

                    player.setCoordinate(mapClickEvent.getCoordinate());
                    playersService.update(player);
                    playerPositions.put(userInfo.getName(), new CustomMarker(mapClickEvent.getCoordinate(), userInfo.getName(),
                            MarkerFeature.PIN_ICON));

                    map.getFeatureLayer().addFeature(playerPositions.get(userInfo.getName() ));
                }
            });
        }

        map.addFeatureClickListener(mapFeatureClickEvent -> {
            CustomMarker feature = (CustomMarker) mapFeatureClickEvent.getFeature();
            var tooltipDialog = getTooltipDialog(feature.getCoordinates(), feature.getUserName());
            tooltipDialog.open();
        });
    }

    private Dialog getTooltipDialog(Coordinate coordinate, String userName){
        var tooltipDialog = new Dialog();

        Button closeButton = new Button(VaadinIcon.CLOSE_BIG.create(), buttonClickEvent -> tooltipDialog.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        tooltipDialog.setHeaderTitle(String.format("%s's Marker", userName));
        tooltipDialog.getHeader().add(closeButton);
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
    public void gameCycle(){
        if (mapGame != null) {
            fetchOpponentMarkers();

            if (opponentPlayers != null){
                clearPlayersFromMapWhoLeft();
                addOpponentMarkersToMap();
                waitForAllPlayerTurns();
                createNextRound();
            }
        }
    }

    public void fetchOpponentMarkers() {
        opponentPlayers = playersService.fetchAllPlayersFromGame(mapGame)
                .stream()
                .filter(players -> !players.getPlayerName().equals(userInfo.getName()))
                .toList();

    }

    private void waitForAllPlayerTurns() {
        if (playerPositions.size() > 1){
            boolean noNullValues = playerPositions.values().stream().noneMatch(Objects::isNull);
            if (noNullValues) {
                putResultMarkerOnMap();
            }
        }
    }

    private void putResultMarkerOnMap(){
        if (Objects.requireNonNull(state) == State.RUNNING) {
            CapitalCity city = capitalCity;
            Coordinate coordinates = new Coordinate(city.getLongitude(), city.getLatitude());
            cityFeature = new CustomMarker(coordinates, city.getName(), MarkerFeature.PIN_ICON);
            map.getUI().ifPresent(ui -> ui.access(() -> map.getFeatureLayer().addFeature(cityFeature)));
            calculateWinnerOfRound();
        }
    }

    private void calculateWinnerOfRound() {
        if (state == State.RUNNING) {
            CapitalCity city = capitalCity;
            Coordinate cityCoordinates = new Coordinate(city.getLongitude(), city.getLatitude());

            HashMap<Players, Double> resultsList = new HashMap<>();
            resultsList.put(player, CoordinateHelper.measureDistanceBetweenTwoPoints(cityCoordinates,
                    player.getCoordinate()));

            opponentPlayers.forEach(players -> {
                resultsList.put(players, CoordinateHelper.measureDistanceBetweenTwoPoints(cityCoordinates,
                        players.getCoordinate()));
            });

            Double minValue = Collections.min(resultsList.values());

            NumberFormat nf = NumberFormat.getNumberInstance();
            nf.setMaximumFractionDigits(1);
            String valueFormatted = nf.format(minValue);

            resultsList.forEach((key, value) -> {
                if (value.equals(minValue)) {
                    getUI().ifPresent(ui -> ui.access(() -> {
                        Notification.show(String.format("Winner: %s with a distance of %s km.", key.getPlayerName(), valueFormatted),
                                5000, Notification.Position.BOTTOM_CENTER);
                    }));
                }
            });

            state = State.WAITING;
        }
    }


    private void addOpponentMarkersToMap() {
        map.getUI().ifPresent(ui -> ui.access(() ->{
            opponentPlayers.stream()
                    .filter(opponentPlayer -> opponentPlayer.getCoordinate() != null)
                    .forEach(opponentPlayer -> {
                                if (playerPositions.containsKey(opponentPlayer.getPlayerName())) {
                                    map.getFeatureLayer().removeFeature(playerPositions.get(opponentPlayer.getPlayerName()));
                                }
                                playerPositions.put(opponentPlayer.getPlayerName(), new CustomMarker(opponentPlayer.getCoordinate(),
                                        opponentPlayer.getPlayerName(), MarkerFeature.POINT_ICON));
                                map.getFeatureLayer().addFeature(playerPositions.get(opponentPlayer.getPlayerName()));
                            });
        }));


    }

    private void clearPlayersFromMapWhoLeft(){
        Set<String> playerNames = opponentPlayers.stream().map(Players::getPlayerName).collect(Collectors.toSet());
        Set<String> keysNotInPlayers = playerPositions
                .keySet()
                .stream()
                .filter(key -> !playerNames.contains(key))
                .collect(Collectors.toSet());

        keysNotInPlayers.forEach(key -> map.getUI().ifPresent(ui -> ui.access(() -> {
            if (!key.equals(userInfo.getName())) map.getFeatureLayer().removeFeature(playerPositions.get(key));
        })));
    }

    private void createNextRound() {
        if (state == State.WAITING) {
             getUI().ifPresent(ui -> ui.access(() -> {
                    Notification.show("Next round in " + waitCounter + " seconds", 2000,
                            Notification.Position.BOTTOM_START);
                }));

            if (waitCounter == 0) {
                map.getUI().ifPresent(ui -> ui.access(() -> {
                    map.getFeatureLayer().removeFeature(cityFeature);
                }));

                resetPlayerPositions();
                updateNewCityName();

                state = State.RUNNING;
                waitCounter = 5;
            }
            waitCounter--;
        }
    }

    private void updateNewCityName() {
        java.util.Map.Entry<String, Coordinate> entry = cityIterator.next();
        capitalCity = cityService.findCityByName(entry.getKey());
        mapGame.setGameCapitalCity(capitalCity);
        mapGameService.save(mapGame);

        getUI().ifPresent(ui -> ui.access(() ->{
            cityNameNotification.close();
            cityNameNotification = new Notification("City: " + capitalCity.getName());
            cityNameNotification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
            cityNameNotification.setPosition(Notification.Position.BOTTOM_CENTER);
            cityNameNotification.open();
        }));
    }

    private void resetPlayerPositions() {
        opponentPlayers = null;
        map.getUI().ifPresent(ui -> ui.access(() -> {
            playerPositions.keySet().forEach(player -> 
                    map.getFeatureLayer().removeFeature(playerPositions.get(player)));
        }));

        player.setCoordinate(null);
        playersService.update(player);

        playerPositions.clear();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        UserDetails user = securityService.getAuthenticatedUser();
        Optional<UUID> mapGameID = beforeEnterEvent.getRouteParameters().get(MAPGAME_ID).map(UUID::fromString);
        if (mapGameID.isPresent()){
            Optional<MapGame> mapGame = mapGameService.get(mapGameID.get());
            mapGame.ifPresentOrElse(game -> {
                userInfo.setName(user.getUsername());
                this.gameId = game.getId();
                this.mapGame = game;
                if (this.mapGame.getPlayers().stream().noneMatch(players -> players.getPlayerName().equals(user.getUsername()))){
                    player = playersService.save(user.getUsername(), null, this.mapGame);
                    mapGameService.addPlayer(this.mapGame, this.player);
                }

                this.player = playersService.getPlayerByName(user.getUsername());

                playerPositions.put(userInfo.getName(), null);

                cityIterator = game.getCapitalCities().entrySet().iterator();

                capitalCity = game.getGameCapitalCity();

                String topic = gameId.toString();
                avatarGroup.setTopic(topic);
                binder.setTopic(topic, () -> game);
                list.setTopic(topic);

                cityNameNotification.setText("City: "+capitalCity.getName());
                cityNameNotification.setPosition(Notification.Position.BOTTOM_CENTER);
                cityNameNotification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);

                UI.getCurrent().add(cityNameNotification);
                cityNameNotification.open();
            }, () -> noGameFoundDialog().open());
        }
        else {
            noGameFoundDialog().open();
        }
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent beforeLeaveEvent) {
        if (this.mapGame != null) {
            playersService.delete(this.player);
        }
        getUI().ifPresent(ui -> ui.access(() -> cityNameNotification.close()));
    }
}
