package com.hyd.web.htalker.push.service;

import com.google.common.base.Strings;
import com.hyd.web.htalker.push.bean.api.base.ResponseModel;
import com.hyd.web.htalker.push.bean.api.group.GroupCreateModel;
import com.hyd.web.htalker.push.bean.api.group.GroupMemberAddModel;
import com.hyd.web.htalker.push.bean.api.group.GroupMemberUpdateModel;
import com.hyd.web.htalker.push.bean.card.ApplyCard;
import com.hyd.web.htalker.push.bean.card.GroupCard;
import com.hyd.web.htalker.push.bean.card.GroupMemberCard;
import com.hyd.web.htalker.push.bean.db.Group;
import com.hyd.web.htalker.push.bean.db.GroupMember;
import com.hyd.web.htalker.push.bean.db.User;
import com.hyd.web.htalker.push.factory.GroupFactory;
import com.hyd.web.htalker.push.factory.PushFactory;
import com.hyd.web.htalker.push.factory.UserFactory;
import com.hyd.web.htalker.push.provider.LocalDateTimeConverter;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Description: 群组的相关接口的入口
 * Created by hydCoder on 2019/11/26 16:41
 */
@SuppressWarnings("Duplicates")
@Path("/group")
public class GroupService extends BaseService {

    /**
     * 创建群
     *
     * @param model 基本参数
     * @return 群信息
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<GroupCard> create(GroupCreateModel model) {
        if (!GroupCreateModel.check(model)) {
            return ResponseModel.buildParameterError();
        }

        // 创建者
        User creator = getSelf();
        // 创建者并不在列表中
        model.getUsers().remove(creator.getId());
        if (model.getUsers().size() == 0) {
            return ResponseModel.buildParameterError();
        }

        // 检查是否已有同名的群
        if (GroupFactory.findByName(model.getName()) != null) {
            return ResponseModel.buildHaveNameError();
        }

        List<User> users = new ArrayList<>();
        for (String userId : model.getUsers()) {
            User user = UserFactory.findById(userId);
            if (user == null) {
                continue;
            }
            users.add(user);
        }
        // 如果没有一个成员
        if (users.size() == 0) {
            return ResponseModel.buildParameterError();
        }

        Group group = GroupFactory.create(creator, model, users);
        if (group == null) {
            // 服务器异常
            return ResponseModel.buildServiceError();
        }

        // 拿管理员的信息(自己的信息)
        GroupMember creatorMember = GroupFactory.getMember(creator.getId(), group.getId());
        if (creatorMember == null) {
            // 服务器异常
            return ResponseModel.buildServiceError();
        }

        // 拿到群的成员，给所有群成员发送已经被添加到群的信息
        Set<GroupMember> members = GroupFactory.getMembers(group);
        if (members == null) {
            // 服务器异常
            return ResponseModel.buildServiceError();
        }
        members = members.stream().filter(groupMember -> !groupMember.getId().equalsIgnoreCase(creatorMember.getId()))
                .collect(Collectors.toSet());

        // 开始发起推送
        PushFactory.pushJoinGroup(members);

        return ResponseModel.buildOk(new GroupCard(creatorMember));
    }

    /**
     * 搜索群，没有传递参数则搜索最近所有的群
     *
     * @param name 搜索的参数
     * @return 群信息列表
     */
    @GET
    @Path("/search/{name:(.*)?}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<GroupCard>> search(@PathParam("name") @DefaultValue("") String name) {
        User self = getSelf();
        List<Group> groups = GroupFactory.search(name);
        if (groups != null && groups.size() > 0) {
            List<GroupCard> groupCards = groups.stream()
                    .map(group -> {
                        GroupMember member = GroupFactory.getMember(self.getId(), group.getId());
                        return new GroupCard(group, member);
                    }).collect(Collectors.toList());
            return ResponseModel.buildOk(groupCards);
        }
        return ResponseModel.buildOk();
    }

