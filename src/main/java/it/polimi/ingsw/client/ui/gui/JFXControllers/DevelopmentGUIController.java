package it.polimi.ingsw.client.ui.gui.JFXControllers;

import it.polimi.ingsw.client.ui.gui.GUIHelper;
import it.polimi.ingsw.client.ui.gui.GameLog;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextFlow;

public class DevelopmentGUIController extends AbstractGUIController implements GameGUIControllerInterface{

    @FXML
    private GridPane devGrid;
    @FXML
    private TextFlow log;
    @FXML
    private StackPane rightPane, centerPane;

    @FXML
    public void initialize() {
        GUIHelper.getInstance().setCurrentGameController(this);
        GameTemplate.getInstance().setTabs(ScreenName.DEV_MARKET);
        rightPane.getChildren().add(GameTemplate.getInstance().getPlayersTabs());
        rightPane.getChildren().add(GameTemplate.getInstance().getMarketsTabs());

        centerPane.getChildren().add(GUIHelper.getInstance().getDevVisualizer());

        updateDevelopment();


    }

    @Override
    public void handleStatusMessage(StatusMessage message) {

    }

    @Override
    public void goToMarket() {
        change(ScreenName.MARKET);
    }

    @Override
    public void goToClientBoard() {
        change(ScreenName.PERSONAL_BOARD);
    }

    @Override
    public void goToDev() { }

    @Override
    public void goToOtherBoard(ActionEvent e) {
        GUIHelper.getInstance().setSelectedPlayer(e.getSource());
        change(ScreenName.OTHER_BOARD);
    }

    public void updateDevelopment() {

        GUIHelper.getInstance().updateDevGrid(devGrid);

    }
}
