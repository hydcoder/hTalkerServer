package com.hyd.web.htalker.push.factory;

import com.google.common.base.Strings;
import com.hyd.web.htalker.push.bean.api.base.PushModel;
import com.hyd.web.htalker.push.bean.card.GroupMemberCard;
import com.hyd.web.htalker.push.bean.card.MessageCard;
import com.hyd.web.htalker.push.bean.card.UserCard;
import com.hyd.web.htalker.push.bean.db.*;
import com.hyd.web.htalker.push.utils.Hib;
import com.hyd.web.htalker.push.utils.PushDispatcher;
import com.hyd.web.htalker.push.utils.TextUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Description: 消息处理与存储的工具类
 * Created by hydCoder on 2019/11/14 10:59
 */
@SuppressWarnings("Duplicates")
public class PushFactory {

    // 发送一条消息并在当前的发送历史记录中记录
    public static void pushNewMessage(User sender, Message message) {
        if (sender == null || message == null) {
            return;
        }

        // 消息卡片用于发送
        MessageCard card = new MessageCard(message);
        // 要推送的消息json字符串
        String entity = TextUtil.toJson(card);

        // 发送者
        PushDispatcher dispatcher = new PushDispatcher();

        if (message.getGroup() == null && Strings.isNullOrEmpty(message.getGroupId())) {
            // 单聊
            User receiver = UserFactory.findById(message.getReceiverId());
            if (receiver == null) {
                return;
            }

            // 历史记录表字段建立
            PushHistory history = new PushHistory();
            // 普通消息类型
            history.setEntityType(PushModel.ENTITY_TYPE_MESSAGE);
            history.setEntity(entity);
            history.setReceiver(receiver);
            // 接收者当前的设备推送id
            history.setReceiverPushId(receiver.getPushId());

            // 推送的真实model
            PushModel pushModel = new PushModel();
            // 每一条历史记录都是独立的，可以单独发送
            pushModel.add(history.getEntityType(), history.getEntity());

            // 把需要发送的数据，丢给发送者进行发送
            dispatcher.add(receiver, pushModel);
            // 保存到数据库
            Hib.queryOnly(session -> session.save(history));
        } else {
            // 群聊
            Group group = message.getGroup();
            if (group == null) {
                // 因为延迟加载情况可能为null，需要通过Id查询
                group = GroupFactory.findById(message.getGroupId());
            }
            // 如果群真的没有，则直接返回
            if (group == null) {
                return;
            }

            // 给群成员发送消息
            Set<GroupMember> members = GroupFactory.getMembers(group);
            if (members == null || members.size() == 0) {
                return;
            }

            // 过滤掉发送者自己本身
            members = members.stream()
                    .filter(groupMember -> !groupMember.getUserId()
                            .equalsIgnoreCase(sender.getId()))
                    .collect(Collectors.toSet());

            if (members.size() == 0) {
                return;
            }
            // 一个历史记录列表
            List<PushHistory> histories = new ArrayList<>();

            addGroupMembersPushModel(dispatcher,   // 推送的发送工具
                    histories,    // 数据库要存储的历史消息列表
                    members,   // 所有的成员
                    entity,    // 要发送的消息内容
                    PushModel.ENTITY_TYPE_MESSAGE); // 发送的类型

            // 保存到数据库的操作
            Hib.queryOnly(session -> {
                for (PushHistory history : histories) {
                    session.saveOrUpdate(history);
                }
            });
        }

        // 发送者进行真实的发送提交
        dispatcher.submit();
    }

    /**
     * 给群成员构建一个消息，
     * 把消息存储到数据库的历史记录中，每个人，每条消息都是一个记录
     */
    private static void addGroupMembersPushModel(PushDispatcher dispatcher, List<PushHistory> histories, Set<GroupMember> members, String entity, int entityTypeMessage) {
        for (GroupMember member : members) {
            // 无须再通过Id去找用户
            User receiver = member.getUser();
            if (receiver == null) {
                return;
            }

            // 历史记录表字段建立
            PushHistory history = new PushHistory();
            history.setEntityType(entityTypeMessage);
            history.setEntity(entity);
            history.setReceiver(receiver);
            history.setReceiverPushId(receiver.getPushId());
            histories.add(history);

            // 构建一个消息Model
            PushModel pushModel = new PushModel();
            pushModel.add(history.getEntityType(), history.getEntity());

            // 添加到发送者的数据集中
            dispatcher.add(receiver, pushModel);
        }
    }

