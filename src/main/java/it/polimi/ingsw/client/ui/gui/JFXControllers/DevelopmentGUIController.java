package it.polimi.ingsw.client.ui.gui.JFXControllers;

import it.polimi.ingsw.client.modelview.GameView;
import it.polimi.ingsw.client.ui.gui.CurrAction;
import it.polimi.ingsw.client.ui.gui.GUIHelper;
import it.polimi.ingsw.client.ui.gui.GameLog;
import it.polimi.ingsw.client.ui.gui.LogUpdates;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class DevelopmentGUIController extends AbstractGUIController implements GameGUIControllerInterface{

    @FXML
    private GridPane devGrid;
    @FXML
    private StackPane rightPane, centerPane;
    @FXML
    private Button buy, end;
    @FXML
    private ImageView inkwell;

    @FXML
    public void initialize() {
        GUIHelper.getInstance().setCurrentGameController(this);
        GameTemplate.getInstance().setTabs(ScreenName.DEV_MARKET);
        rightPane.getChildren().add(GameTemplate.getInstance().getPlayersTabs());

        centerPane.getChildren().add(GUIHelper.getInstance().getDevVisualizer());

        buy.setOpacity(0);
        buy.setDisable(true);

        updateDevelopment();

        GameLog.getInstance().setLog(rightPane);

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
        if(GUIHelper.getInstance().getTurn()) {
            end.setDisable(GUIHelper.getInstance().getClientView().isMainAction());
            inkwell.setOpacity(1);
        }
        else inkwell.setOpacity(0);
    }

    public void updateDevelopment() {

        GUIHelper.getInstance().updateDevGrid(devGrid);

    }

    public void enableBuy(boolean chosenCard) {
        buy.setDisable(!chosenCard);
        if (chosenCard) buy.setOpacity(1);
        else buy.setOpacity(0);
    }

    public void buyCard(ActionEvent actionEvent) {
        var price = GameView.getInstance().getDevelopmentCardsMarket().getTray()[GUIHelper.getInstance().getSelectedI()][GUIHelper.getInstance().getSelectedJ()].getCost();
        if (GUIHelper.getInstance().getClientView().canPay(price)) {
            change(ScreenName.PERSONAL_BOARD);
            GUIHelper.getInstance().setCurrAction(CurrAction.SELECTING_SLOT);
        }
        else GameLog.getInstance().update(LogUpdates.DEV_NOT_RICH);
    }

    public void goToLeader(ActionEvent actionEvent) {
        GUIHelper.getInstance().setScreenshot(GUIHelper.getInstance().getCurrentScene().snapshot(null));
        change(ScreenName.LEADER);
    }
}
