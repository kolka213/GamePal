package com.example.application.views.map;

import com.example.application.data.entity.MapGame;
import com.example.application.data.service.MapGameService;
import com.example.application.security.SecurityService;
import com.example.application.views.MainLayout;
import com.vaadin.collaborationengine.*;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
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
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.security.PermitAll;
import java.util.Optional;
import java.util.UUID;

@PageTitle("Map")
@Route(value = "map", layout = MainLayout.class)
@PermitAll
public class MapView extends VerticalLayout {

    private final SecurityService securityService;
    private final MapGameService mapGameService;
    private Map map;
    private CollaborationBinder<MapGame> binder;

    public MapView(SecurityService securityService, MapGameService mapGameService) {
        this.securityService = securityService;
        this.mapGameService = mapGameService;
        initComponents();
        setSize();
        setEventListeners();

    }

    private void initComponents(){
        map = new Map();
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.addToPrimary(map);
        splitLayout.setOrientation(SplitLayout.Orientation.HORIZONTAL);
        splitLayout.setSizeFull();
        splitLayout.setSplitterPosition(90.0);

        UserInfo userInfo = new UserInfo(String.valueOf(securityService.getAuthenticatedUser().hashCode()),
                securityService.getAuthenticatedUser().getUsername());
        binder = new CollaborationBinder<>(MapGame.class, userInfo);
        CollaborationAvatarGroup avatarGroup = new CollaborationAvatarGroup(userInfo, null);


        String topic;
        Optional<MapGame> mapGame = mapGameService.get(UUID.fromString("f6f0e684-40e2-4131-b3bc-70e7bc4edb6b"));
        if (mapGame.isPresent()){
            topic = mapGame.get().getId().toString();
            binder.setTopic(topic, () -> mapGame.get());
            avatarGroup.setTopic(topic);
        }

        CollaborationMessageList list = new CollaborationMessageList(userInfo, "chat/"+mapGame.get().getId().toString());
        list.setWidthFull();
        list.addClassNames("chat-view-message-list");

        CollaborationMessageInput input = new CollaborationMessageInput(list);
        input.addClassNames("chat-view-message-input");
        input.setWidthFull();

        var verticalLayout = new VerticalLayout(avatarGroup, list, input);
        verticalLayout.setSizeFull();
        verticalLayout.expand(list);
        splitLayout.addToSecondary(verticalLayout);

        add(splitLayout);
    }

    private void setSize(){
        setHeight(100f, Unit.PERCENTAGE);
        map.setHeight(100f, Unit.PERCENTAGE);
    }

    private void setEventListeners(){
        map.addClickEventListener(mapClickEvent -> {
            if (mapClickEvent.getFeatures().isEmpty()) {
                if (!map.getFeatureLayer().getFeatures().isEmpty()) map.getFeatureLayer().removeFeature(map.getFeatureLayer().getFeatures().get(0));

                map.getFeatureLayer().addFeature(new MarkerFeature(mapClickEvent.getCoordinate()));
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
}
