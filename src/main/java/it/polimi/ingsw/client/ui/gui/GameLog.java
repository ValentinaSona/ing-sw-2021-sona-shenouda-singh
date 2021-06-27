package it.polimi.ingsw.client.ui.gui;

import it.polimi.ingsw.client.modelview.MatchSettings;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.model.Resource;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerActivateProductionMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerSoloDiscardMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerSoloMoveMessage;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.ArrayList;
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
        log.setMaxWidth(GUISizes.get().logX());
        log.setMaxHeight(GUISizes.get().logY());
        log.setPadding(new Insets(5, 20, 5, 20));
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
                        add("It's your turn!");
                    }
                    else add("It's " + user.getNickName() + "'s turn!");
                }

                case ABILITY_ACTIVATION -> {
                    if (!user.getNickName().equals(MatchSettings.getInstance().getClientNickname()))
                        add(user.getNickName() + " has activated a leader card");
                }

                case CARD_THROW -> {
                    if (!user.getNickName().equals(MatchSettings.getInstance().getClientNickname()))
                        add(user.getNickName() + " threw a leader card");
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

                activity += getResRegister(resources);

                activity += " from the market";
            }

            add(activity);
        });
    }

    public String getResRegister(List<Resource> resources) {

        String activity;

        activity = transformToString(resources.get(0));

        for (int i = 1; i < resources.size(); i++){
            activity += ", ";
            activity += transformToString(resources.get(i));
        }

        return activity;

    }

    public void update(LogUpdates logUpdates, ServerMessage message) {

        String activity = "";

        switch(logUpdates){

            case PRODUCTION_ACTIVATED -> {
                ServerActivateProductionMessage newMessage = (ServerActivateProductionMessage) message;

                if (newMessage.getUser().getNickName().equals(MatchSettings.getInstance().getClientNickname())) {
                    activity = "You have";
                }
                else activity = newMessage.getUser().getNickName() + " has";

                activity += " spent ";
                activity += getResRegister(newMessage.getSpent());

                activity += " and gained ";
                activity += getResRegister(newMessage.getGained());
            }

            case SOLO_DISCARD -> {
                ServerSoloDiscardMessage newMessage = (ServerSoloDiscardMessage) message;

                activity = "Two ";
                activity += newMessage.getType().toString().toLowerCase();
                activity += " development cards were discarded";
            }

            case BLACK_CROSS -> {
                ServerSoloMoveMessage newMessage = (ServerSoloMoveMessage) message;

                activity = "Lorenzo has advanced on the faith track ";
            }
        }

        add(activity);

    }

    public void update(LogUpdates logUpdates) {
        switch(logUpdates){
            case DEV_NOT_RICH -> addError("You don't have enough resources to buy this card");
            case DISCONNECTION -> add("A player has disconnected");
            case RECONNECTION -> add("A player has reconnected");
        }
    }

    private void add(String text) {
        Platform.runLater(() -> {
            if(log.getChildren().size() > logLenght) log.getChildren().remove(0);

            var line = new Text(text + "\n\n");
            line.setStyle("-fx-fill: #5a9cf2");

            log.getChildren().add(line);
        });
    }

    private void addError(String text) {
        Platform.runLater(() -> {
            if(log.getChildren().size() > logLenght) log.getChildren().remove(0);

            var line = new Text(text + "\n\n");
            line.setStyle("-fx-fill: red");

            log.getChildren().add(line);
        });
    }

    public String transformToString(Resource resource) {
        String output = Integer.toString(resource.getQuantity());
        output += " ";
        output += resource.getResourceType().toString().toLowerCase();

        if (resource.getQuantity() > 1) output += "s";

        return output;
    }
}
