package com.hyd.web.htalker.push.factory;

import com.hyd.web.htalker.push.bean.api.message.MessageCreateModel;
import com.hyd.web.htalker.push.bean.db.Group;
import com.hyd.web.htalker.push.bean.db.Message;
import com.hyd.web.htalker.push.bean.db.User;
import com.hyd.web.htalker.push.utils.Hib;

/**
 * Description: 消息数据存储的类
 * Created by Administrator on 2019/11/14 10:05
 */
public class MessageFactory {

    // 查询某一个消息
    public static Message findById(String id) {
        return Hib.query(session -> session.get(Message.class, id));
    }

    // 添加一条单聊消息
    public static Message add(User sender, User receiver, MessageCreateModel model) {
        Message message = new Message(sender, receiver, model);
        return save(message);
    }

    // 添加一条群聊消息
    public static Message add(User sender, Group group, MessageCreateModel model) {
        Message message = new Message(sender, group, model);
        return save(message);
    }

    private static Message save(Message message) {
        return Hib.query(session -> {
            session.save(message);

            // 写入到数据库
            session.flush();

            // 紧接着从数据库中查询出来
            session.refresh(message);
            return message;
        });
    }
}
