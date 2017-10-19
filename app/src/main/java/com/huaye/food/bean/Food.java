package com.huaye.food.bean;

import cn.bmob.v3.BmobObject;

public class Food extends BmobObject {

    private int restaurantId;
    private int type;
    private String calories;
    private String name;
    private String pic;
    private int week;

    public Food(int restaurantId, int type, String calories, String name, String pic) {
        this.restaurantId = restaurantId;
        this.type = type;
        this.calories = calories;
        this.name = name;
        this.pic = pic;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

}