    /**
     * 通知新加入的成员你加入了xxx群
     *
     * @param members 新加入的成员
     */
    public static void pushJoinGroup(Set<GroupMember> members) {

        // 发送者
        PushDispatcher dispatcher = new PushDispatcher();
        // 一个历史记录列表
        List<PushHistory> histories = new ArrayList<>();

        for (GroupMember member : members) {
            // 无须再通过Id去找用户
            User receiver = member.getUser();
            if (receiver == null) {
                return;
            }

            // 每个成员的消息卡片
            GroupMemberCard memberCard = new GroupMemberCard(member);
            String entity = TextUtil.toJson(memberCard);

            // 历史记录表字段建立
            PushHistory history = new PushHistory();
            // 普通消息类型
            history.setEntityType(PushModel.ENTITY_TYPE_ADD_GROUP);
            history.setEntity(entity);
            history.setReceiver(receiver);
            // 接收者当前的设备推送id
            history.setReceiverPushId(receiver.getPushId());

            // 构建一个消息Model
            PushModel pushModel = new PushModel()
                    .add(history.getEntityType(), history.getEntity());

            // 添加到发送者的数据集中
            dispatcher.add(receiver, pushModel);
            histories.add(history);
        }

        // 保存到数据库的操作
        Hib.queryOnly(session -> {
            for (PushHistory history : histories) {
                session.saveOrUpdate(history);
            }
        });

        // 发送者进行真实的发送提交
        dispatcher.submit();
    }

    /**
     * 通知群里的老成员有新成员加入了群
     *
     * @param oldMembers  老成员
     * @param insertCards 新加入的成员
     */
    public static void pushGroupHaveMemberJoin(Set<GroupMember> oldMembers, List<GroupMemberCard> insertCards) {
        // 发送者
        PushDispatcher dispatcher = new PushDispatcher();
        // 一个历史记录列表
        List<PushHistory> histories = new ArrayList<>();

        // 当前新增的成员的集合的json字符串
        String entity = TextUtil.toJson(insertCards);

        // 进行循环添加，给每一个老的成员构建一个消息，消息的内容为新增的成员的集合
        // 通知的类型是：群成员添加了的消息类型
        addGroupMembersPushModel(dispatcher, histories, oldMembers, entity, PushModel.ENTITY_TYPE_ADD_GROUP_MEMBERS);

        // 保存到数据库的操作
        Hib.queryOnly(session -> {
            for (PushHistory history : histories) {
                session.saveOrUpdate(history);
            }
        });

        // 发送者进行真实的发送提交
        dispatcher.submit();
    }

    /**
     * 推送账户退出的消息
     *
     * @param receiver 接收者
     * @param pushId   这个时刻的接收者的设备Id
     */
    public static void pushLogout(User receiver, String pushId) {
        // 历史记录表字段建立
        PushHistory history = new PushHistory();
        history.setEntityType(PushModel.ENTITY_TYPE_LOGOUT);
        history.setEntity("Account logout!!!");
        history.setReceiver(receiver);
        history.setReceiverPushId(pushId);

        // 保存到数据库
        Hib.queryOnly(session -> session.save(history));

        // 发送者
        PushDispatcher dispatcher = new PushDispatcher();

        // 具体发送的内容
        PushModel pushModel = new PushModel();
        pushModel.add(history.getEntityType(), history.getEntity());

        // 添加到发送者的数据集中
        dispatcher.add(receiver, pushModel);
        dispatcher.submit();
    }

    /**
     * 给一个朋友推送我的信息过去
     * 类型是：我关注了他
     * @param receiver 接收者
     * @param userCard 我的卡片信息
     */
    public static void pushFollow(User receiver, UserCard userCard) {
        // 一定是相互关注了
        userCard.setFollow(true);
        String entity = TextUtil.toJson(userCard);
        // 历史记录表字段建立
        PushHistory history = new PushHistory();
        history.setEntityType(PushModel.ENTITY_TYPE_ADD_FRIEND);
        history.setEntity(entity);
        history.setReceiver(receiver);
        history.setReceiverPushId(receiver.getPushId());
        // 保存到历史记录表
        Hib.queryOnly(session -> session.save(history));

        // 发送者
        PushDispatcher dispatcher = new PushDispatcher();

        // 具体发送的内容
        PushModel pushModel = new PushModel();
        pushModel.add(history.getEntityType(), history.getEntity());

        // 添加到发送者的数据集中
        dispatcher.add(receiver, pushModel);
        dispatcher.submit();
    }

    /**
     * 推送朋友圈消息
     * @param receiver 接受者
     */
    public static void pushFriendCircle(User receiver) {

        PushHistory history = new PushHistory();
        history.setEntityType(PushModel.ENTITY_TYPE_FRIEND_CIRCLE);
        history.setEntity("你有一条新消息");
        history.setReceiver(receiver);
        //当前接受者的设备id
        history.setReceiverPushId(receiver.getPushId());
        Hib.queryOnly(session -> session.save(history));
        //发送者
        PushDispatcher dispatcher = new PushDispatcher();
        //具体推送的内容
        PushModel model = new PushModel()
                .add(history.getEntityType(), history.getEntity());
        dispatcher.add(receiver,model);
        //发送者
        dispatcher.submit();
    }
}
