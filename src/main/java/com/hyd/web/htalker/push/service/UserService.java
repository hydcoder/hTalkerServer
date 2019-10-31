package com.hyd.web.htalker.push.service;

import com.google.common.base.Strings;
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

        if (Strings.isNullOrEmpty(followId) || followId.equalsIgnoreCase(self.getId())) {
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

    // 获取某个用户的信息
    @GET
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<UserCard> getUser(@PathParam("id") String id) {
        if (Strings.isNullOrEmpty(id)) {
            // 返回参数异常
            return ResponseModel.buildParameterError();
        }

        User self = getSelf();
        if (self.getId().equalsIgnoreCase(id)) {
            // 返回自己，不必查询数据库
            return ResponseModel.buildOk(new UserCard(self, true));
        }

        User user = UserFactory.findById(id);
        if (user == null) {
            // 没有找到该用户
            return ResponseModel.buildNotFoundUserError(null);
        }

        boolean isFollow = UserFactory.getUserFollow(self, user) != null;
        return ResponseModel.buildOk(new UserCard(user, isFollow));
    }

    // 搜索人的接口实现
    // 为了简化分页，只返回20条数据
    @GET    // 搜索人，不涉及数据更改，只是查询，则为GET
    @Path("/search/{name:(.*)?}")  // 名字为任意字符，可以为空
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<UserCard>> search(@DefaultValue("") @PathParam("name") String name) {
        User self = getSelf();

        // 先查询数据
        List<User> searchUsers = UserFactory.search(name);
        // 把查询的人封装为UserCard
        // 判断这些人中是否已经有我关注的人
        // 如果有，则返回的关注状态中应该已经设置好状态

        // 查询到我的联系人列表
        List<User> contacts = UserFactory.contacts(self);

        // 把 List<User> -> List<UserCard>
        List<UserCard> userCards = searchUsers.stream()
                .map(user -> {
                    // 判断这个人是否是我自己或者是我的联系人中的人
                    boolean isFollow = user.getId().equalsIgnoreCase(self.getId())
                            // 进行联系人的任意匹配，匹配其中的id字段
                            || contacts.stream().anyMatch(
                                    contactUser -> contactUser.getId().equalsIgnoreCase(user.getId()));
                            return new UserCard(user, isFollow);
                }).collect(Collectors.toList());

        return ResponseModel.buildOk(userCards);
    }
}
