package com.huaye.food.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;

public class Comment extends BmobObject {

	private String foodId;
	private String content;
	private BmobUser user;

	public String getFoodId() {
		return foodId;
	}

	public void setFoodId(String foodId) {
		this.foodId = foodId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public BmobUser getUser() {
		return user;
	}

	public void setUser(BmobUser user) {
		this.user = user;
	}

}
