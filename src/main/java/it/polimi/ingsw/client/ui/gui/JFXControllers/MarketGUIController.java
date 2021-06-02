package it.polimi.ingsw.client.ui.gui.JFXControllers;

import it.polimi.ingsw.client.modelview.MarketView;
import it.polimi.ingsw.server.model.Market;
import it.polimi.ingsw.server.model.MarketBuilder;
import it.polimi.ingsw.server.model.MarketMarble;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;

import java.util.HashMap;

public class MarketGUIController extends AbstractGUIController {

    private HashMap<SVGPath, Rectangle> rowColMap;

    @FXML
    private GridPane marketGrid;
    @FXML
    private Rectangle rc1, rc2, rc3, rc4, rc5, rc6, rc7;
    @FXML
    private SVGPath ar1, ar2, ar3, ar4, ar5, ar6, ar7;

    @FXML
    public void initialize() {
        rowColMap = new HashMap<>();
        rowColMap.put(ar1, rc1);
        rowColMap.put(ar2, rc2);
        rowColMap.put(ar3, rc3);
        rowColMap.put(ar4, rc4);
        rowColMap.put(ar5, rc5);
        rowColMap.put(ar6, rc6);
        rowColMap.put(ar7, rc7);

        Market market = MarketBuilder.build();
        MarketView view = market.getVisible();

        MarketMarble[][] tray = view.getTray();

        for(int i=0; i < 3; i++) {
            for(int j=0; j < 4; j++){

                marketGrid.add(new ImageView(new Image("assets/market/" + tray[i][j].toString().toLowerCase() + ".png", 120, 120, false, false)), j, i+1);

            }
        }

        marketGrid.add(new ImageView(new Image("assets/market/" + view.getExtra().toString().toLowerCase() + ".png", 120, 120, false, false)), 4, 0);

    }

    public void goToBoard(ActionEvent actionEvent) {
        change(ScreenName.PERSONAL_BOARD);
    }

    //TODO

    @Override
    public void handleStatusMessage(StatusMessage message) {

    }
}
