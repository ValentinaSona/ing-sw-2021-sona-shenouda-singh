package it.polimi.ingsw.client.ui.gui.JFXControllers;

import it.polimi.ingsw.client.modelview.GameView;
import it.polimi.ingsw.client.modelview.MarketView;
import it.polimi.ingsw.client.ui.controller.UIController;
import it.polimi.ingsw.client.ui.gui.Action;
import it.polimi.ingsw.client.ui.gui.GUIHelper;
import it.polimi.ingsw.client.ui.gui.GameLog;
import it.polimi.ingsw.server.model.MarketMarble;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerStartTurnMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;

import java.util.Arrays;

public class MarketGUIController extends AbstractGUIController implements GameGUIControllerInterface {

    @FXML
    private StackPane rightPane;
    @FXML
    private StackPane centerPane;
    @FXML
    private HBox topBar;
    @FXML
    private VBox choice;
    @FXML
    private Button buyButton, end;

    private boolean turn;
    private int rowCol;

    @FXML
    public void initialize() {

        activateTurn(turn);

        GUIHelper.getInstance().setCurrentGameController(this);
        GameLog.getInstance().setLog(rightPane);
        activateTurn(GUIHelper.getInstance().getTurn());

        GameTemplate.getInstance().setTabs(ScreenName.MARKET);
        rightPane.getChildren().add(GameTemplate.getInstance().getPlayersTabs());
        centerPane.getChildren().add(MarketTemplate.getInstance().updateMarket());
        centerPane.getChildren().add(MarketTemplate.getInstance().getVHighlight());
        centerPane.getChildren().add(MarketTemplate.getInstance().getHHighlight());

        rowCol = -1;

        update();
    }

    private void activateTurn(boolean turn) {
        this.turn = turn;
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

    @Override
    public void update() {
        activateTurn(GUIHelper.getInstance().getTurn());
        if(turn) {
            end.setDisable(GUIHelper.getInstance().getClientView().isMainAction());
        }
    }

    public void showMarbles(int rowCol) {

        this.rowCol = rowCol;

        if (!GUIHelper.getInstance().getTurn()) return;

        MarketMarble[][] market = GameView.getInstance().getMarketInstance().getTray();
        MarketMarble[] selected;

        if (rowCol < 3) {
            selected = market[rowCol];
        }

        else {
            selected = new MarketMarble[3];
            for (int i = 0; i < 3; i++) {
                selected[i] = market[i][rowCol-3];
            }
        }

        choice.getChildren().clear();

        for (MarketMarble m: selected) {
            choice.getChildren().add(new ImageView(GUIHelper.getInstance().getResourceImage(m, 130, 130)));
        }

        buyButton.setDisable(false);
        buyButton.setOpacity(1);
    }

    public void buyMarbles(ActionEvent actionEvent) {
        if (rowCol == -1) return;
        GUIHelper.getInstance().setChoosingTemp(true);
        UIController.getInstance().buyMarbles(rowCol);
        rowCol = -1;
    }
}
