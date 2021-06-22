package it.polimi.ingsw.client.ui.gui.JFXControllers;

import it.polimi.ingsw.client.modelview.GameView;
import it.polimi.ingsw.client.modelview.MarketView;
import it.polimi.ingsw.client.ui.gui.GUIHelper;
import it.polimi.ingsw.client.ui.gui.GUISizes;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;

public class MarketTemplate {

    private static MarketTemplate singleton;

    private MarketView market;

    private SVGPath[] rowArrows;
    private SVGPath[] colArrows;
    private Pane[][] panes;

    private GridPane marketGrid;
    private VBox vHighlight;
    private HBox hHighlight;

    public static MarketTemplate getInstance() {
        if (singleton == null) singleton = new MarketTemplate();
        return singleton;
    }

    private MarketTemplate(){

        panes = new Pane[3][4];

        for(int i = 0; i < 3; i ++) {
            for (int j = 0; j < 4; j++) {
                panes[i][j] = new Pane();
            }
        }

        market = GameView.getInstance().getMarketInstance();

        rowArrows = new SVGPath[3];
        colArrows = new SVGPath[4];

        marketGrid = new GridPane();
        marketGrid.setMaxWidth(525);
        marketGrid.setMaxHeight(525);
        StackPane.setMargin(marketGrid, new Insets(0, 200, 0, 0));

        for (int i = 0; i < rowArrows.length; i++) {
            rowArrows[i] = new SVGPath();
            rowArrows[i].setContent("M0,20,20,0,20,10,45,10,45,30,20,30,20,40");
            rowArrows[i].setFill(Color.rgb(230, 132, 62));

            GridPane.setValignment(rowArrows[i], VPos.CENTER);
            GridPane.setHalignment(rowArrows[i], HPos.CENTER);

            marketGrid.add(rowArrows[i], 4, i+1);

        }

        for (int i = 0; i < colArrows.length; i++) {
            colArrows[i] = new SVGPath();
            colArrows[i].setContent("M20,0,40,20,30,20,30,45,10,45,10,20,0,20");
            colArrows[i].setFill(Color.rgb(230, 132, 62));

            GridPane.setValignment(colArrows[i], VPos.CENTER);
            GridPane.setHalignment(colArrows[i], HPos.CENTER);

            marketGrid.add(colArrows[i], i, 4);
        }

        vHighlight = new VBox(10);
        vHighlight.setMaxWidth(525);
        vHighlight.setMaxHeight(525);
        for (int i=0; i < 3; i++) {

            var rc = new Rectangle();
            rc.setWidth(525);
            rc.setHeight(100);
            rc.setStroke(Color.TRANSPARENT);
            rc.setStyle("-fx-fill: rgba(232, 133, 79, 0.2)");
            rc.setOpacity(0);
            vHighlight.getChildren().add(rc);

            rc.setOnMouseEntered(e -> rc.setOpacity(1));
            rc.setOnMouseExited(e -> rc.setOpacity(0));

            int choice = i;

            rc.setOnMouseReleased(e ->
                    Platform.runLater(() -> ((MarketGUIController)GUIHelper.getInstance().getCurrentGameController()).showMarbles(choice)));

        }
        StackPane.setMargin(vHighlight, new Insets(220, 200, 0, 0));

        hHighlight = new HBox(10);
        hHighlight.setAlignment(Pos.BOTTOM_LEFT);
        hHighlight.setMaxWidth(420);
        hHighlight.setMaxHeight(525);
        for (int i=0; i < 4; i++) {

            var rc = new Rectangle();
            rc.setWidth(100);
            rc.setHeight(420);
            rc.setStroke(Color.TRANSPARENT);
            rc.setStyle("-fx-fill: rgba(232, 133, 79, 0.2)");
            rc.setOpacity(0);
            hHighlight.getChildren().add(rc);

            rc.setOnMouseEntered(e -> rc.setOpacity(1));
            rc.setOnMouseExited(e -> rc.setOpacity(0));

            int choice = i;

            rc.setOnMouseReleased(e ->
                    Platform.runLater(() -> ((MarketGUIController)GUIHelper.getInstance().getCurrentGameController()).showMarbles(choice + 3)));

        }
        StackPane.setMargin(hHighlight, new Insets(0, 307, 0, 0));

    }

    public GridPane updateMarket() {

        var tray = GameView.getInstance().getMarketInstance().getTray();

        marketGrid.getChildren().clear();
        addArrows();

        for(int i = 0; i < 3; i ++) {
            for(int j = 0; j < 4; j++) {
                marketGrid.add(new ImageView(GUIHelper.getInstance().getImage(tray[i][j], GUISizes.get().marbles(), GUISizes.get().marbles())), j, i+1);
            }
        }

        marketGrid.add(new ImageView(GUIHelper.getInstance().getImage(GameView.getInstance().getMarketInstance().getExtra(), GUISizes.get().marbles(), GUISizes.get().marbles())), 4, 0);

        return marketGrid;
    }

    private void addArrows() {
        for (int i = 0; i < rowArrows.length; i++)
            marketGrid.add(rowArrows[i], 4, i+1);

        for (int i = 0; i < colArrows.length; i++)
            marketGrid.add(colArrows[i], i, 4);
    }

    public VBox getVHighlight() {
        return vHighlight;
    }

    public HBox getHHighlight() {
        return hHighlight;
    }

}
