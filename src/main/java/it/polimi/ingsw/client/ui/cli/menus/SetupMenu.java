package it.polimi.ingsw.client.ui.cli.menus;

import it.polimi.ingsw.client.modelview.GameView;
import it.polimi.ingsw.client.modelview.MatchSettings;
import it.polimi.ingsw.client.ui.cli.CLI;
import it.polimi.ingsw.client.ui.controller.UIController;
import it.polimi.ingsw.server.model.Id;
import it.polimi.ingsw.server.model.LeaderCard;
import it.polimi.ingsw.server.model.Resource;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerSetupUserMessage;

import java.util.HashMap;
import java.util.Map;

import static it.polimi.ingsw.client.ui.cli.CLIHelper.CHECK_MARK;
import static it.polimi.ingsw.client.ui.cli.CLIHelper.MARBLE_LEGEND;
/**
 * Implements the setup menu, the phase in between the main one and the actual game.
 * Handles the selection of leader cards and starting resources. When all the players are done, the game menu takes over.
 */
public class SetupMenu {

    private final CLI cli;
    private MenuRunner runner;

    private LeaderCard[] chosen;
    private Map<Id, Resource> map;
    private boolean done;


    public SetupMenu(CLI cli) {
        this.cli = cli;
        chosen = null;
        done = false;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    /**
     * The main menu.
     * The setup menu is actually woken up from the menuRunner waitStateChange every time a player starts their setup turn, so it checks that the player has not already had theirs.
     */
    public void run(){
        this.runner = MenuRunner.getInstance(cli);
        if (!done) setupMenu();
        done = true;
    }

    /**
     * The options a player is given depend on whether or not it's their setup turn and their turn order.
     * @return The array of textual options.
     */
    private String[] getSetupOptions(){

        // Gets the message from runner. This is updated by the setState called for each player.
        ServerSetupUserMessage message = (ServerSetupUserMessage) runner.getInMsg();

        if( message.getUser().getNickName().equals(MatchSettings.getInstance().getClientNickname())) {

            // If it is this client's setup turn.
            if ( chosen== null )
                // If they haven't chosen their leader cards yet.
                cli.printMessage("[ ] You've received a selection of leader cards. Select \"Pick leader cards\" to continue.");

            if ( message.getResources() != 0) {
                if ( map== null)
                    // If they have received and not yet chosen some resources.
                    cli.printMessage("[ ] You've received " + message.getResources() + " resources. Select \"Pick starting resources\" to continue.");

                return new String[]{"See market", "See development card market", "See faith track", "Pick leader cards", "Pick starting resources"};

            } else

               return new String[]{"See market", "See development card market", "See faith track", "Pick leader cards"};
        } else {

            // Not their turn
            return new String[]{"See market", "See development card market", "See faith track"};
        }


    }

    /**
     * Switch that calls the various menu functions and handles the refresh.
     * This in turns calls the two "pick" functions. When they have both been done, the setup exits.
     */
    public void setupMenu() {
        boolean hasBeenRefreshed = false;
        do {
            String[] options = getSetupOptions();
            switch (cli.getChoice(options, hasBeenRefreshed, true)) {
                case 0 ->  hasBeenRefreshed = true;
                case 1 -> {
                    cli.printMessage(MARBLE_LEGEND);
                    cli.printMessage(GameView.getInstance().getMarketInstance());
                    hasBeenRefreshed = false;
                }
                case 2 -> {
                    cli.printMessage(GameView.getInstance().getDevelopmentCardsMarket());
                    hasBeenRefreshed = false;
                }
                case 3 -> {
                    runner.printFaithTracks();
                    hasBeenRefreshed = false;
                }
                case 4 -> {

                    // If the resources have already been picked and the cards haven't, send the message and exit setup.
                    if (pickLeaders() && map != null) {
                        UIController.getInstance().chosenStartingResources(map, chosen);
                        done = true;
                    }
                    else hasBeenRefreshed = false;
                }
                case 5 -> {

                    // If the cards have already been picked and the resources haven't, send the message and exit setup.
                    if (pickResources() && chosen != null) {
                        UIController.getInstance().chosenStartingResources(map, chosen);
                        done = true;
                    }
                    else  hasBeenRefreshed = false;
                }

            }
        } while (runner.getState()== MenuStates.SETUP && !done);
    }

    private boolean pickResources(){
        Map<Id, Resource> resIdMap = new HashMap<>();

        int maxResources = ((ServerSetupUserMessage) runner.getInMsg()).getResources();

        if ( map != null ) {
            cli.printMessage("[X] You have already picked your resources");
            return false;
        }

        while (maxResources > 0 ){
            Resource resource = cli.getResource(maxResources);

            // Fixing the depots. The user is able to rearrange them as soon as the game starts.
            // Only two cases: the player chooses max 2 resources.
            if (maxResources == 2)
                resIdMap.put(Id.DEPOT_2, resource);
            else if (resIdMap.get(Id.DEPOT_2)!= null && resource.getResourceType()== resIdMap.get(Id.DEPOT_2).getResourceType())
                resIdMap.get(Id.DEPOT_2).add(resource);
            else
                resIdMap.put(Id.DEPOT_3, resource);

            maxResources -= resource.getQuantity();

        }

        map = resIdMap;

        cli.printMessage("["+CHECK_MARK+"] Your resources have been placed in your depots. You'll be able to rearrange them freely once the game begins.");
        return true;
    }

    private boolean pickLeaders() {
        var cards4 = ((ServerSetupUserMessage) runner.getInMsg()).getLeaderCards();
        LeaderCard[] leaderCards = new LeaderCard[2];

        if (chosen != null) {
            cli.printMessage("[X] You have already picked your cards");
            return false;
        }

        // Prepare the cards for printing as choices
        String[] cardOptions = new String[4];
        for (int i = 0; i < cards4.length; i++) {
            cardOptions[i]="\n" + cards4[i].toString();
        }

        cli.printMessage("[ ] Pick one from these leader cards: (1/2 cards to pick)");
        int choice = cli.getChoice(cardOptions);

        // Get selected card and remove it from the possible choices.
        leaderCards[0] = cards4[choice-1];
        cards4[choice-1] = null;

        // Prepare new options, this time overwriting the already picked card.
        cardOptions = new String[4];

        for (int i = 0; i < cards4.length; i++) {
            if (cards4[i]!= null)  cardOptions[i]= "\n" + cards4[i].toString();
            else cardOptions[i]= "You already picked this card!\n";
        }

        cli.printMessage("[ ] Pick one from these leader cards: (2/2 cards to pick)");

        // And repeat the selection if the player tries to pick the same one as before.
        int choice2;
        do {
            choice2 = cli.getChoice(cardOptions);
        } while (choice == choice2);

        // Save the selected card.
        leaderCards[1] = cards4[choice2-1];

        // Since the pick resource option is not available to the first player, set the map to empty for them here.
        if (((ServerSetupUserMessage)runner.getInMsg()).getResources() == 0)
            // The user doesn't have resources, so they don't need to pick them.
            map = new HashMap<>();

        chosen = leaderCards;

        return true;

    }



}
