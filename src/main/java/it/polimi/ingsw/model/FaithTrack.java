package it.polimi.ingsw.model;

import it.polimi.ingsw.exception.VaticanReportException;

import static it.polimi.ingsw.model.PopeFavorTiles.*;

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

	public void addFaithPoints(Resource faith) throws VaticanReportException {
		if (faith.getResourceType() != ResourceType.FAITH) {
			throw new RuntimeException("Can only add Faith to FaithTrack");
		} else {
			faithMarker += faith.getQuantity();
			if (faithMarker >= 8 && (getPopeFavorTiles(0) != DISMISSED || getPopeFavorTiles(0) != UPWARDS) ){
				throw new VaticanReportException("1");
			} else if(faithMarker >= 16 && (getPopeFavorTiles(1) != DISMISSED || getPopeFavorTiles(1) != UPWARDS)){
				throw new VaticanReportException("2");

			} else if(faithMarker >= 4 && (getPopeFavorTiles(2) != DISMISSED || getPopeFavorTiles(2) != UPWARDS)){
				throw new VaticanReportException("3");
			}
		}



	}

	public void addFaithPoints(int faith) throws VaticanReportException {
		faithMarker += faith;
		if (faithMarker >= 8 && (getPopeFavorTiles(0) != DISMISSED || getPopeFavorTiles(0) != UPWARDS) ){
			throw new VaticanReportException("1");
		} else if(faithMarker >= 16 && (getPopeFavorTiles(1) != DISMISSED || getPopeFavorTiles(1) != UPWARDS)){
			throw new VaticanReportException("2");

		} else if(faithMarker >= 4 && (getPopeFavorTiles(2) != DISMISSED || getPopeFavorTiles(2) != UPWARDS)){
			throw new VaticanReportException("3");
		}

	}

	public void validatePopeFavor(String report) {

		switch (report) {
			case "1":
				if (faithMarker >= 5 && popeFavorTiles[0] != DISMISSED) {
					popeFavorTiles[0] = PopeFavorTiles.UPWARDS;
				} else {
					popeFavorTiles[0] = DISMISSED;
				}

				break;
			case "2":

				if (faithMarker >= 12 && popeFavorTiles[1] != DISMISSED) {
					popeFavorTiles[1] = PopeFavorTiles.UPWARDS;
				} else {
					popeFavorTiles[1] = DISMISSED;
				}

				break;
			case "3":

				if (faithMarker >= 19 && popeFavorTiles[2] != DISMISSED) {
					popeFavorTiles[2] = PopeFavorTiles.UPWARDS;
				} else {
					popeFavorTiles[2] = DISMISSED;
				}
				break;
		}
	}

}
