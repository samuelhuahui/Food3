package com.huaye.food.bean;

import cn.bmob.v3.BmobObject;

public class Score extends BmobObject {
	private float scroe;
	private String restaurant;

	public float getScroe() {
		return scroe;
	}

	public void setScroe(float scroe) {
		this.scroe = scroe;
	}

	public String getRestaurant() {
		return restaurant;
	}

	public void setRestaurant(String restaurant) {
		this.restaurant = restaurant;
	}

}
