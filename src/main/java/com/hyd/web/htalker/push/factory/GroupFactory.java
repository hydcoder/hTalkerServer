package com.hyd.web.htalker.push.factory;

import com.google.common.base.Strings;
import com.hyd.web.htalker.push.bean.api.group.GroupCreateModel;
import com.hyd.web.htalker.push.bean.db.Group;
import com.hyd.web.htalker.push.bean.db.GroupMember;
import com.hyd.web.htalker.push.bean.db.User;
import com.hyd.web.htalker.push.utils.Hib;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Description: 群数据处理类
 * Created by hydCoder on 2019/11/14 15:16
 */
public class GroupFactory {

    // 通过id拿群model
    public static Group findById(String groupId) {
        return Hib.query(session -> session.get(Group.class, groupId));
    }

    // 查询一个群, 同时该User必须为群的成员，否则返回null
    public static Group findById(User user, String groupId) {
        GroupMember member = getMember(user.getId(), groupId);
        if (member != null) {
            return member.getGroup();
        }
        return null;
    }

    // 通过名字查找群
    public static Group findByName(String name) {
        return Hib.query(session -> (Group) session
                .createQuery("from Group where lower(name)=:name")
                .setParameter("name", name)
                .uniqueResult());
    }

    // 查询一个群的所有成员
    public static Set<GroupMember> getMembers(Group group) {
        return Hib.query(session -> {
            @SuppressWarnings("unchecked")
            List<GroupMember> members = session
                    .createQuery("from GroupMember where group=:group")
                    .setParameter("group", group)
                    .list();
            return new HashSet<>(members);
        });
    }

    // 查询一个人加入的所有群
    public static Set<GroupMember> getMembers(User user) {
        return Hib.query(session -> {
            @SuppressWarnings("unchecked")
            List<GroupMember> members = session
                    .createQuery("from GroupMember where user=:user")
                    .setParameter("user", user)
                    .list();
            return new HashSet<>(members);
        });
    }

    // 创建群
    public static Group create(User creator, GroupCreateModel model, List<User> users) {
        return Hib.query(session -> {
            Group group = new Group(creator, model);
            session.save(group);

            GroupMember ownerMember = new GroupMember(creator, group);
            // 设置超级权限，创建者
            ownerMember.setPermissionType(GroupMember.PERMISSION_TYPE_ADMIN_SU);
            // 保存
            session.save(ownerMember);

            for (User user : users) {
                GroupMember groupMember = new GroupMember(user, group);
                session.save(groupMember);
            }

//            session.flush();
//            session.load(group, group.getId());

            return group;
        });
    }

    // 获取一个群的成员
    public static GroupMember getMember(String userId, String groupId) {
        return Hib.query(session -> (GroupMember) session
                .createQuery("from GroupMember where userId=:userId and groupId=:groupId")
                .setParameter("userId", userId)
                .setParameter("groupId", groupId)
                .setMaxResults(1)
                .uniqueResult()
        );
    }

    @SuppressWarnings("unchecked")
    public static List<Group> search(String name) {
        if (Strings.isNullOrEmpty(name)) {
            name = "";   // 保证不能为null的情况，减少后面的一些判断和额外的错误
        }
        final String searchName = "%" + name + "%";

        return Hib.query(session -> {
            // 查询的条件：name忽略大小写，并且使用like(模糊)查询；头像和描述必须完善才能被查询到
            return (List<Group>) session.createQuery("from Group where lower(name) like :name")
                    .setParameter("name", searchName)
                    .setMaxResults(20)
                    .list();
        });
    }

    /**
     * 添加成员到某个群
     * @param group 要添加的群
     * @param insertUsers 要加入群的成员
     * @return 加入群的成员
     */
    public static Set<GroupMember> addMembers(Group group, List<User> insertUsers) {
        return Hib.query(session -> {
            Set<GroupMember> groupMembers = new HashSet<>();
            for (User user : insertUsers) {
                GroupMember groupMember = new GroupMember(user, group);
                session.save(groupMember);
                // 此时session并没有进行外键的关联查询，所以groupMember里的user和group是null
                groupMembers.add(groupMember);
            }

            // 第一种解决办法，进行数据刷新
            /*
            for (GroupMember groupMember : groupMembers) {
                // 进行刷新，会进行关联查询，但是在循环中刷新消耗较高
                session.refresh(groupMember);
            }
            */
            // 第二种解决办法，GroupMemberCard构造方法中改成member.getUser().getId()和member.getGroup().getId()
            return groupMembers;
        });
    }
}
