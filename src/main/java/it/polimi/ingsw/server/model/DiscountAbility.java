package it.polimi.ingsw.server.model;

public class DiscountAbility extends SpecialAbility {

	private final Resource discount;

	public DiscountAbility(Resource discount) {
		this.discount = discount;
	}

	public Resource getDiscount() {
		return discount;
	}

	@Override
	public String toString() {
		return "-" + discount;
	}
}
