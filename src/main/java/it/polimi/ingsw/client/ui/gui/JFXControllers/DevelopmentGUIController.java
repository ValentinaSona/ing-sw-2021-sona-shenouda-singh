package it.polimi.ingsw.client.ui.gui.JFXControllers;

import it.polimi.ingsw.client.ui.gui.GUIHelper;
import it.polimi.ingsw.client.ui.gui.GameLog;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerStartTurnMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
    private Button end;

    @FXML
    public void initialize() {
        GUIHelper.getInstance().setCurrentGameController(this);
        GameTemplate.getInstance().setTabs(ScreenName.DEV_MARKET);
        rightPane.getChildren().add(GameTemplate.getInstance().getPlayersTabs());

        centerPane.getChildren().add(GUIHelper.getInstance().getDevVisualizer());

        updateDevelopment();

        update();
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

    @Override
    public void update() {
        //activateTurn(GUIHelper.getInstance().getTurn());
        if(GUIHelper.getInstance().getTurn())
            end.setDisable(GUIHelper.getInstance().getClientView().isMainAction());
    }

    public void updateDevelopment() {

        GUIHelper.getInstance().updateDevGrid(devGrid);

    }
}
