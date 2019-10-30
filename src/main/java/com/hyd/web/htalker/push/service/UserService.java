package com.hyd.web.htalker.push.service;

import com.hyd.web.htalker.push.bean.api.base.ResponseModel;
import com.hyd.web.htalker.push.bean.api.user.UpdateInfoModel;
import com.hyd.web.htalker.push.bean.card.UserCard;
import com.hyd.web.htalker.push.bean.db.User;
import com.hyd.web.htalker.push.factory.UserFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Description: 用户信息处理的Service
 * Created by Administrator on 2019/10/25 16:10
 */
// 127.0.0.1/api/user/...
@Path("/user")
public class UserService extends BaseService {

    // 用户信息修改接口
    // 返回自己的个人信息
    @PUT
    //@Path("") //127.0.0.1/api/user 不需要写，就是当前目录
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<UserCard> update(UpdateInfoModel model) {
        if (!UpdateInfoModel.check(model)) {
            return ResponseModel.buildParameterError();
        }

        User self = getSelf();
//        User user = UserFactory.findByToken(token);
        // 更新用户信息
        self = model.updateToUser(self);
        self = UserFactory.update(self);
        // 构架自己的用户信息
        UserCard card = new UserCard(self, true);
        // 返回
        return ResponseModel.buildOk(card);
    }

    // 获取我的联系人列表
    @GET
    @Path("/contact")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<UserCard>> contact() {
        User self = getSelf();

        // 拿到我的所有联系人
        List<User> contacts = UserFactory.contacts(self);

        // 转换为UserCard
        List<UserCard> userCards = contacts.stream()
                // map操作，相当于转置操作  eg：User -> UserCard
                .map(user -> new UserCard(user, true)).collect(Collectors.toList());
        return ResponseModel.buildOk(userCards);
    }

    // 关注某个用户
    @PUT   // 修改类用PUT
    @Path("/follow/{followId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<UserCard> follow(@PathParam("followId") String followId) {
        User self = getSelf();

        if (followId.equalsIgnoreCase(self.getId())) {
            // 自己不能关注自己，返回参数异常
            return ResponseModel.buildParameterError();
        }

        // 找到要关注的用户
        User followUser = UserFactory.findById(followId);
        if (followUser == null) {
            // 未找到要关注的人
            return ResponseModel.buildNotFoundUserError(null);
        }

        // 备注默认没有，后面可以扩展
        followUser = UserFactory.follow(self, followUser, null);
        if (followUser == null) {
            // 关注失败，返回服务器异常
            return ResponseModel.buildServiceError();
        }

        // TODO 通知我关注的人，我关注了他

        return ResponseModel.buildOk(new UserCard(followUser, true));
    }
}
