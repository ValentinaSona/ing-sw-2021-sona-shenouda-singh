package it.polimi.ingsw.client.ui.gui.JFXControllers;

import it.polimi.ingsw.client.modelview.GameView;
import it.polimi.ingsw.client.modelview.MarketView;
import it.polimi.ingsw.client.ui.gui.GUIHelper;
import it.polimi.ingsw.server.model.Market;
import it.polimi.ingsw.server.model.MarketBuilder;
import it.polimi.ingsw.server.model.MarketMarble;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MarketGUIController extends AbstractGUIController {

    private HashMap<SVGPath, Rectangle> rowColMap;

    @FXML
    private Rectangle rc0, rc1, rc2, rc3, rc4, rc5, rc6;
    @FXML
    private SVGPath ar0, ar1, ar2, ar3, ar4, ar5, ar6;
    @FXML
    private GridPane marketGrid;
    @FXML
    private StackPane leftPane;

    private List<Rectangle> recList = Arrays.asList(rc0, rc1, rc2, rc3, rc4, rc5, rc6);
    private List<SVGPath> arList = Arrays.asList(ar0, ar1, ar2, ar3, ar4, ar5, ar6);

    @FXML
    public void initialize() {
        leftPane.getChildren().add(GameTemplate.getInstance().getTabs(ScreenName.MARKET));
        updateMarket();
    }

    public void goToBoard(ActionEvent actionEvent) {
        change(ScreenName.PERSONAL_BOARD);
    }

    //TODO

    @Override
    public void handleStatusMessage(StatusMessage message) {

    }

    public void marbleEnter(MouseEvent mouseEvent) {
        var rec = (Rectangle)mouseEvent.getSource();
        rec.setOpacity(1);
        //TODO fix this
        //int index = recList.indexOf(rec);
        //arList.get(index).setFill(Color.rgb(255, 211, 181));
    }

    public void marbleExit(MouseEvent mouseEvent) {
        var rec = (Rectangle)mouseEvent.getSource();
        rec.setOpacity(0);
        //int index = recList.indexOf(rec);
        //arList.get(index).setFill(Color.rgb(245, 138, 66));
    }

    public void updateMarket() {
        var market = GameView.getInstance().getMarketInstance();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                marketGrid.add(new ImageView(GUIHelper.getInstance().getImage(market.getTray()[i][j], 106, 106)), j, i+1);
            }
        }

        marketGrid.add(new ImageView(GUIHelper.getInstance().getImage(market.getExtra(), 106, 106)), 4, 0);
    }
}
