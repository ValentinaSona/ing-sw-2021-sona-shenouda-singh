package it.polimi.ingsw.client.ui.gui.JFXControllers;

import it.polimi.ingsw.client.modelview.GameView;
import it.polimi.ingsw.client.ui.controller.UIController;
import it.polimi.ingsw.client.ui.gui.*;
import it.polimi.ingsw.server.model.MarketMarble;
import it.polimi.ingsw.server.model.Resource;
import it.polimi.ingsw.server.model.ResourceType;
import it.polimi.ingsw.server.model.WhiteMarbleAbility;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.Objects;
import java.util.stream.Stream;

public class MarketGUIController extends AbstractGUIController implements GameGUIControllerInterface {

    @FXML
    private StackPane rightPane;
    @FXML
    private StackPane centerPane;
    @FXML
    private Pane activeAb;
    @FXML
    private HBox topBar, cardBox;
    @FXML
    private VBox choice;
    @FXML
    private Button buyButton, end;
    @FXML
    private ImageView inkwell;

    private boolean turn;
    private int rowCol;

    private long numOfWhites = 0;

    private Resource[] selection;

    @FXML
    public void initialize() {

        disableNode(activeAb);

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
        if(turn) inkwell.setOpacity(1);
        else inkwell.setOpacity(0);
    }

    public void goToBoard(ActionEvent actionEvent) {
        change(ScreenName.PERSONAL_BOARD);
    }

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
        end.setDisable(! (!GUIHelper.getInstance().getClientView().isMainAction() && GUIHelper.getInstance().getClientView().isMyTurn()));
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

        numOfWhites = Stream.of(selected).filter(e -> e == MarketMarble.WHITE).count();
        selection = new Resource[(int)numOfWhites];

        choice.getChildren().clear();

        for (MarketMarble m: selected) {
            var im = new ImageView(GUIHelper.getInstance().getResourceImage(m, GUISizes.get().chosenMarbles(), GUISizes.get().chosenMarbles()));
            if (m == MarketMarble.WHITE) DragNDrop.getInstance().setMarketWhiteDroppable(im, choice, selection);
            choice.getChildren().add(im);
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

    public void goToLeader(ActionEvent actionEvent) {
        GUIHelper.getInstance().setScreenshot(GUIHelper.getInstance().getCurrentScene().snapshot(null));
        change(ScreenName.LEADER);
    }

    public void openMenu(ActionEvent actionEvent) {
        GUIHelper.getInstance().setScreenshot(GUIHelper.getInstance().getCurrentScene().snapshot(null));
        change(ScreenName.GAME_MENU);
    }

    public void startTwoCardSelection() {
        enableNode(activeAb);

        var cards = GUIHelper.getInstance().getClientView().getLeaderCards();

        var marble1 = ((WhiteMarbleAbility)cards.get(0).getSpecialAbility()).getMarble().convertToResource();
        var marble2 = ((WhiteMarbleAbility)cards.get(1).getSpecialAbility()).getMarble().convertToResource();

        var im1 = new ImageView(GUIHelper.getInstance().getImage(marble1, GUISizes.get().abilityMarbles(), GUISizes.get().abilityMarbles()));
        var im2 = new ImageView(GUIHelper.getInstance().getImage(marble2, GUISizes.get().abilityMarbles(), GUISizes.get().abilityMarbles()));

        DragNDrop.getInstance().setMarketWhiteDraggable(im1, true);
        DragNDrop.getInstance().setMarketWhiteDraggable(im2, true);

        cardBox.getChildren().add(im1);
        cardBox.getChildren().add(im2);

        GUIHelper.getInstance().setCurrAction(CurrAction.TWO_LEADER);
    }

    public void acceptChoice() {
        var output = Stream.of(selection).filter(Objects::nonNull).map(e -> e.getResourceType().convertToMarble()).toArray(MarketMarble[]::new);

        if( choice.getChildren().stream().noneMatch(e -> GUIHelper.getInstance().getResFromImage( ((ImageView)e).getImage() ).getResourceType() == ResourceType.JOLLY)) {
            GUIHelper.getInstance().setCurrAction(CurrAction.IDLE);
            UIController.getInstance().convertWhiteMarbles(output);
        }
    }

    public void endTurn(ActionEvent actionEvent) {
        UIController.getInstance().endTurn();
        SelectedProductions.getInstance().reset();
    }
}
