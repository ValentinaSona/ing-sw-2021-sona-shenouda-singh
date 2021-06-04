package it.polimi.ingsw.utils.networking.transmittables.servermessages;

import it.polimi.ingsw.client.modelview.DevMarketView;
import it.polimi.ingsw.client.ui.controller.DispatcherController;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.utils.networking.ClientHandleable;

import java.util.Map;

public class ServerFinalScoreMessage  implements ServerMessage, ClientHandleable {
        private final Map<User, Integer> rank;

        public ServerFinalScoreMessage( Map<User, Integer> rank){

            this.rank = rank;
        }

        @Override
        public boolean handleMessage(DispatcherController handler) {
            handler.handleFinalScore(this);
            return true;
        }

         public Map<User, Integer> getRank() {
            return rank;
        }
}



