package com.example.application.views.guess;

import com.example.application.data.entity.GuessingGame;
import com.example.application.data.entity.Players;
import com.example.application.data.service.GuessingGameService;
import com.example.application.data.service.PlayersService;
import com.example.application.security.SecurityService;
import com.example.application.views.MainLayout;
import com.example.application.views.gamebrowser.GameBrowserView;
import com.vaadin.collaborationengine.*;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.router.*;
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

    private VerticalLayout gameBoardLayout;

    private CollaborationBinder<GuessingGame> binder;

    private CollaborationAvatarGroup avatarGroup;

    private CollaborationMessageList list;

    public GuessingGameView(GuessingGameService gameService, PlayersService playersService, SecurityService securityService) {
        this.gameService = gameService;
        this.playersService = playersService;
        this.securityService = securityService;
        initGame();
        setSize();
    }

    private void initGame() {
        this.userInfo = new UserInfo(String.valueOf(securityService.getAuthenticatedUser().hashCode()), null);

        this.gameBoardLayout = new VerticalLayout();
        this.gameBoardLayout.setSizeFull();

        var splitLayout = new SplitLayout();
        splitLayout.addToPrimary(this.gameBoardLayout);
        splitLayout.setOrientation(SplitLayout.Orientation.VERTICAL);
        splitLayout.setSizeFull();

        this.avatarGroup = new CollaborationAvatarGroup(userInfo, null);
        this.avatarGroup.setOwnAvatarVisible(true);

        this.binder = new CollaborationBinder<>(GuessingGame.class, userInfo);

        this.list = new CollaborationMessageList(userInfo, null);
        this.list.setWidthFull();
        this.list.addClassNames("chat-view-message-list");

        var input = new CollaborationMessageInput(list);
        input.addClassNames("chat-view-message-input");
        input.setWidthFull();

        var verticalLayout = new VerticalLayout(avatarGroup, list, input);
        verticalLayout.setSizeFull();
        verticalLayout.expand(list);
        splitLayout.addToSecondary(verticalLayout);

        add(splitLayout);
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

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        UserDetails user = securityService.getAuthenticatedUser();
        Optional<UUID> guessingGameID = beforeEnterEvent.getRouteParameters().get(GUESS_ID).map(UUID::fromString);
        if (guessingGameID.isPresent()) {
            Optional<GuessingGame> guessingGame = gameService.get(guessingGameID.get());
            guessingGame.ifPresentOrElse(game -> {
                this.guessingGame = game;
                player = playersService.save(user.getUsername(), game);
                gameService.addPlayer(this.guessingGame, player);
                userInfo.setName(user.getUsername());

                String topic = this.guessingGame.getId().toString();
                avatarGroup.setTopic(topic);
                binder.setTopic(topic, () -> this.guessingGame);
                list.setTopic(topic);

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
