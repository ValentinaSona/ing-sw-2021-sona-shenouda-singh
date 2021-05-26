package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.exception.VaticanReportException;
import it.polimi.ingsw.server.model.PopeFavorTiles;
import it.polimi.ingsw.server.model.Resource;
import it.polimi.ingsw.server.model.ResourceType;
import it.polimi.ingsw.utils.networking.Transmittable;
import it.polimi.ingsw.utils.observer.LambdaObservable;

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
			if (faithMarker >= 8 && getPopeFavorTiles(0) == PopeFavorTiles.DOWNWARDS ){
				throw new VaticanReportException(1);
			} else if(faithMarker >= 16 && getPopeFavorTiles(1) == PopeFavorTiles.DOWNWARDS){
				throw new VaticanReportException(2);

			} else if(faithMarker >= 24 && getPopeFavorTiles(2) == PopeFavorTiles.DOWNWARDS){
				throw new VaticanReportException(3);
			}
		}



	}

	public void addFaithPoints(int faith) throws VaticanReportException {
		this.addFaithPoints(new Resource(faith, ResourceType.FAITH));
	}

	public void validatePopeFavor(int report) {

		switch (report) {
			case 1:
				if (faithMarker >= 5 && popeFavorTiles[0] != PopeFavorTiles.DISMISSED) {
					popeFavorTiles[0] = PopeFavorTiles.UPWARDS;
				} else {
					popeFavorTiles[0] = PopeFavorTiles.DISMISSED;
				}

				break;
			case 2:

				if (faithMarker >= 12 && popeFavorTiles[1] != PopeFavorTiles.DISMISSED) {
					popeFavorTiles[1] = PopeFavorTiles.UPWARDS;
				} else {
					popeFavorTiles[1] = PopeFavorTiles.DISMISSED;
				}

				break;
			case 3:

				if (faithMarker >= 19 && popeFavorTiles[2] != PopeFavorTiles.DISMISSED) {
					popeFavorTiles[2] = PopeFavorTiles.UPWARDS;
				} else {
					popeFavorTiles[2] = PopeFavorTiles.DISMISSED;
				}
				break;
			default:
				throw new RuntimeException("Invalid number, reports range from 1 to 3.");
		}
	}

}