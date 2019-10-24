package com.hyd.web.htalker.push.bean.db;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 消息接收历史记录表
 * Description: hTalker
 * Created by Administrator on 2019/10/23 16:58
 */
@Entity
@Table(name = "TB_PUSH_HISTORY")
public class PushHistory {

    // 主键
    @Id
    @PrimaryKeyJoinColumn
    // 主键生成存储的类型为UUID
    @GeneratedValue(generator = "uuid")
    // 把生成器的生成规则定义为uuid2,uuid2是常规的toString
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    // 不允许为null， 不允许修改
    @Column(nullable = false, updatable = false)
    private String id;

    // 推送的具体实体存储的都是json字符串
    // BLOB是比TEXT更多的大字段类型
    @Lob
    @Column(nullable = false, columnDefinition = "BLOB")
    private String entity;

    // 推送的实体类型
    @Column(nullable = false)
    private int entityType;

    // 定义为创建时间戳，在创建时就写入数据库
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createAt = LocalDateTime.now();

    // 定义为更新时间戳，在更新时就写入数据库
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updateAt = LocalDateTime.now();

    // 推送送达的时间， 可为null
    @Column()
    private LocalDateTime arrivalAt;

    // 发送者，可为空, 可能是系统消息
    // 一个发送者可以发送很多推送消息
    @JoinColumn(name = "senderId")
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private User sender;
    // 这个字段是为了对应sender的数据库字段senderId
    // 因此不允许手动的更新或者插入
    @Column(updatable = false, insertable = false)
    private String senderId;

    // 接收者，不允许为空
    // 一个接收者可以接收很多推送消息
    @JoinColumn(name = "receiverId")
    // FetchType.EAGER: 加载一条推送消息的时候直接加载用户信息
    @ManyToOne(optional = false, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private User receiver;
    // 这个字段是为了对应receiver的数据库字段receiverId
    // 因此不允许手动的更新或者插入
    @Column(updatable = false, insertable = false)
    private String receiverId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public int getEntityType() {
        return entityType;
    }

    public void setEntityType(int entityType) {
        this.entityType = entityType;
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

    public LocalDateTime getArrivalAt() {
        return arrivalAt;
    }

    public void setArrivalAt(LocalDateTime arrivalAt) {
        this.arrivalAt = arrivalAt;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }
}
