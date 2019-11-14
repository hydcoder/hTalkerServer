package com.hyd.web.htalker.push.service;

import com.hyd.web.htalker.push.bean.api.base.ResponseModel;
import com.hyd.web.htalker.push.bean.api.message.MessageCreateModel;
import com.hyd.web.htalker.push.bean.card.MessageCard;
import com.hyd.web.htalker.push.bean.db.Message;
import com.hyd.web.htalker.push.bean.db.User;
import com.hyd.web.htalker.push.factory.MessageFactory;
import com.hyd.web.htalker.push.factory.PushFactory;
import com.hyd.web.htalker.push.factory.UserFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Description: 消息发送的入口
 * Created by hydCoder on 2019/11/14 10:05
 */
@Path("/msg")
public class MessageService extends BaseService {

    // 发送一条消息到服务器
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<MessageCard> pushMessage(MessageCreateModel model) {
        if (!MessageCreateModel.check(model)) {
            return ResponseModel.buildParameterError();
        }

        User self = getSelf();

        // 查询该消息是否已经在数据库中存在
        Message message = MessageFactory.findById(model.getId());
        if (message!= null) {
            return ResponseModel.buildOk(new MessageCard(message));
        }

        if (model.getReceiverType() == Message.RECEIVER_TYPE_NONE) {
            return pushToUser(self, model);
        } else {
            return pushToGroup(self, model);
        }
    }

    // 发送群聊消息
    private ResponseModel<MessageCard> pushToGroup(User sender, MessageCreateModel model) {
        // TODO
        return null;
    }

    // 发送单聊消息
    private ResponseModel<MessageCard> pushToUser(User sender, MessageCreateModel model) {
        User receiver = UserFactory.findById(model.getReceiverId());
        if (receiver == null){
            // 没有找到接收者
            return ResponseModel.buildNotFoundUserError("can't find receiver user.");
        }

        if (receiver.getId().equalsIgnoreCase(sender.getId())) {
            // 发送者和接收者是同一个人,返回消息创建失败
            return ResponseModel.buildCreateError(ResponseModel.ERROR_CREATE_MESSAGE);
        }

        // 存储到数据库
        Message message = MessageFactory.add(sender, receiver, model);

        return buildAndPushResponse(sender, message);
    }

    // 推送并构建一个返回信息
    private ResponseModel<MessageCard> buildAndPushResponse(User sender, Message message) {
        if (message == null) {
            return ResponseModel.buildCreateError(ResponseModel.ERROR_CREATE_MESSAGE);
        }

        // 进行推送
        PushFactory.pushNewMessage(sender, message);

        // 返回
        return ResponseModel.buildOk(new MessageCard(message));
    }
}
