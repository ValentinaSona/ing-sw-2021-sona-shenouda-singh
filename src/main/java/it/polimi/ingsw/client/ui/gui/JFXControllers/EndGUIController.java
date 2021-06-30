package it.polimi.ingsw.client.ui.gui.JFXControllers;

import it.polimi.ingsw.client.modelview.MatchSettings;
import it.polimi.ingsw.client.ui.gui.GUIHelper;
import it.polimi.ingsw.server.exception.EndOfGameCause;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import javafx.util.Pair;

import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

public class EndGUIController {

    @FXML
    private ListView<Text> list;
    @FXML
    private Label winner, cause;

    @FXML
    public void initialize() {

        var header = new Text(String.format("%-24s %s", "Player", "Score"));
        header.setStyle("-fx-fill: #063154;");

        list.getItems().add(header);

        var rank = GUIHelper.getInstance().getRank();

        var scores = rank.entrySet()
                .stream().sorted(Comparator.comparingInt(Map.Entry::getValue))
                .map(e -> new Pair<>(e.getKey(), e.getValue())).collect(Collectors.toList());

        scores.forEach(e -> {
            var text = new Text(String.format("%-25s %s", e.getKey(), e.getValue()));
            if(e.getKey().equals(MatchSettings.getInstance().getClientNickname()) && !GUIHelper.getInstance().isSolo()) text.setStyle("-fx-fill: #0c361a");
            else text.setStyle("-fx-fill: white");
            list.getItems().add(text);
        });

        var endCause = GUIHelper.getInstance().getCause();

        if (!GUIHelper.getInstance().isSolo()) {
            if(scores.get(0).getKey().equals(MatchSettings.getInstance().getClientNickname())) winner.setText("You win!");
            else winner.setText(scores.get(0).getKey() + " wins!");
        }
        else winner.setText("You win!");

        switch(endCause){
            case DEBUG -> cause.setText("Debug");
            case FAITH_END -> cause.setText("A player has completed the faith track");
            case SEVENTH_CARD -> cause.setText("A player has bought the 7th development card");
            case LORENZO_FAITH -> {
                cause.setText("Lorenzo has completed the faith track");
                winner.setText("Lorenzo wins!");
            }
            case LORENZO_DISCARD -> {
                cause.setText("Lorenzo has discarded a column of development cards");
                winner.setText("Lorenzo wins!");
            }
        }
    }

    public void quit(ActionEvent actionEvent) {
        Platform.exit();
    }
}
