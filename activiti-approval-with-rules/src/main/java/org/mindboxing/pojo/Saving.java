package org.mindboxing.pojo;

import java.io.Serializable;

public class Saving implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private float amount;
	private float percentage;

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public float getPercentage() {
		return percentage;
	}

	public void setPercentage(float percentage) {
		this.percentage = percentage;
	}
	
	
}
