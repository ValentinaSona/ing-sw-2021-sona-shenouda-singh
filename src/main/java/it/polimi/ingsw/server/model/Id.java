package it.polimi.ingsw.server.model;

public enum Id {
    DEPOT_1(0),
    DEPOT_2(1),
    DEPOT_3(2),
    S_DEPOT_1(3),
    S_DEPOT_2(4),
    STRONGBOX_SHIELD(0),
    STRONGBOX_COIN(0),
    STRONGBOX_STONE(0),
    STRONGBOX_SERVANT(0),
    BOARD_PRODUCTION(0),
    SLOT_1(1),
    SLOT_2(2),
    SLOT_3(3),
    S_SLOT_1(4),
    S_SLOT_2(5),
    LEADER_CARD_1(0),
    LEADER_CARD_2(1);

    private final int value;

    private Id(int value){
        this.value =value;
    }
    public int getValue() {
        return value;
    }
}
