package com.example.application.views.guess;

import com.example.application.components.characterbox.CharBox;
import com.example.application.data.entity.GuessingGame;
import com.example.application.data.entity.Players;
import com.example.application.data.service.GuessingGameService;
import com.example.application.data.service.PlayersService;
import com.example.application.helper.State;
import com.example.application.security.SecurityService;
import com.example.application.views.MainLayout;
import com.example.application.views.gamebrowser.GameBrowserView;
import com.vaadin.collaborationengine.*;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.router.*;
import org.apache.commons.text.WordUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;

import javax.annotation.security.PermitAll;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@PageTitle("Guess")
@Route(value = "guessing/:guessingGameID?", layout = MainLayout.class)
@PermitAll
public class GuessingGameView extends VerticalLayout implements BeforeEnterObserver, BeforeLeaveObserver {

    private static String GUESS_ID = "guessingGameID";

    private final GuessingGameService gameService;

    private final PlayersService playersService;

    private final SecurityService securityService;

    private UserInfo userInfo;

    private Players player;

    private GuessingGame guessingGame;

    private List<Players> opponentPlayers;

    private HorizontalLayout gameBoardLayout;

    private CollaborationBinder<GuessingGame> binder;

    private CollaborationAvatarGroup avatarGroup;

    private CollaborationMessageList list;

    private CollaborationMessageInput input;

    private Notification winnerNotification;

    private State state;
    private int waitCounter = 5;

    private int nextCharVisibleCounter = 0;

    public GuessingGameView(GuessingGameService gameService, PlayersService playersService, SecurityService securityService) {
        this.gameService = gameService;
        this.playersService = playersService;
        this.securityService = securityService;
        initGame();
        setSize();
    }

    private void initGame() {
        this.userInfo = new UserInfo(String.valueOf(securityService.getAuthenticatedUser().hashCode()), null);

        this.state = State.RUNNING;

        this.gameBoardLayout = new HorizontalLayout();
        this.gameBoardLayout.setAlignItems(Alignment.START);
        this.gameBoardLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        this.gameBoardLayout.setPadding(true);
        this.gameBoardLayout.setSizeUndefined();

        var splitLayout = new SplitLayout();
        splitLayout.addToPrimary(this.gameBoardLayout);

        splitLayout.setOrientation(SplitLayout.Orientation.VERTICAL);
        splitLayout.setSizeFull();
        splitLayout.setSplitterPosition(60.0);

        this.avatarGroup = new CollaborationAvatarGroup(userInfo, null);
        this.avatarGroup.setOwnAvatarVisible(true);

        this.binder = new CollaborationBinder<>(GuessingGame.class, userInfo);

        this.list = new CollaborationMessageList(userInfo, null);
        this.list.setWidthFull();
        this.list.addClassNames("chat-view-message-list");

        input = new CollaborationMessageInput(list);
        input.addClassNames("chat-view-message-input");
        input.setWidthFull();

        var verticalLayout = new VerticalLayout(avatarGroup, list, input);
        verticalLayout.setSizeFull();
        verticalLayout.expand(list);
        splitLayout.addToSecondary(verticalLayout);

        checkWordBeforeSend();

        add(splitLayout);
    }

    private void createWordBoxes() {
        String currentWord = WordUtils.capitalizeFully(guessingGame.getCurrentWord());
        CharBox firstLetter = new CharBox(currentWord.charAt(0));
        firstLetter.setVisible(true);

        this.gameBoardLayout.add(firstLetter);
        for (int i = 1; i < currentWord.length(); i++) {
            this.gameBoardLayout.add(new CharBox(currentWord.charAt(i)));
        }
    }


