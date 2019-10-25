package com.hyd.web.htalker.push.service;

import com.hyd.web.htalker.push.bean.api.RegisterModel;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Description: hTalker
 * Created by Administrator on 2019/10/16 10:00
 */
// 127.0.0.1/api/account/...
@Path("/account")
public class AccountService {

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RegisterModel register(RegisterModel registerModel) {
        return registerModel;
        /*User user = new User();
        user.setName(registerModel.getName());
        user.setSex(2);
        return user;*/
    }
}
