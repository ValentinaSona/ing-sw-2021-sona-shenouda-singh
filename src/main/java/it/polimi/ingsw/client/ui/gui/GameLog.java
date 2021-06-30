package it.polimi.ingsw.client.ui.gui;

import it.polimi.ingsw.client.modelview.MatchSettings;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.model.Resource;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.client.ui.cli.CLIHelper.CHECK_MARK;

public class GameLog {

    private static int logLenght = 2;
    private static String warning = "#ffb545";

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

                if (user.getNickName().equals(MatchSettings.getInstance().getClientNickname())) {
                    activity += ". You can drag the resources in the depots you want to put them";
                }
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

                if (newMessage.isShuffled()) activity = "Lorenzo has gained 1 faith points and has shuffled his actions";

                activity = "Lorenzo has gained 2 faith points";
            }

            case FAITH_TRACK -> {
                var newMessage = (ServerFaithTrackMessage) message;
                if(newMessage.getUser()==null){
                    if (newMessage.isReport()) {
                        add("Lorenzo has received " + newMessage.getFaith() + " faith points and triggered a vatican report", warning);
                    } else
                        add("Lorenzo has received " + newMessage.getFaith()+ " faith points");
                    return;
                }

                if (newMessage.isReport()) {
                    if (newMessage.getUser().getNickName().equals(MatchSettings.getInstance().getClientNickname()))
                        add("A vatican report has been triggered", warning);
                    else
                    if (newMessage.getFaith()!= 0)
                        add(newMessage.getUser().getNickName() + " has received " + newMessage.getFaith() + " faith point and triggered a vatican report");
                } else {
                    if (newMessage.getUser().getNickName().equals(MatchSettings.getInstance().getClientNickname()))
                        add("You have received " + newMessage.getFaith() + " faith points");
                    else
                        add(newMessage.getUser().getNickName() + " has received " + newMessage.getFaith() + " faith point");
                }
                return;
            }
            case BUY_DEV -> {
                var newMessage = (ServerBuyDevelopmentCardMessage) message;
                if (newMessage.getUser().getNickName().equals(MatchSettings.getInstance().getClientNickname()))
                    activity += "You bought";
                else activity += newMessage.getUser().getNickName() + " has bought";

                activity += " a " + newMessage.getCard().getType().toString().toLowerCase() + " level " + newMessage.getCard().getLevel() + " card";
            }
        }

        add(activity);

    }

    public void update(LogUpdates logUpdates) {
        switch(logUpdates){
            case DEV_NOT_RICH -> add("You don't have enough resources to buy this card", "red");
            case DISCONNECTION -> add("A player has disconnected");
            case RECONNECTION -> add("A player has reconnected");
            case CHOOSE_WHITE -> add("You must choose which ability you want to use for each white marble. Drag the ability you want to use and drop it on the correspondent resource!");
            case HES_RUNNING_AWAY -> add("Please choose how you wish to convert the marbles", "red");
            case DEV_SLOT -> add("Click on the slot where you want to put the card");
            case DEV_RES -> add("Select the resources you want to use to pay the card and click the \"confirm\" button");
            case DEV_RES_ERROR -> add("Wrong selection of resources", "red");
        }
    }

    private void add(String text) {
        Platform.runLater(() -> {
            if(log.getChildren().size() > logLenght) {
                log.getChildren().remove(0);
            }

            var line = new Text(text + "\n\n");
            line.setStyle("-fx-fill: #5a9cf2");

            log.getChildren().add(line);
        });
    }

    private void add(String text, String color) {
        Platform.runLater(() -> {
            if(log.getChildren().size() > logLenght) {
                log.getChildren().remove(0);
            }

            var line = new Text(text + "\n\n");
            line.setStyle("-fx-fill: " + color);

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
