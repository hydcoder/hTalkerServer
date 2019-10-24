package com.hyd.web.htalker.push.service;

import com.hyd.web.htalker.push.bean.db.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Description: hTalker
 * Created by Administrator on 2019/10/16 10:00
 */
// 127.0.0.1/api/account/...
@Path("/account")
public class AccountService {

    @GET
    @Path("/login")
    public String get() {
        return "you get the login";
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public User post() {
        User user = new User();
        user.setName("美女");
        user.setSex(2);
        return user;
    }
}
