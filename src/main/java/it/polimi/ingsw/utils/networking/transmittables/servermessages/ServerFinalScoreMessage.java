package it.polimi.ingsw.utils.networking.transmittables.servermessages;

import it.polimi.ingsw.client.modelview.DevMarketView;
import it.polimi.ingsw.client.ui.controller.DispatcherController;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.exception.EndOfGameCause;
import it.polimi.ingsw.utils.networking.ClientHandleable;

import java.util.Map;

public class ServerFinalScoreMessage  implements ServerMessage, ClientHandleable {
        private final Map<User, Integer> rank;
        private final EndOfGameCause cause;

        public ServerFinalScoreMessage(Map<User, Integer> rank, EndOfGameCause cause){

            this.rank = rank;
            this.cause = cause;
        }

        @Override
        public boolean handleMessage(DispatcherController handler) {
            handler.handleFinalScore(this);
            return true;
        }

    public EndOfGameCause getCause() {
        return cause;
    }

    public Map<User, Integer> getRank() {
            return rank;
        }
}



