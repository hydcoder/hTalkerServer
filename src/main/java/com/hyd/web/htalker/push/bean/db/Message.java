package com.hyd.web.htalker.push.bean.db;

import com.hyd.web.htalker.push.bean.api.message.MessageCreateModel;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Description: hTalker
 * Created by Administrator on 2019/10/22 17:09
 */
@Entity
@Table(name = "TB_MESSAGE")
public class Message {
    public static final int RECEIVER_TYPE_NONE = 1;   // 发送给人
    public static final int RECEIVER_TYPE_GROUP = 2;   // 发送给群

    public static final int TYPE_STR = 1;   // 字符类型
    public static final int TYPE_PIC = 2;   // 图片类型
    public static final int TYPE_FILE = 3;   // 文件类型
    public static final int TYPE_AUDIO = 4;   // 语音类型

    // 主键
    @Id
    @PrimaryKeyJoinColumn
    // message表不自动生成UUID，id由代码写入，由客户端生成，避免服务器和客户端的复杂的映射关系
    // 不允许为null， 不允许修改
    @Column(nullable = false, updatable = false)
    private String id;

    // 消息内容，默认为text
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // 附件
    @Column
    private String attach;

    // 消息类型
    @Column(nullable = false)
    private int type;

    // 定义为创建时间戳，在创建时就写入数据库
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createAt = LocalDateTime.now();

    // 定义为更新时间戳，在更新时就写入数据库
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updateAt = LocalDateTime.now();

    // 发送者，不能为空
    // 多个消息对应一个发送者
    @JoinColumn(name = "senderId")
    @ManyToOne(optional = false)
    private User sender;
    // 这个字段是为了对应sender的数据库字段senderId
    // 因此不允许手动的更新或者插入
    @Column(nullable = false, updatable = false, insertable = false)
    private String senderId;

    // 接收者，允许为空
    // 多个消息对应一个接收者
    @JoinColumn(name = "receiverId")
    @ManyToOne()
    private User receiver;
    // 这个字段是为了对应receiver的数据库字段receiverId
    // 因此不允许手动的更新或者插入
    @Column(updatable = false, insertable = false)
    private String receiverId;

    // 一个群可以接收多个消息
    @JoinColumn(name = "groupId")
    @ManyToOne()
    private Group group;
    // 这个字段是为了对应receiver的数据库字段receiverId
    // 因此不允许手动的更新或者插入
    @Column(updatable = false, insertable = false)
    private String groupId;

    public Message() {
    }

    // 单聊的构造函数
    public Message(User sender, User receiver, MessageCreateModel model) {
        this.id = model.getId();
        this.attach = model.getAttach();
        this.content = model.getContent();
        this.type = model.getType();

        this.sender = sender;
        this.receiver = receiver;
    }

    // 群聊的构造函数
    public Message(User sender, Group group, MessageCreateModel model) {
        this.id = model.getId();
        this.attach = model.getAttach();
        this.content = model.getContent();
        this.type = model.getType();

        this.sender = sender;
        this.group = group;
    }

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

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
