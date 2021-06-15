package it.polimi.ingsw.client.ui.gui.JFXControllers;

import it.polimi.ingsw.client.ui.gui.GUIHelper;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

public class LeaderShowGUIController extends AbstractGUIController implements GameGUIControllerInterface {

    @FXML
    private BorderPane mainPane;
    @FXML
    private Region backRegion;
    @FXML
    private HBox leaderBox;

    @FXML
    public void initialize() {

        var size = new BackgroundSize(1.0, 1.0, true, true, false, false);

        BackgroundImage backIm = new BackgroundImage(GUIHelper.getInstance().getScreenShot(),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                size);

        var background = new Background(backIm);
        backRegion.setBackground(background);
        GaussianBlur blur = new GaussianBlur(20);
        backRegion.setEffect(blur);

        updateLeader();
    }

    @Override
    public void handleStatusMessage(StatusMessage message) {

    }

    @Override
    public void goToMarket() {

    }

    @Override
    public void goToClientBoard() {

    }

    @Override
    public void goToDev() {

    }

    @Override
    public void goToOtherBoard(ActionEvent e) {

    }

    public void updateLeader() {
        var cards = GUIHelper.getInstance().getClientView().getLeaderCards();
        leaderBox.getChildren().clear();
        cards.forEach(e -> leaderBox.getChildren().add(new ImageView(GUIHelper.getInstance().getImage(e, 369, 558))));
    }

    public void goBack(ActionEvent actionEvent) {
        change(GUIHelper.getInstance().getPrevScreen());
    }
}
