package com.hyd.web.htalker.push.bean.db;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Description: hTalker
 * Created by Administrator on 2019/10/23 16:35
 */
@Entity
@Table(name = "TB_GROUP_MEMBER")
public class GroupMember {

    public static final int PERMISSION_TYPE_NONE = 0;        // 默认权限，普通成员
    public static final int PERMISSION_TYPE_ADMIN = 1;       // 管理员
    public static final int PERMISSION_TYPE_ADMIN_SU = 100;  // 创建者

    public static final int NOTIFY_LEVEL_INVALID = -1;   // 默认不接收消息
    public static final int NOTIFY_LEVEL_NONE = 0;       // 默认通知级别
    public static final int NOTIFY_LEVEL_CLOSE = 1;      // 接受消息不提示

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

    // 别名
    @Column
    private String alias;

    // 消息通知级别
    @Column(nullable = false)
    private int notifyLevel = NOTIFY_LEVEL_NONE;

    // 成员的权限级别
    @Column(nullable = false)
    private int permissionType = PERMISSION_TYPE_NONE;

    // 定义为创建时间戳，在创建时就写入数据库
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createAt = LocalDateTime.now();

    // 定义为更新时间戳，在更新时就写入数据库
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updateAt = LocalDateTime.now();

    // 成员信息对应的用户信息
    @JoinColumn(name = "userId")
    @ManyToOne(optional = false, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private User user;
    @Column(nullable = false, insertable = false, updatable = false)
    private String userId;

    // 成员信息对应的群信息
    @JoinColumn(name = "groupId")
    @ManyToOne(optional = false, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Group group;
    @Column(nullable = false, insertable = false, updatable = false)
    private String groupId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int getNotifyLevel() {
        return notifyLevel;
    }

    public void setNotifyLevel(int notifyLevel) {
        this.notifyLevel = notifyLevel;
    }

    public int getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(int permissionType) {
        this.permissionType = permissionType;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
