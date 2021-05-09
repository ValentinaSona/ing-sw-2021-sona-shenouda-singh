package it.polimi.ingsw.model;

public enum Id {
    DEPOT_1(0),
    DEPOT_2(1),
    DEPOT_3(2),
    S_DEPOT_1(3),
    S_DEPOT_2(4),
    STRONGBOX(-1),
    BOARD_PRODUCTION(0),
    SLOT_1(1),
    SLOT_2(2),
    SLOT_3(3),
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
