package it.polimi.ingsw.client.ui.gui.JFXControllers;

import it.polimi.ingsw.client.modelview.*;
import it.polimi.ingsw.client.ui.controller.UIController;
import it.polimi.ingsw.client.ui.gui.*;
import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class BoardGUIController extends AbstractGUIController implements GameGUIControllerInterface {

    @FXML
    private Button confirmDev;
    @FXML
    private ImageView strongBoxHover, inkwell;
    @FXML
    private GridPane faithGrid, productionGrid;
    @FXML
    private HBox depot1, depot2, depot3, specialDepot1, specialDepot2;
    @FXML
    private HBox topBar, tempBox;
    @FXML
    private VBox depotBox, abilityBox;
    @FXML
    private StackPane rightPane;
    @FXML
    private Rectangle tempWindow, tempBlock;
    @FXML
    private Button activateProd, end, discard, strClose;
    @FXML
    private StackPane popeTile1, popeTile2, popeTile3;
    private StackPane[] tilePane;

    @FXML
    private Pane slot1, slot2, slot3;

    @FXML
    private ScrollPane strongboxScrollPane;
    @FXML
    private GridPane strongboxGrid;
    @FXML
    private ImageView boardSelectionDialog, selectedBoardRes;
    @FXML
    private VBox boardSelectionResources;
    @FXML
    private Button selectBoardProd, selectSlot1, selectSlot2, selectSlot3, selectSpecialProd1, selectSpecialProd2;
    private Button[] prodButtons;

    @FXML
    private ImageView special1Dialog, special2Dialog;
    @FXML
    private VBox special1Resources, special2Resources;

    private PlayerView playerView;

    private Map<Id, Resource> resChoiceMap;

    @FXML
    public void initialize() {

        GUIHelper.getInstance().setCurrentScreen(ScreenName.PERSONAL_BOARD);

        GameTemplate.getInstance().setTabs(ScreenName.PERSONAL_BOARD);
        rightPane.getChildren().add(GameTemplate.getInstance().getPlayersTabs());

        setPlayerView(GUIHelper.getInstance().getClientView());

        activateTurn(GUIHelper.getInstance().getTurn());

        GameLog.getInstance().setLog(rightPane);

        synchronized (GUIMessageHandler.getInstance()) {
            GUIMessageHandler.getInstance().setCurrentGameController(this);
            GUIMessageHandler.getInstance().notifyAll();
        }

        tilePane = new StackPane[]{popeTile1, popeTile2, popeTile3};
        prodButtons = new Button[]{selectBoardProd, selectSlot1, selectSlot2, selectSlot3, selectSpecialProd1, selectSpecialProd2};

        screenStart();

        updateFaithTrack();
        updateDepot();

        update();
    }

    private void setDepot() {
        DragNDrop.getInstance().setDepotDraggable(depot1, Id.DEPOT_1, true);
        DragNDrop.getInstance().setDepotDraggable(depot2, Id.DEPOT_2, true);
        DragNDrop.getInstance().setDepotDraggable(depot3, Id.DEPOT_3, true);

        DragNDrop.getInstance().setDroppable(depot1, Id.DEPOT_1);
        DragNDrop.getInstance().setDroppable(depot2, Id.DEPOT_2);
        DragNDrop.getInstance().setDroppable(depot3, Id.DEPOT_3);

        if (GUIHelper.getInstance().activeSpecialDepot(playerView, 0)) {
            if (((ExtraDepotAbility) playerView.getLeaderCards().get(0).getSpecialAbility()).getType() == playerView.getWarehouse().get(3).getResource().getResourceType() ) {
                DragNDrop.getInstance().setDepotDraggable(specialDepot1, Id.S_DEPOT_1, true);
                DragNDrop.getInstance().setDroppable(specialDepot1, Id.S_DEPOT_1);
            }
            else {
                DragNDrop.getInstance().setDepotDraggable(specialDepot1, Id.S_DEPOT_2, true);
                DragNDrop.getInstance().setDroppable(specialDepot1, Id.S_DEPOT_2);
            }
        }

        if (GUIHelper.getInstance().activeSpecialDepot(playerView, 1)) {
            if (((ExtraDepotAbility) playerView.getLeaderCards().get(1).getSpecialAbility()).getType() == playerView.getWarehouse().get(3).getResource().getResourceType() ) {
                DragNDrop.getInstance().setDepotDraggable(specialDepot2, Id.S_DEPOT_1, true);
                DragNDrop.getInstance().setDroppable(specialDepot2, Id.S_DEPOT_1);
            }
            else {
                DragNDrop.getInstance().setDepotDraggable(specialDepot2, Id.S_DEPOT_2, true);
                DragNDrop.getInstance().setDroppable(specialDepot2, Id.S_DEPOT_2);
            }
        }
    }

    public void screenStart() {
        setDepot();
        disableNode(strongboxScrollPane);
        disableNode(boardSelectionDialog);
        disableNode(boardSelectionResources);

        disableNode(special1Dialog);
        disableNode(special2Dialog);
        disableNode(special1Resources);
        disableNode(special2Resources);

        if (GUIHelper.getInstance().isChoosingTemp()) {
            showTemp();
        }
        resChoiceMap = new HashMap<>();

        if (GUIHelper.getInstance().getCurrAction() == CurrAction.SELECTED_SLOT) startDevResSelection();

        SelectedProductions.getInstance().adjust();

        Stream.of(prodButtons).forEach(this::disableNode);
    }

    private void showTemp() {

        tempBox.getChildren().clear();

        tempWindow.setOpacity(1);
        tempBlock.setOpacity(1);
        tempBox.setOpacity(1);
        discard.setOpacity(1);
        discard.setDisable(false);

        tempBox.setOpacity(1);

        if (GUIHelper.getInstance().getClientView().getTempResources() != null) {
            for (var r : GUIHelper.getInstance().getClientView().getTempResources()) {

                for (int i = 0; i < r.getQuantity(); i++) {
                    var res = new ImageView(GUIHelper.getInstance().getImage(r.getResourceType(), 120, 120));
                    tempBox.getChildren().add(res);
                    DragNDrop.getInstance().setDraggableResource(res, true);
                }

            }
            if (GUIHelper.getInstance().getClientView().getTempResources().isEmpty()) update();
        }
    }

    public void setPlayerView(PlayerView playerView) {
        this.playerView = playerView;
    }

    @Override
    public void handleStatusMessage(StatusMessage message) {

    }

    public void updateFaithTrack() {

        var faithImage = new ImageView(GUIHelper.getInstance().getImage(ResourceType.FAITH, GUISizes.get().faithTrack(), GUISizes.get().faithTrack()));

        var faith = playerView.getFaithTrackView().getFaithMarker();
        faithGrid.getChildren().clear();

        if (GUIHelper.getInstance().isSolo()) {
            var blackCross = new ImageView(new Image("assets/game/black_cross.png", GUISizes.get().faithTrack(), GUISizes.get().faithTrack(), false, false));
            var lorenzoFaith = GameView.getInstance().getBlackCross();
            GUIHelper.getInstance().placeFaithTrack(lorenzoFaith, getFaithGrid(), blackCross);

        }

        GUIHelper.getInstance().placeFaithTrack(faith, faithGrid, faithImage);

        var tiles = playerView.getFaithTrackView().getPopeFavorTiles();

        Stream.of(tilePane).forEach(e -> e.getChildren().clear());

        for (int i = 0; i < 3; i++) {
            var im = GUIHelper.getInstance().getImage(tiles[i], i);
            if (im != null) tilePane[i].getChildren().add(new ImageView(im));
        }
    }

    public void updateDepot() {

        depotBox.getChildren().stream().map(e -> (HBox)e).forEach(e -> e.getChildren().clear());
        specialDepot1.getChildren().clear();
        specialDepot2.getChildren().clear();

        var warehouse = playerView.getWarehouse();

        for (int j = 0; j < 3; j++) {
            var d = warehouse.get(j);
            if (d.getResource() != null) {
                var depot = (HBox) depotBox.getChildren().get(d.getId().getValue());

                for (int i = 0; i < d.getResource().getQuantity(); i++) {
                    var img = new ImageView(GUIHelper.getInstance().getImage(d.getResource().getResourceType(), GUISizes.get().depotRes(), GUISizes.get().depotRes()));
                    img.setPreserveRatio(true);
                    depot.getChildren().add(img);
                }

            }
        }

        if (warehouse.size() > 3) {
            int index1, index2;

            if (GUIHelper.getInstance().abilityCorresponds(warehouse.get(3), playerView.getLeaderCards().get(0))) {
                index1 = 3;
                index2 = 4;
            }
            else {
                index1 = 4;
                index2 = 3;
            }

            if(GUIHelper.getInstance().activeSpecialDepot(playerView, 0)){
                for (int i = 0; i < warehouse.get(index1).getResource().getQuantity(); i++) {
                    var img = new ImageView(GUIHelper.getInstance().getImage(warehouse.get(index1).getResource().getResourceType(), GUISizes.get().depotRes(), GUISizes.get().depotRes()));
                    img.setPreserveRatio(true);
                    specialDepot1.getChildren().add(img);
                }
            }

            if(GUIHelper.getInstance().activeSpecialDepot(playerView, 1)) {
                for (int i = 0; i < warehouse.get(index2).getResource().getQuantity(); i++) {
                    var img = new ImageView(GUIHelper.getInstance().getImage(warehouse.get(index2).getResource().getResourceType(), GUISizes.get().depotRes(), GUISizes.get().depotRes()));
                    img.setPreserveRatio(true);
                    specialDepot2.getChildren().add(img);
                }
            }
        }



        if (playerView.equals(GUIHelper.getInstance().getClientView())) {
            if (GUIHelper.getInstance().isChoosingTemp()) {

                if (GUIHelper.getInstance().getClientView().getTempResources() != null && GUIHelper.getInstance().getClientView().getTempResources().isEmpty()) {
                    tempWindow.setOpacity(0);
                    tempBlock.setOpacity(0);
                    tempBox.setOpacity(0);
                    discard.setOpacity(0);
                    discard.setDisable(true);

                    tempBox.setOpacity(0);

                    GUIHelper.getInstance().setChoosingTemp(false);
                }

                else {
                    showTemp();
                }
            }
        }

        if(GUIHelper.getInstance().getTurn())
            end.setDisable(!(!GUIHelper.getInstance().getClientView().isMainAction() && GUIHelper.getInstance().getTurn()));
    }

    public void updateStrongbox() {

        if (GUIHelper.getInstance().getCurrAction() != CurrAction.SELECTED_SLOT) {

            strongboxGrid.getChildren().clear();
            strongboxGrid.add(strClose, 5, 0);

            var strongbox = playerView.getStrongboxView();

            var coins = strongbox.getCoin();
            var shields = strongbox.getShield();
            var servants = strongbox.getServant();
            var stones = strongbox.getStone();

            int i = 0;

            for(int counter = 0, j = 0; counter < coins.getQuantity(); counter++, j++) {
                if (j == GUISizes.get().strongboxCols() || (j==5 && i==0)) {
                    j = 0;
                    i++;
                }
                var im = new ImageView(GUIHelper.getInstance().getImage(ResourceType.COIN, GUISizes.get().strongboxRes(), GUISizes.get().strongboxRes()));
                GridPane.setValignment(im, VPos.CENTER);
                GridPane.setHalignment(im, HPos.CENTER);
                strongboxGrid.add(im, j, i);

            }

            if (coins.getQuantity() > 0) i++;

            for(int counter = 0, j = 0; counter < shields.getQuantity(); counter++, j++) {
                if (j == GUISizes.get().strongboxCols() || (j==5 && i==0)) {
                    j = 0;
                    i++;
                }
                var im = new ImageView(GUIHelper.getInstance().getImage(ResourceType.SHIELD, GUISizes.get().strongboxRes(), GUISizes.get().strongboxRes()));
                GridPane.setValignment(im, VPos.CENTER);
                GridPane.setHalignment(im, HPos.CENTER);
                strongboxGrid.add(im, j, i);

            }

            if (shields.getQuantity() > 0) i++;

            for(int counter = 0, j = 0; counter < servants.getQuantity(); counter++, j++) {
                if (j == GUISizes.get().strongboxCols() || (j==5 && i==0)) {
                    j = 0;
                    i++;
                }
                var im = new ImageView(GUIHelper.getInstance().getImage(ResourceType.SERVANT, GUISizes.get().strongboxRes(), GUISizes.get().strongboxRes()));
                GridPane.setValignment(im, VPos.CENTER);
                GridPane.setHalignment(im, HPos.CENTER);
                strongboxGrid.add(im, j, i);

            }

            if (servants.getQuantity() > 0) i++;

            for(int counter = 0, j = 0; counter < stones.getQuantity(); counter++, j++) {
                if (j == GUISizes.get().strongboxCols() || (j==5 && i==0)) {
                    j = 0;
                    i++;
                }
                var im = new ImageView(GUIHelper.getInstance().getImage(ResourceType.STONE, GUISizes.get().strongboxRes(), GUISizes.get().strongboxRes()));
                GridPane.setValignment(im, VPos.CENTER);
                GridPane.setHalignment(im, HPos.CENTER);
                strongboxGrid.add(im, j, i);

            }

        }
    }

    public void strBoxHighlight(MouseEvent mouseEvent) {
        strongBoxHover.setOpacity(1);
    }

    public void strBoxHighlightExit(MouseEvent mouseEvent) {
        strongBoxHover.setOpacity(0);
    }

    @Override
    public void goToMarket() {
        change(ScreenName.MARKET);
    }

    @Override
    public void goToClientBoard() { }

    @Override
    public void goToDev() {
        change(ScreenName.DEV_MARKET);
    }

    @Override
    public void goToOtherBoard(ActionEvent e) {
        GUIHelper.getInstance().setSelectedPlayer(e.getSource());
        change(ScreenName.OTHER_BOARD);
    }

    @Override
    public void update() {
        Platform.runLater(() -> {
            activateTurn(GUIHelper.getInstance().getTurn());
            if(GUIHelper.getInstance().getTurn())
                end.setDisable(!(!GUIHelper.getInstance().getClientView().isMainAction() && GUIHelper.getInstance().getTurn()));

            if (GUIHelper.getInstance().isChoosingTemp()) showTemp();

            updateDevSlots();
            updateAbilityBox();
            updateProductions();
            updateDepot();
            updateStrongbox();
            updateFaithTrack();

            if (GUIHelper.getInstance().getCurrAction() == CurrAction.SELECTED_SLOT) showStrongbox(null);
            SelectedProductions.getInstance().update();
        });
    }

    public void updateDevSlots() {
        Platform.runLater(() -> {
            var slots = playerView.getSlots();

            productionGrid.getChildren().clear();

            if (playerView.getNickname().equals(MatchSettings.getInstance().getClientNickname())) {
                productionGrid.add(selectSlot1, 0, 0);
                productionGrid.add(selectSlot2, 1, 0);
                productionGrid.add(selectSlot3, 2, 0);
            }

            for(int i = 0; i < 3; i++) {
                var slot = ((DevelopmentCardSlotView)slots.get(i+1)).getSlot();
                if (!slot.isEmpty()) {
                    int offset = 0;

                    for (DevelopmentCard card : slot) {
                        var image = new ImageView(GUIHelper.getInstance().getImage(card, GUISizes.get().devSize(), GUISizes.get().devSizeY()));
                        var effect = new DropShadow();
                        image.setEffect(effect);
                        GridPane.setMargin(image, new Insets(0, 0, offset, 0));
                        GridPane.setValignment(image, VPos.CENTER);
                        GridPane.setHalignment(image, HPos.CENTER);
                        productionGrid.add(image, i, 0);

                        offset += GUISizes.get().devOffset();
                    }
                }

            }

            if (GUIHelper.getInstance().getCurrAction() != CurrAction.SELECTED_SLOT && playerView.equals(GUIHelper.getInstance().getClientView())) {
                confirmDev.setDisable(true);
                confirmDev.setOpacity(0);
            }

            if (playerView.getNickname().equals(MatchSettings.getInstance().getClientNickname())) {
                productionGrid.add(slot1, 0, 0);
                productionGrid.add(slot2, 1, 0);
                productionGrid.add(slot3, 2, 0);

                slot1.setDisable(true);
                slot2.setDisable(true);
                slot3.setDisable(true);
            }

            if (GUIHelper.getInstance().getCurrAction() == CurrAction.SELECTING_SLOT) {
                slot1.setDisable(false);
                slot2.setDisable(false);
                slot3.setDisable(false);
            }

        });


    }

    public void updateProductions() {
        if (prodButtons == null) prodButtons = new Button[]{selectBoardProd, selectSlot1, selectSlot2, selectSlot3, selectSpecialProd1, selectSpecialProd2};
        if (playerView.isMainAction()) {
            var productions = SelectedProductions.getInstance().getProductions();


            for (int i =0; i < productions.size(); i++) {
                var p = productions.get(i);
                enableNode(prodButtons[i]);
                if (p == ProductionState.IDLE) {
                    prodButtons[i].setStyle("");
                    prodButtons[i].getStyleClass().add("darkButton");
                    prodButtons[i].setText("Select");}
                else if (p == ProductionState.SELECTED) {
                    prodButtons[i].setText("✓");
                    prodButtons[i].getStyleClass().add("darkButton");
                }
                else if (p == ProductionState.CONFIRMED) {
                    prodButtons[i].setText("✓");
                    prodButtons[i].setStyle("-fx-background-color: rgba(77, 125, 42, 1)");
                    prodButtons[i].setDisable(true);
                }
                else disableNode(prodButtons[i]);
            }

            if (SelectedProductions.getInstance().get(0) == ProductionState.CONFIRMED) {
                disableNode(boardSelectionDialog);
                disableNode(boardSelectionResources);
            }
            if (SelectedProductions.getInstance().get(4) == ProductionState.CONFIRMED) {
                disableNode(special1Resources);
                disableNode(special1Dialog);
            }
            if (SelectedProductions.getInstance().get(5) == ProductionState.CONFIRMED) {
                disableNode(special2Dialog);
                disableNode(special2Resources);
            }

            activateProd.setDisable(productions.stream().noneMatch(e -> e == ProductionState.CONFIRMED));
        }
        else {
            Stream.of(prodButtons).forEach(this::disableNode);
        }
    }

    public void updateAbilityBox() {
        Platform.runLater(() -> {
            var cards = playerView.getLeaderCards();

            var box1 = (HBox)abilityBox.getChildren().get(0);
            var box2 = (HBox)abilityBox.getChildren().get(1);

            box1.getChildren().clear();
            box2.getChildren().clear();

            box1.getChildren().add(selectSpecialProd1);
            box2.getChildren().add(selectSpecialProd2);

            if (cards != null) {
                if (cards.size() > 0 && cards.get(0) != null && cards.get(0).isActive()) {
                    var im = new ImageView(GUIHelper.getInstance().getAbilityImageFromLeader(cards.get(0)));
                    var effect = new DropShadow();
                    im.setEffect(effect);
                    box1.getChildren().add(im);
                }
                if (cards.size() > 1 && cards.get(1) != null && cards.get(1).isActive()) {
                    var im = new ImageView(GUIHelper.getInstance().getAbilityImageFromLeader(cards.get(1)));
                    var effect = new DropShadow();
                    im.setEffect(effect);
                    box2.getChildren().add(im);
                }
            }
        });

    }

    private void activateTurn(boolean turn) {
        activateProd.setDisable(!turn);
        if(turn) inkwell.setOpacity(1);
        else inkwell.setOpacity(0);
    }

    public void goToLeader(ActionEvent actionEvent) {
        GUIHelper.getInstance().setScreenshot(GUIHelper.getInstance().getCurrentScene().snapshot(null));
        change(ScreenName.LEADER);
    }

    public void showStrongbox(MouseEvent mouseEvent) {
        enableNode(strongboxScrollPane);
        updateStrongbox();
    }

    public void discardResources(ActionEvent actionEvent) {
        tempWindow.setOpacity(0);
        tempBlock.setOpacity(0);
        tempBox.setOpacity(0);
        discard.setOpacity(0);
        discard.setDisable(true);

        tempBox.setOpacity(0);

        GUIHelper.getInstance().setChoosingTemp(false);
        UIController.getInstance().throwResources();

        update();
    }

    public void endTurn(ActionEvent actionEvent) {
        UIController.getInstance().endTurn();
        SelectedProductions.getInstance().reset();
    }

    public void lightSlot(MouseEvent mouseEvent) {
        if (GUIHelper.getInstance().getCurrAction() == CurrAction.SELECTING_SLOT) ((Pane)mouseEvent.getSource()).setStyle("-fx-background-color: rgba(245, 179, 66, 0.5)");
    }

    public void turnOffSlot(MouseEvent mouseEvent) {
        if (GUIHelper.getInstance().getCurrAction() == CurrAction.SELECTING_SLOT) ((Pane)mouseEvent.getSource()).setStyle("-fx-background-color: transparent");
    }

    public void selectedSlot1(MouseEvent mouseEvent) {
        if(GUIHelper.getInstance().getCurrAction() == CurrAction.SELECTING_SLOT) {
            GUIHelper.getInstance().setCurrAction(CurrAction.SELECTED_SLOT);
            ((Pane)mouseEvent.getSource()).setStyle("-fx-background-color: transparent");
            GUIHelper.getInstance().setSelectedSlot(Id.SLOT_1);
            if (GUIHelper.getInstance().getCurrAction() == CurrAction.SELECTED_SLOT)
                UIController.getInstance().selectDevelopmentCard(GUIHelper.getInstance().getSelectedI(), GUIHelper.getInstance().getSelectedJ(), Id.SLOT_1);
        }
    }

    public void selectedSlot2(MouseEvent mouseEvent) {
        if(GUIHelper.getInstance().getCurrAction() == CurrAction.SELECTING_SLOT) {
            GUIHelper.getInstance().setCurrAction(CurrAction.SELECTED_SLOT);
            ((Pane)mouseEvent.getSource()).setStyle("-fx-background-color: transparent");
            GUIHelper.getInstance().setSelectedSlot(Id.SLOT_2);
            if (GUIHelper.getInstance().getCurrAction() == CurrAction.SELECTED_SLOT)
                UIController.getInstance().selectDevelopmentCard(GUIHelper.getInstance().getSelectedI(), GUIHelper.getInstance().getSelectedJ(), Id.SLOT_2);
        }
    }

    public void selectedSlot3(MouseEvent mouseEvent) {
        if(GUIHelper.getInstance().getCurrAction() == CurrAction.SELECTING_SLOT) {
            GUIHelper.getInstance().setCurrAction(CurrAction.SELECTED_SLOT);
            ((Pane)mouseEvent.getSource()).setStyle("-fx-background-color: transparent");
            GUIHelper.getInstance().setSelectedSlot(Id.SLOT_3);
            if (GUIHelper.getInstance().getCurrAction() == CurrAction.SELECTED_SLOT)
                UIController.getInstance().selectDevelopmentCard(GUIHelper.getInstance().getSelectedI(), GUIHelper.getInstance().getSelectedJ(), Id.SLOT_3);
        }
    }

    public void startDevResSelection() {
        Modify.makeDepotResourcesSelectable(depotBox, resChoiceMap, true);

        makeSpecialSelectable();

        updateStrongbox();
        if (strongboxGrid.getChildren().size() > 1) {

            Modify.makeStrongboxSelectable(strongboxGrid, resChoiceMap, true);
            showStrongbox(null);
        }
        confirmDev.setDisable(false);
        confirmDev.setOpacity(1);
    }

    public void confirmDevRes(ActionEvent actionEvent) {
        GUIHelper.getInstance().setCurrAction(CurrAction.DEV_CONFIRMATION);
        UIController.getInstance().depositResourcesIntoSlot(GUIHelper.getInstance().getSelectedSlot(), resChoiceMap);
    }

    public void closeStrongBox(ActionEvent actionEvent) {
        disableNode(strongboxScrollPane);
    }

    public void resBoardSelected(MouseEvent mouseEvent) {
        boardSelectionResources.getChildren().forEach(im -> im.setOpacity(0.5));
        ((ImageView)mouseEvent.getSource()).setOpacity(1);
        selectedBoardRes = (ImageView)mouseEvent.getSource();
    }

    public void selectBoardProduction(ActionEvent actionEvent) {
        var state = SelectedProductions.getInstance().get(0);

        if (state == ProductionState.IDLE) {
            resChoiceMap.clear();
            Modify.makeDepotResourcesSelectable(depotBox, resChoiceMap, true);
            showStrongbox(null);
            Modify.makeStrongboxSelectable(strongboxGrid, resChoiceMap, true);
            makeSpecialSelectable();
            enableNode(boardSelectionDialog, 0.75);
            enableNode(boardSelectionResources);
            SelectedProductions.getInstance().set(0, ProductionState.SELECTED);
            updateProductions();
        }

        else if (state == ProductionState.SELECTED) {
            if (selectedBoardRes != null) {
                UIController.getInstance().depositResourcesIntoSlot(Id.BOARD_PRODUCTION,
                        resChoiceMap,
                        GUIHelper.getInstance().getResFromImage(selectedBoardRes.getImage()).getResourceType(),
                        false);
            }

        }
    }

    public void selectSlot1Production(ActionEvent actionEvent) {
        var state = SelectedProductions.getInstance().get(1);

        if (state == ProductionState.IDLE) {
            resChoiceMap.clear();
            Modify.makeDepotResourcesSelectable(depotBox, resChoiceMap, true);
            showStrongbox(null);
            Modify.makeStrongboxSelectable(strongboxGrid, resChoiceMap, true);
            makeSpecialSelectable();
            SelectedProductions.getInstance().set(1, ProductionState.SELECTED);
            updateProductions();
        }

        else if (state == ProductionState.SELECTED) {
            UIController.getInstance().depositResourcesIntoSlot(Id.SLOT_1,
                    resChoiceMap,
                    ResourceType.JOLLY,
                    false);
        }
    }

    public void selectSlot2Production(ActionEvent actionEvent) {
        var state = SelectedProductions.getInstance().get(2);

        if (state == ProductionState.IDLE) {
            resChoiceMap.clear();
            Modify.makeDepotResourcesSelectable(depotBox, resChoiceMap, true);
            showStrongbox(null);
            Modify.makeStrongboxSelectable(strongboxGrid, resChoiceMap, true);
            makeSpecialSelectable();
            SelectedProductions.getInstance().set(2, ProductionState.SELECTED);
            updateProductions();
        }

        else if (state == ProductionState.SELECTED) {
            UIController.getInstance().depositResourcesIntoSlot(Id.SLOT_2,
                    resChoiceMap,
                    ResourceType.JOLLY,
                    false);
        }
    }

    public void selectSlot3Production(ActionEvent actionEvent) {
        var state = SelectedProductions.getInstance().get(3);

        if (state == ProductionState.IDLE) {
            resChoiceMap.clear();
            Modify.makeDepotResourcesSelectable(depotBox, resChoiceMap, true);
            showStrongbox(null);
            Modify.makeStrongboxSelectable(strongboxGrid, resChoiceMap, true);
            makeSpecialSelectable();
            SelectedProductions.getInstance().set(3, ProductionState.SELECTED);
            updateProductions();
        }

        else if (state == ProductionState.SELECTED) {
            UIController.getInstance().depositResourcesIntoSlot(Id.SLOT_3,
                    resChoiceMap,
                    ResourceType.JOLLY,
                    false);
        }
    }

    public void selectSpecialProd1(ActionEvent actionEvent) {
        var state = SelectedProductions.getInstance().get(4);

        if (state == ProductionState.IDLE) {
            resChoiceMap.clear();
            Modify.makeDepotResourcesSelectable(depotBox, resChoiceMap, true);
            showStrongbox(null);
            Modify.makeStrongboxSelectable(strongboxGrid, resChoiceMap, true);
            makeSpecialSelectable();
            enableNode(special1Dialog, 0.75);
            enableNode(special1Resources);
            SelectedProductions.getInstance().set(4, ProductionState.SELECTED);
            updateProductions();
        }

        else if (state == ProductionState.SELECTED) {
            if (selectedBoardRes != null) {
                UIController.getInstance().depositResourcesIntoSlot(Id.S_SLOT_1,
                        resChoiceMap,
                        GUIHelper.getInstance().getResFromImage(selectedBoardRes.getImage()).getResourceType(),
                        false);
            }

        }
    }

    public void selectSpecialProd2(ActionEvent actionEvent) {
        var state = SelectedProductions.getInstance().get(5);

        if (state == ProductionState.IDLE) {
            resChoiceMap.clear();
            Modify.makeDepotResourcesSelectable(depotBox, resChoiceMap, true);
            showStrongbox(null);
            Modify.makeStrongboxSelectable(strongboxGrid, resChoiceMap, true);
            makeSpecialSelectable();
            enableNode(special2Dialog, 0.75);
            enableNode(special2Resources);
            SelectedProductions.getInstance().set(5, ProductionState.SELECTED);
            updateProductions();
        }

        else if (state == ProductionState.SELECTED) {
            if (selectedBoardRes != null) {
                UIController.getInstance().depositResourcesIntoSlot(Id.SLOT_2,
                        resChoiceMap,
                        GUIHelper.getInstance().getResFromImage(selectedBoardRes.getImage()).getResourceType(),
                        false);
            }

        }
    }

    public void activateProduction(ActionEvent actionEvent) {
        UIController.getInstance().activateProduction();
    }

    public void confirmBoardProduction(ActionEvent actionEvent) {
    }

    public GridPane getFaithGrid() {
        return faithGrid;
    }

    public PlayerView getPlayerView() {
        return playerView;
    }

    public StackPane[] getTilePane() {
        return tilePane;
    }

    public void openMenu(ActionEvent actionEvent) {
        GUIHelper.getInstance().setScreenshot(GUIHelper.getInstance().getCurrentScene().snapshot(null));
        change(ScreenName.GAME_MENU);
    }

    public void makeSpecialSelectable() {
        if (GUIHelper.getInstance().activeSpecialDepot(playerView, 0)) {
            if (playerView.getWarehouse().get(3).getResource().getResourceType() == ((ExtraDepotAbility) playerView.getLeaderCards().get(0).getSpecialAbility()).getType())
                Modify.makeSpecialDepotSelectable(specialDepot1, specialDepot2, resChoiceMap, true);
            else Modify.makeSpecialDepotSelectable(specialDepot2, specialDepot1, resChoiceMap, true);
        }
        else if (GUIHelper.getInstance().activeSpecialDepot(playerView, 1)) {
            if (playerView.getWarehouse().get(3).getResource().getResourceType() == ((ExtraDepotAbility) playerView.getLeaderCards().get(1).getSpecialAbility()).getType())
                Modify.makeSpecialDepotSelectable(specialDepot2, specialDepot1, resChoiceMap, true);
            else Modify.makeSpecialDepotSelectable(specialDepot1, specialDepot2, resChoiceMap, true);
        }
    }
}
