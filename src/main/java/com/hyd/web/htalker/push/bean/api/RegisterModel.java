package com.hyd.web.htalker.push.bean.api;

import com.google.gson.annotations.Expose;

/**
 * Description: hTalker
 * Created by Administrator on 2019/10/25 11:04
 */
public class RegisterModel {

    @Expose
    private String account;
    @Expose
    private String password;
    @Expose
    private String name;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
