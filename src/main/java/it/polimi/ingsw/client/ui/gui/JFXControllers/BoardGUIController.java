package it.polimi.ingsw.client.ui.gui.JFXControllers;

import it.polimi.ingsw.client.modelview.DepotView;
import it.polimi.ingsw.client.modelview.MatchSettings;
import it.polimi.ingsw.client.ui.controller.MainGameController;
import it.polimi.ingsw.client.ui.gui.GUIHelper;
import it.polimi.ingsw.server.model.ResourceType;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class BoardGUIController extends AbstractGUIController implements MainGameController {

    @FXML
    private ImageView strongBoxHover;
    @FXML
    private GridPane faithGrid;
    @FXML
    private HBox depot1, depot2, depot3;
    @FXML
    private VBox depotBox;
    @FXML
    private StackPane leftPane;

    @FXML
    public void initialize() {
        leftPane.getChildren().add(GameTemplate.getInstance().getTabs(ScreenName.PERSONAL_BOARD));
        updateFaithTrack();
        updateDepot();
    }

    public void goToMarket(ActionEvent actionEvent) {
        change(ScreenName.MARKET);
    }

    //TODO
    @Override
    public void handleStatusMessage(StatusMessage message) {

    }

    private void updateFaithTrack() {

        var faithImage = new ImageView(GUIHelper.getInstance().getImage(ResourceType.FAITH, 62, 62));

        var faith = GUIHelper.getInstance().getClientView().getFaithTrackView().getFaithMarker();
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

    private void updateDepot() {

        depotBox.getChildren().stream().map(e -> (HBox)e).forEach(e -> e.getChildren().clear());
        var warehouse = GUIHelper.getInstance().getClientView().getWarehouse();

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
}
