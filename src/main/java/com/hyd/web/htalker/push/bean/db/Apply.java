package com.hyd.web.htalker.push.bean.db;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Description: 申请记录表
 * Created by Administrator on 2019/10/24 17:35
 */
@Entity
@Table(name = "TB_APPLY")
public class Apply {

    public static final int TYPE_ADD_USER = 1; // 添加好友
    public static final int TYPE_ADD_GROUP = 2; // 加入群

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

    // 描述部分，对我们的申请信息做描述
    // eg: 加个好友呗
    @Column(nullable = false)
    private String description;

    // 附件，可为空
    // 可以附带图片地址，或者其他
    @Column(columnDefinition = "TEXT")
    private String attach;

    // 当前申请的类型
    @Column(nullable = false)
    private int type;

    // 目标id，不进行强关联，不建立主外键关系
    // type-> TYPE_ADD_USER, targetId就是User.id
    // type-> TYPE_ADD_GROUP, targetId就是Group.id
    @Column(nullable = false)
    private String targetId;

    // 定义为创建时间戳，在创建时就写入数据库
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createAt = LocalDateTime.now();

    // 定义为更新时间戳，在更新时就写入数据库
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updateAt = LocalDateTime.now();

    // 申请人, 可为空，为系统消息
    // 一个人可以有很多个申请
    @ManyToOne()
    @JoinColumn(name = "applicationId")
    private User applicant;
    @Column(updatable = false, insertable = false)
    private String applicationId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
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

    public User getApplicant() {
        return applicant;
    }

    public void setApplicant(User applicant) {
        this.applicant = applicant;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }
}
