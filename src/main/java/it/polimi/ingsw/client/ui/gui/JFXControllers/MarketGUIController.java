package it.polimi.ingsw.client.ui.gui.JFXControllers;

import it.polimi.ingsw.client.ui.gui.GUIHelper;
import it.polimi.ingsw.client.ui.gui.GameLog;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextFlow;

public class MarketGUIController extends AbstractGUIController implements GameGUIControllerInterface {

    @FXML
    private StackPane rightPane;
    @FXML
    private StackPane centerPane;
    @FXML
    private TextFlow log;

    @FXML
    public void initialize() {

        GUIHelper.getInstance().setCurrentGameController(this);
        //GameLog.getInstance().update(log);

        GameTemplate.getInstance().setTabs(ScreenName.MARKET);
        rightPane.getChildren().add(GameTemplate.getInstance().getPlayersTabs());
        rightPane.getChildren().add(GameTemplate.getInstance().getMarketsTabs());
        centerPane.getChildren().add(MarketTemplate.getInstance().updateMarket());
        centerPane.getChildren().add(MarketTemplate.getInstance().getVHighlight());
        centerPane.getChildren().add(MarketTemplate.getInstance().getHHighlight());
    }

    public void goToBoard(ActionEvent actionEvent) {
        change(ScreenName.PERSONAL_BOARD);
    }

    //TODO

    @Override
    public void handleStatusMessage(StatusMessage message) {

    }

    @Override
    public void goToMarket() { }

    @Override
    public void goToClientBoard() {
        change(ScreenName.PERSONAL_BOARD);
    }

    @Override
    public void goToDev() {
        change(ScreenName.DEV_MARKET);
    }

    @Override
    public void goToOtherBoard(ActionEvent e) {
        GUIHelper.getInstance().setSelectedPlayer(e.getSource());
        change(ScreenName.OTHER_BOARD);
    }
}
