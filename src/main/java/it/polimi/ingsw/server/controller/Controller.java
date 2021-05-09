package it.polimi.ingsw.server.controller;


import it.polimi.ingsw.server.model.Model;

public class Controller {
    private static Controller singleton;
    private final DevelopmentCardMarketController developmentCardMarketController;
    private final LeaderCardsController leaderCardsController;
    private final MarketController marketController;
    private final ResourceController resourceController;
    private final TurnController turnController;

    public static Controller getInstance(Model model){
        if(singleton == null){
            singleton = new Controller(model);
        }

        return singleton;
    }

    private Controller(Model model){
        developmentCardMarketController = DevelopmentCardMarketController.getInstance(model);
        leaderCardsController = LeaderCardsController.getInstance(model);
        marketController = MarketController.getInstance(model);
        resourceController = ResourceController.getInstance(model);
        turnController = TurnController.getInstance(model);
    }

}
