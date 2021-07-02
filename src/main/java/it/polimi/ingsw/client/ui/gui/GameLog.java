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

/**
 * This singleton class handles the GUI log, a TextFlow that shows regular updates during the game
 */
public class GameLog {

    private static final int logLength = 3;
    private static final String warning = "#ffb545";

    private static GameLog singleton;
    private final TextFlow log;

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

    /**
     * Insert the log into a pane
     * @param pane pane where the log should be put in
     */
    public void setLog(StackPane pane) {
        pane.getChildren().add(log);
    }

    /**
     * Updates the log
     * @param logUpdates the update
     * @param user the user associated with the update
     */
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

    /**
     * Updates the log keeping in account a list of resources
     * @param logUpdates the update
     * @param user the user who triggered the update
     * @param resources the list of resources to be printed
     */
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

    /**
     * Transforms a list of resources in easily readable string
     * @param resources the list of resources
     * @return a String containing the resources
     */
    public String getResRegister(List<Resource> resources) {

        StringBuilder activity;

        activity = new StringBuilder(transformToString(resources.get(0)));

        for (int i = 1; i < resources.size(); i++){
            activity.append(", ");
            activity.append(transformToString(resources.get(i)));
        }

        return activity.toString();

    }

    public void update(LogUpdates logUpdates, ServerMessage message) {

        StringBuilder activity = new StringBuilder();

        switch(logUpdates){

            case PRODUCTION_ACTIVATED -> {
                ServerActivateProductionMessage newMessage = (ServerActivateProductionMessage) message;

                if (newMessage.getUser().getNickName().equals(MatchSettings.getInstance().getClientNickname())) {
                    activity = new StringBuilder("You have");
                }
                else activity = new StringBuilder(newMessage.getUser().getNickName() + " has");

                activity.append(" spent ");
                activity.append(getResRegister(newMessage.getSpent()));

                activity.append(" and gained ");
                activity.append(getResRegister(newMessage.getGained()));
            }

            case SOLO_DISCARD -> {
                ServerSoloDiscardMessage newMessage = (ServerSoloDiscardMessage) message;

                activity = new StringBuilder("Two ");
                activity.append(newMessage.getType().toString().toLowerCase());
                activity.append(" development cards were discarded");
            }

            case BLACK_CROSS -> {
                ServerSoloMoveMessage newMessage = (ServerSoloMoveMessage) message;

                if (newMessage.isShuffled()) activity = new StringBuilder("Lorenzo has gained 1 faith points and has shuffled his actions");

                activity = new StringBuilder("Lorenzo has gained 2 faith points");
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
                    activity.append("You bought");
                else activity.append(newMessage.getUser().getNickName()).append(" has bought");

                activity.append(" a ").append(newMessage.getCard().getType().toString().toLowerCase()).append(" level ").append(newMessage.getCard().getLevel()).append(" card");
            }
            case END -> {
                var newMessage = (ServerLastTurnsMessage) message;
                switch(newMessage.getCause()){
                    case LORENZO_DISCARD -> add("Lorenzo has discarded a column of development cards. Play your last turn", warning);
                    case LORENZO_FAITH -> add("Lorenzo has completed the faith track, play your last turn", warning);
                    case SEVENTH_CARD -> {
                        activity.append("A player has bought the 7th development card. ");
                        activity.append(newMessage.getLastUsers().get(0).getNickName());

                        for(int i = 1; i < newMessage.getLastUsers().size(); i++) {
                            activity.append(", ");
                            activity.append(newMessage.getLastUsers().get(i).getNickName());
                        }
                        activity.append(" must play their last turn");

                        add(activity.toString(), warning);
                    }
                    case FAITH_END -> {
                        activity.append("A player has completed the faith track. ");
                        if(newMessage.getLastUsers() != null) {
                            activity.append(newMessage.getLastUsers().get(0).getNickName());

                            for(int i = 1; i < newMessage.getLastUsers().size(); i++) {
                                activity.append(", ").append(newMessage.getLastUsers().get(i).getNickName());
                            }
                            activity.append(" must play their last turn");

                            add(activity.toString(), warning);
                        }
                    }
                    case DEBUG -> add("Debug", warning);
                }
                return;
            }
        }

        add(activity.toString());

    }

    public void update(LogUpdates logUpdates) {
        switch(logUpdates){
            case DEV_NOT_RICH -> add("You don't have enough resources to buy this card", "red");
            case DISCONNECTION -> add("A player has disconnected");
            case RECONNECTION -> add("A player has reconnected");
            case CHOOSE_WHITE -> {
                addSimple("You must choose which ability you want to use for each white marble.");
                add(" Drag the ability you want to use and drop it on the correspondent resource!");
            }
            case HES_RUNNING_AWAY -> add("Please choose how you wish to convert the marbles", "red");
            case DEV_SLOT -> add("Click on the slot where you want to put the card");
            case DEV_RES -> add("Select the resources you want to use to pay the card and click the \"confirm\" button");
            case DEV_RES_ERROR -> add("Wrong selection of resources", "red");
        }
    }

    /**
     * Adds a text without adding new-line characters after it
     * @param text the String to add to the log
     */
    private void addSimple(String text) {
        Platform.runLater(() -> {
            if(log.getChildren().size() > logLength) {
                log.getChildren().remove(0);
            }

            var line = new Text(text);
            line.setStyle("-fx-fill: #5a9cf2");

            log.getChildren().add(line);
        });
    }

    /**
     * Adds a text to the log, followed by 2 new-line characters
     * @param text the text to add
     */
    private void add(String text) {
        Platform.runLater(() -> {
            if(log.getChildren().size() > logLength) {
                log.getChildren().remove(0);
            }

            var line = new Text(text + "\n\n");
            line.setStyle("-fx-fill: #5a9cf2");

            log.getChildren().add(line);
        });
    }

    /**
     * Adds a text of a specified color to the log
     * @param text the text to add
     * @param color a Sting of the desired color, can be a hex value, a rgb value or the color's name
     */
    private void add(String text, String color) {
        Platform.runLater(() -> {
            if(log.getChildren().size() > logLength) {
                log.getChildren().remove(0);
            }

            var line = new Text(text + "\n\n");
            line.setStyle("-fx-fill: " + color);

            log.getChildren().add(line);
        });
    }

    /**
     * Convert a resource into a string keeping in account the plural
     * @param resource the resource to convert
     * @return the string containing the string version of the resource
     */
    public String transformToString(Resource resource) {
        String output = Integer.toString(resource.getQuantity());
        output += " ";
        output += resource.getResourceType().toString().toLowerCase();

        if (resource.getQuantity() > 1) output += "s";

        return output;
    }
}
