package it.polimi.ingsw.client.ui.gui;

import it.polimi.ingsw.client.modelview.MatchSettings;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.model.Resource;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.ArrayList;

public class GameLog {

    private static int logLenght = 4;

    private static GameLog singleton;
    private TextFlow log;

    public static GameLog getInstance() {
        if (singleton == null) singleton = new GameLog();
        return singleton;
    }

    private GameLog() {
        log = new TextFlow();
        log.getStyleClass().add("textLog");
        log.setMaxWidth(400);
        log.setMaxHeight(350);
        log.setPadding(new Insets(5, 0, 0, 20));
        StackPane.setMargin(log, new Insets(0, 0, 10, 0));
        StackPane.setAlignment(log, Pos.BOTTOM_CENTER);
    }

    public void setLog(StackPane pane) {
        pane.getChildren().add(log);
    }

    public void update(LogUpdates logUpdates, User user) {
        Platform.runLater(() -> {
            switch(logUpdates) {

                case TURN -> {
                    if (user.getNickName().equals(MatchSettings.getInstance().getClientNickname())) {
                        add("It's your turn!\n\n");
                    }
                    else add("It's " + user.getNickName() + "'s turn!\n\n");
                }

                case ABILITY_ACTIVATION -> {
                    if (!user.getNickName().equals(MatchSettings.getInstance().getClientNickname()))
                        add(user.getNickName() + " has activated a leader card\n\n");
                }

                case CARD_THROW -> {
                    if (!user.getNickName().equals(MatchSettings.getInstance().getClientNickname()))
                        add(user.getNickName() + " threw a leader card\n\n");
                }
            }
        });
    }

    public void update(LogUpdates logUpdates, User user, ArrayList<Resource> resources) {

        Platform.runLater(() -> {
            String activity = "";

            if(logUpdates == LogUpdates.BUY_MARKET) {
                if (user.getNickName().equals(MatchSettings.getInstance().getClientNickname())) {
                    activity = "You bought ";
                }
                else activity = user.getNickName() + " has bought ";

                activity += transformToString(resources.get(0));

                for (int i = 1; i < resources.size(); i++){
                    activity += ", ";
                    activity += transformToString(resources.get(i));
                }

                activity += " from the market\n\n";
            }

            add(activity);
        });
    }

    private void add(String text) {
        if(log.getChildren().size() > logLenght) log.getChildren().remove(0);

        var line = new Text(text);
        line.setStyle("-fx-fill: #5a9cf2");

        log.getChildren().add(line);
    }

    public String transformToString(Resource resource) {
        String output = Integer.toString(resource.getQuantity());
        output += " ";
        output += resource.getResourceType().toString().toLowerCase();

        if (resource.getQuantity() > 1) output += "s";

        return output;
    }
}
