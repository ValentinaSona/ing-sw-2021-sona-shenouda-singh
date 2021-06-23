package it.polimi.ingsw.client.ui.gui.JFXControllers;

import it.polimi.ingsw.client.ui.controller.UIController;
import it.polimi.ingsw.client.ui.gui.GUIHelper;
import it.polimi.ingsw.client.ui.gui.GUIMessageHandler;
import it.polimi.ingsw.client.ui.gui.GUISizes;
import it.polimi.ingsw.server.model.Id;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.util.Objects;

public class LeaderShowGUIController extends AbstractGUIController implements GameGUIControllerInterface {

    @FXML
    private BorderPane mainPane;
    @FXML
    private Region backRegion;
    @FXML
    private HBox leaderBox;

    @FXML
    public void initialize() {

        GUIMessageHandler.getInstance().setCurrentGameController(this);

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

    @Override
    public void update() {
        updateLeader();
    }

    public void updateLeader() {
        var cards = GUIHelper.getInstance().getClientView().getLeaderCards();
        leaderBox.getChildren().clear();
        cards.stream().filter(Objects::nonNull).forEach(e -> {

            VBox leader = new VBox(30);
            leader.setAlignment(Pos.CENTER);
            leader.getChildren().add(new ImageView(GUIHelper.getInstance().getImage(e, GUISizes.get().leaderShowX(), GUISizes.get().leaderShowY())));

            var activate = new Button();
            activate.setText("Activate");
            activate.getStyleClass().add("basicButton");

            activate.setOnAction(event -> {
                Id id;
                int index = GUIHelper.getInstance().getClientView().getLeaderCards().indexOf(e);
                if (index == 0) UIController.getInstance().activateSpecialAbility(Id.LEADER_CARD_1);
                else UIController.getInstance().activateSpecialAbility(Id.LEADER_CARD_2);
                GUIHelper.getInstance().setScreen(ScreenName.PERSONAL_BOARD);
            });

            var throwCard = new Button();
            throwCard.setText("Throw");
            throwCard.getStyleClass().add("basicButton");

            throwCard.setOnAction(event -> {
                Id id;
                int index = GUIHelper.getInstance().getClientView().getLeaderCards().indexOf(e);
                if (index == 0) UIController.getInstance().throwLeaderCard(Id.LEADER_CARD_1);
                else UIController.getInstance().throwLeaderCard(Id.LEADER_CARD_2);
            });

            leader.getChildren().add(activate);
            leader.getChildren().add(throwCard);

            leaderBox.getChildren().add(leader);
        });
    }

    public void goBack(ActionEvent actionEvent) {
        change(GUIHelper.getInstance().getPrevScreen());
    }
}
