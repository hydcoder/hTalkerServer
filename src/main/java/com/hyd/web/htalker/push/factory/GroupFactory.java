package com.hyd.web.htalker.push.factory;

import com.hyd.web.htalker.push.bean.db.Group;
import com.hyd.web.htalker.push.bean.db.GroupMember;
import com.hyd.web.htalker.push.bean.db.User;

import java.util.Set;

/**
 * Description: 群数据处理类
 * Created by hydCoder on 2019/11/14 15:16
 */
public class GroupFactory {

    public static Group findById(String groupId) {
        // TODO 查询一个群
        return null;
    }

    public static Group findById(User user, String groupId) {
        // TODO 查询一个群, 同时该User必须为群的成员，否则返回null
        return null;
    }

    public static Set<GroupMember> getMembers(Group group) {
        // TODO 查询一个群的成员
        return null;
    }
}
