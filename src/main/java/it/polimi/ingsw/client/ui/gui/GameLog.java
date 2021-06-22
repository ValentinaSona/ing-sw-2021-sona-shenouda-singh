package it.polimi.ingsw.client.ui.gui;

import it.polimi.ingsw.client.modelview.MatchSettings;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.model.MarketMarble;
import it.polimi.ingsw.server.model.Resource;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
        log.setPrefWidth(404);
        log.setPrefHeight(120);
        log.setPadding(new Insets(5, 0, 0, 20));
    }

    public void setLog(HBox bar) {
        bar.getChildren().add(1, log);
    }

    public void update(Action action, User user) {
        Platform.runLater(() -> {
            if(action == Action.TURN) {
                if (user.getNickName().equals(MatchSettings.getInstance().getClientNickname())) {
                    add("It's your turn!");
                }
                else add("It's " + user.getNickName() + "'s turn!\n\n");
            }
        });
    }

    public void update(Action action, User user, ArrayList<Resource> resources) {

        Platform.runLater(() -> {
            String activity = "";

            if(action == Action.BUY_MARKET) {
                if (user.getNickName().equals(MatchSettings.getInstance().getClientNickname())) {
                    activity = "You bought ";
                }
                else activity = user.getNickName() + " has bought ";

                activity += transformToString(resources.get(0));

                for (int i = 1; i < resources.size(); i++){
                    activity += ", ";
                    activity += transformToString(resources.get(i));
                }

                activity += " from the market.\n\n";
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
        output += resource.getResourceType();

        if (resource.getQuantity() > 1) output += "s";

        return output;
    }
}
