package it.polimi.ingsw.client.ui.gui.JFXControllers;

import it.polimi.ingsw.client.modelview.DepotView;
import it.polimi.ingsw.client.modelview.PlayerView;
import it.polimi.ingsw.client.ui.gui.GUIHelper;
import it.polimi.ingsw.client.ui.gui.GameLog;
import it.polimi.ingsw.server.model.ResourceType;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;

public class BoardGUIController extends AbstractGUIController implements GameGUIControllerInterface {

    @FXML
    private ImageView strongBoxHover;
    @FXML
    private GridPane faithGrid;
    @FXML
    private HBox depot1, depot2, depot3;
    @FXML
    private VBox depotBox;
    @FXML
    private StackPane rightPane;
    @FXML
    private TextFlow log;

    private PlayerView playerView;

    @FXML
    public void initialize() {

        GUIHelper.getInstance().setCurrentGameController(this);
        GameTemplate.getInstance().setTabs(ScreenName.PERSONAL_BOARD);
        rightPane.getChildren().add(GameTemplate.getInstance().getPlayersTabs());
        rightPane.getChildren().add(GameTemplate.getInstance().getMarketsTabs());

        setPlayerView(GUIHelper.getInstance().getClientView());

        updateFaithTrack();
        updateDepot();

        screenStart();
    }

    public void screenStart() { }

    public void setPlayerView(PlayerView playerView) {
        this.playerView = playerView;
    }

    public void goToMarket(ActionEvent actionEvent) {
        change(ScreenName.MARKET);
    }

    //TODO
    @Override
    public void handleStatusMessage(StatusMessage message) {

    }

    public void updateFaithTrack() {

        var faithImage = new ImageView(GUIHelper.getInstance().getImage(ResourceType.FAITH, 62, 62));

        var faith = playerView.getFaithTrackView().getFaithMarker();
        faithGrid.getChildren().clear();

        if (faith <= 2) faithGrid.add(faithImage, faith, 2);
        else if (faith == 3) faithGrid.add(faithImage, 2, 1);
        else if (faith <= 9) faithGrid.add(faithImage, faith-2, 0);
        else if (faith == 10) faithGrid.add(faithImage, 7, 1);
        else if (faith <= 16) faithGrid.add(faithImage, faith-4, 2);
        else if (faith == 17) faithGrid.add(faithImage, 12, 1);
        else if (faith <= 24) faithGrid.add(faithImage, faith-6, 0);
        else throw new RuntimeException("Faith out of bounds");
    }

    public void updateDepot() {

        depotBox.getChildren().stream().map(e -> (HBox)e).forEach(e -> e.getChildren().clear());
        var warehouse = playerView.getWarehouse();

        for (DepotView d : warehouse) {

            if (d.getResource() != null) {
                var depot = (HBox) depotBox.getChildren().get(d.getId().getValue());

                for (int i = 0; i < d.getResource().getQuantity(); i++) {
                    var img = new ImageView(GUIHelper.getInstance().getImage(d.getResource().getResourceType(), 62, 62));
                    img.setPreserveRatio(true);
                    depot.getChildren().add(img);
                }

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

    public void goToLeader(ActionEvent actionEvent) {
        GUIHelper.getInstance().setScreenshot(GUIHelper.getInstance().getCurrentScene().snapshot(null));
        change(ScreenName.LEADER);
    }
}
