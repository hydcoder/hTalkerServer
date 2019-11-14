package com.hyd.web.htalker.push.bean.api.message;

import com.google.common.base.Strings;
import com.google.gson.annotations.Expose;
import com.hyd.web.htalker.push.bean.db.Message;

/**
 * Description: Api请求的model格式
 * Created by Administrator on 2019/11/14 10:06
 */
public class MessageCreateModel {

    // id从客户端生成，一个UUID
    @Expose
    private String id;

    @Expose
    private String content;

    @Expose
    private String attach;

    // 消息类型
    @Expose
    private int type = Message.TYPE_STR;

    // 接收者，允许为空
    @Expose
    private String receiverId;

    // 接收者类型，群或人
    @Expose
    private int receiverType = Message.RECEIVER_TYPE_NONE;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public int getReceiverType() {
        return receiverType;
    }

    public void setReceiverType(int receiverType) {
        this.receiverType = receiverType;
    }

    public static boolean check(MessageCreateModel model) {
        // Model 不允许为null，
        return model != null
                && !(Strings.isNullOrEmpty(model.id)
                || Strings.isNullOrEmpty(model.content)
                || Strings.isNullOrEmpty(model.receiverId))

                && (model.receiverType == Message.RECEIVER_TYPE_NONE
                || model.receiverType == Message.RECEIVER_TYPE_GROUP)

                && (model.type == Message.TYPE_STR
                || model.type == Message.TYPE_PIC
                || model.type == Message.TYPE_AUDIO
                || model.type == Message.TYPE_FILE);
    }
}