    /**
     * 拉取自己当前的群的列表
     *
     * @param date 时间字段，如果不传递，则返回全部当前的群列表；否则返回这个时间之后所加入的群
     * @return 群信息列表
     */
    @GET
    @Path("/list/{date:(.*)?}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<GroupCard>> list(@PathParam("date") @DefaultValue("") String date) {
        User self = getSelf();

        // 拿到参数里的时间
        LocalDateTime dateTime = null;
        if (!Strings.isNullOrEmpty(date)) {
            try {
                dateTime = LocalDateTime.parse(date, LocalDateTimeConverter.FORMATTER);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Set<GroupMember> members = GroupFactory.getMembers(self);
        if (members == null || members.size() == 0) {
            return ResponseModel.buildOk();
        }

        LocalDateTime finalDateTime = dateTime;
        List<GroupCard> groupCards = members.stream()
                .filter(groupMember -> finalDateTime == null    // 时间如果为null则不过滤
                        || groupMember.getUpdateAt().isAfter(finalDateTime)) // 时间不为null，则时间需要在群的更新时间之后
                .map(GroupCard::new)   // 将groupMember转换为GroupCard
                .collect(Collectors.toList());

        return ResponseModel.buildOk(groupCards);
    }

    /**
     * 获取一个群的信息, 你必须是这个群的成员
     *
     * @param groupId 群id
     * @return 群信息
     */
    @GET
    @Path("/{groupId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<GroupCard> getGroup(@PathParam("groupId") String groupId) {
        if (Strings.isNullOrEmpty(groupId)) {
            return ResponseModel.buildParameterError();
        }

        User self = getSelf();
        GroupMember member = GroupFactory.getMember(self.getId(), groupId);
        if (member == null) {
            return ResponseModel.buildNotFoundGroupError(null);
        }

        return ResponseModel.buildOk(new GroupCard(member));
    }

    /**
     * 拉取指定群的所有成员，查询者必须是成员
     *
     * @param groupId 群id
     * @return 成员列表
     */
    @GET
    @Path("/{groupId}/members")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<GroupMemberCard>> members(@PathParam("groupId") String groupId)  {

        if (Strings.isNullOrEmpty(groupId)) {
            return ResponseModel.buildParameterError();
        }

        User self = getSelf();

        // 群是否存在
        Group group = GroupFactory.findById(groupId);
        if (group == null) {
            return ResponseModel.buildNotFoundGroupError(null);
        }

        // 检查是否是群里的成员
        GroupMember selfMember = GroupFactory.getMember(self.getId(), groupId);
        if (selfMember == null) {
            return ResponseModel.buildNoPermissionError();
        }

        // 拿到所有的成员
        Set<GroupMember> members = GroupFactory.getMembers(group);
        if (members == null) {
            return ResponseModel.buildServiceError();
        }

        List<GroupMemberCard> groupMemberCards = members.stream()
                .map(GroupMemberCard::new)
                .collect(Collectors.toList());

        return ResponseModel.buildOk(groupMemberCards);
    }

    /**
     * 给群添加成员的接口,调用者必须是群管理员
     *
     * @param groupId 群id
     * @param model   添加成员的model
     * @return 添加的成员列表
     */
    @POST
    @Path("/{groupId}/member")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<GroupMemberCard>> memberAdd(@PathParam("groupId") String groupId, GroupMemberAddModel model) {
        if (Strings.isNullOrEmpty(groupId) || GroupMemberAddModel.check(model)) {
            return ResponseModel.buildParameterError();
        }

        // 拿到我的信息
        User self = getSelf();

        // 移除我之后再判断数量
        model.getUsers().remove(self.getId());
        if (model.getUsers().size() == 0) {
            return ResponseModel.buildParameterError();
        }

        // 判断群是否存在
        Group group = GroupFactory.findById(groupId);
        if (group == null) {
            return ResponseModel.buildNotFoundGroupError(null);
        }

        // 我必须是该群的成员，同时是管理员或者创建者
        GroupMember selfMember = GroupFactory.getMember(self.getId(), groupId);
        if (selfMember == null || selfMember.getPermissionType() == GroupMember.PERMISSION_TYPE_NONE) {
            return ResponseModel.buildNoPermissionError();
        }

        // 拿到已有的成员
        Set<GroupMember> oldMembers = GroupFactory.getMembers(group);
        Set<String> oldMemberUserIds = oldMembers.stream()
                .map(GroupMember::getUserId)
                .collect(Collectors.toSet());

        List<User> insertUsers = new ArrayList<>();
        for (String userId : model.getUsers()) {
            // 要添加的用户是否存在
            User user = UserFactory.findById(userId);
            if (user == null) {
                continue;
            }

            // 要添加的用户是否已经是群成员
            if (oldMemberUserIds.contains(userId)) {
                continue;
            }

            insertUsers.add(user);
        }
        // 判断是否有要新增的成员
        if (insertUsers.size() == 0) {
            return ResponseModel.buildParameterError();
        }

        // 进行添加操作
        Set<GroupMember> insertMembers = GroupFactory.addMembers(group, insertUsers);
        if (insertMembers == null || insertMembers.size() == 0) {
            return ResponseModel.buildServiceError();
        }

        // 进行转换操作
        List<GroupMemberCard> insertCards = insertMembers.stream()
                .map(GroupMemberCard::new)
                .collect(Collectors.toList());

        // 进行通知，两部曲
        // 1.通知新增的群员，你被加入了XXX群
        PushFactory.pushJoinGroup(insertMembers);

        // 2.通知群中的老群员，有XXX，XXX等加入了群
        PushFactory.pushGroupHaveMemberJoin(oldMembers, insertCards);

        return ResponseModel.buildOk(insertCards);
    }

    /**
     * 更改成员信息，请求的人要么是管理员，要么是成员本人
     *
     * @param memberId 成员id，可以查询到对应的群，和人
     * @param model    修改的model
     * @return 当前成员的信息
     */
    @PUT
    @Path("/member/{memberId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<GroupMemberCard> modifyMember(@PathParam("memberId") String memberId, GroupMemberUpdateModel model) {
        return null;
    }

    /**
     * 申请加入一个群，此时会创建一个加入群的申请，并写入表；
     * 然后给管理员推送消息，管理员同意，其实就是调用添加成员的接口把对应的用户添加进去
     *
     * @param groupId 群id
     * @return 申请的信息
     */
    @POST
    @Path("/applyJoin/{groupId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<ApplyCard> join(@PathParam("groupId") String groupId) {
        return null;
    }
}
