package com.huaye.food.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;

/**
 * Created by sunhuahui on 2017/10/21.
 */

public class Caleras extends BmobObject {
    private BmobUser user;

    private Float cal;

    public BmobUser getUser() {
        return user;
    }

    public Caleras setUser(BmobUser user) {
        this.user = user;
        return this;
    }

    public Float getCal() {
        return cal;
    }

    public Caleras setCal(Float cal) {
        this.cal = cal;
        return this;
    }
}
