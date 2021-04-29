package it.polimi.ingsw.model;

public class FaithTrack {


	private int faithMarker;

	private PopeFavorTiles[] popeFavorTiles;

	public FaithTrack() {
		this.popeFavorTiles = new PopeFavorTiles[]{PopeFavorTiles.DOWNWARDS, PopeFavorTiles.DOWNWARDS,PopeFavorTiles.DOWNWARDS};
		this.faithMarker = 0;
	}

	public int getFaithMarker() {
		return faithMarker;
	}

	public PopeFavorTiles getPopeFavorTiles(int index) {
		return popeFavorTiles[index];
	}

	//TODO: add VaticanReport exceptions code
	public void addFaithPoints(Resource faith) {
		if (faith.getResourceType() != ResourceType.FAITH){
			throw new RuntimeException("Can only add Faith to FaithTrack");
		} else {
			faithMarker += faith.getQuantity();
		}
	}

	public void addFaithPoints(int faith) {
		faithMarker += faith;
	}

	public void validatePopeFavor(int report) {

		if (report == 1) {
			if (faithMarker >= 5 && popeFavorTiles[0] != PopeFavorTiles.DISMISSED) {
				popeFavorTiles[0] = PopeFavorTiles.UPWARDS;
			} else {
				popeFavorTiles[0] = PopeFavorTiles.DISMISSED;
			}

		} else if (report == 2) {

			if (faithMarker >= 12 && popeFavorTiles[1] != PopeFavorTiles.DISMISSED) {
				popeFavorTiles[1] = PopeFavorTiles.UPWARDS;
			} else {
				popeFavorTiles[1] = PopeFavorTiles.DISMISSED;
			}

		} else if (report == 3) {

			if (faithMarker >= 19 && popeFavorTiles[2] != PopeFavorTiles.DISMISSED) {
				popeFavorTiles[2] = PopeFavorTiles.UPWARDS;
			} else {
				popeFavorTiles[2] = PopeFavorTiles.DISMISSED;
			}
		}
	}

}
