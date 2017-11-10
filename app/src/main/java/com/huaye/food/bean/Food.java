package com.huaye.food.bean;

import cn.bmob.v3.BmobObject;

public class Food extends BmobObject {

    private int restaurantId;
    private int type;
    private float calories;
    private String name;
    private String pic;
    private int week;
    private int calorieLevel;

    public Food(){

    }
    public Food(int restaurantId, int type, float calories, String name, String pic) {
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

    public Float getCalories() {
        return calories;
    }

    public Food setCalories(float calories) {
        this.calories = calories;
        return this;
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

    public int getCalorieLevel() {
        return calorieLevel;
    }

    public Food setCalorieLevel(int calorieLevel) {
        this.calorieLevel = calorieLevel;
        return this;
    }
}
