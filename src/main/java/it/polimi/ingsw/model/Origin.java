package it.polimi.ingsw.model;

public enum Origin {
    DEPOT_1(0),
    DEPOT_2(1),
    DEPOT_3(2),
    S_DEPOT_1(3),
    S_DEPOT_2(4),
    STRONGBOX(0);

    private final int value;

    private Origin(int value){
        this.value =value;
    }
    public int getValue() {
        return value;
    }
}