    private void setSize(){
        setSizeFull();
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
    private void gameCycle(){
        if (this.guessingGame != null){
            switch (state) {
                case RUNNING -> {
                    fetchOpponentPlayers();
                    showCardsVisiblePeriodically();
                    checkIfAnyPlayerWon();
                }
                case FINISHED -> {
                    showFullWord();
                }
                case WAITING -> {
                    createNextRound();
                }
            }
        }
    }

    private void showCardsVisiblePeriodically(){
        int timeframe = 60 / (this.guessingGame.getCurrentWord().length() - 1);
        if (this.nextCharVisibleCounter > timeframe){
            nextCharVisibleCounter = 0;
            this.gameBoardLayout.getUI().ifPresent(ui -> ui.access(() -> {
                this.gameBoardLayout.getChildren()
                        .filter(component -> !component.isVisible())
                        .findAny()
                        .ifPresent(component -> component.setVisible(true));
            }));
        }
        nextCharVisibleCounter++;
    }

    private void resetGame(){
        int index = this.guessingGame.getWords().indexOf(this.guessingGame.getCurrentWord());
        this.guessingGame.setCurrentWord(this.guessingGame.getWords().get(index + 1));
        this.gameService.update(this.guessingGame);

        this.player.setGuessed(false);
        this.playersService.update(player);

        this.opponentPlayers = null;

        this.gameBoardLayout.getUI().ifPresent(ui -> ui.access(() -> {
            this.gameBoardLayout.removeAll();
            createWordBoxes();
        }));
    }

    private void createNextRound() {
        getUI().ifPresent(ui -> ui.access(() -> {
            Notification.show("Next round in " + waitCounter + " seconds", 2000,
                    Notification.Position.BOTTOM_START);
        }));

        if (waitCounter == 0) {
            resetGame();
            this.state = State.RUNNING;
            this.waitCounter = 5;
        }
        this.waitCounter--;

    }

    private void checkIfAnyPlayerWon() {
        if (this.opponentPlayers.stream().anyMatch(Players::hasGuessed)){
            Players winner = this.opponentPlayers.stream().filter(Players::hasGuessed).findFirst().get();
            showWinnerNotification(winner);
            state = State.FINISHED;
        }
    }

    private void showWinnerNotification(Players player) {
        getUI().ifPresent(ui -> ui.access(() -> {
            this.winnerNotification = new Notification(String.format("%s has guessed the word.", player.getPlayer()));
            this.winnerNotification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
            this.winnerNotification.setPosition(Notification.Position.BOTTOM_CENTER);
            this.winnerNotification.setDuration(5000);
            this.winnerNotification.open();
        }));
    }

    private void fetchOpponentPlayers() {
        this.opponentPlayers = playersService.fetchAllPlayersFromGame(this.guessingGame);
    }

    private void checkWordBeforeSend(){
        this.input.getContent().addSubmitListener(submitEvent -> {
            if (submitEvent.getValue().toLowerCase().equals(this.guessingGame.getCurrentWord())){
                this.player.setGuessed(true);
                this.playersService.update(player);
            }
        });
    }

    private void showFullWord(){
        getUI().ifPresent(ui -> ui.access(() -> {
            this.gameBoardLayout.getChildren().forEach(component -> component.setVisible(true));
        }));
        state = State.WAITING;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        UserDetails user = securityService.getAuthenticatedUser();
        Optional<UUID> guessingGameID = beforeEnterEvent.getRouteParameters().get(GUESS_ID).map(UUID::fromString);
        if (guessingGameID.isPresent()) {
            Optional<GuessingGame> guessingGame = gameService.get(guessingGameID.get());
            guessingGame.ifPresentOrElse(game -> {
                this.guessingGame = game;
                if (this.guessingGame.getPlayers().stream().noneMatch(players -> players.getPlayer().equals(user.getUsername()))){
                    player = playersService.save(user.getUsername(), this.guessingGame);
                    gameService.addPlayer(this.guessingGame, this.player);
                }
                this.userInfo.setName(user.getUsername());

                String topic = this.guessingGame.getId().toString();
                this.avatarGroup.setTopic(topic);
                this.binder.setTopic(topic, () -> this.guessingGame);
                this.list.setTopic(topic);
                createWordBoxes();

            }, () -> noGameFoundDialog().open());
        }
        else {
            noGameFoundDialog().open();
        }
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent beforeLeaveEvent) {
        if (guessingGame != null){
            gameService.removePlayer(guessingGame, player);
        }
    }
}
