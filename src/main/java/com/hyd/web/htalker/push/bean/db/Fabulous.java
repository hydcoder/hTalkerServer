package com.hyd.web.htalker.push.bean.db;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Description: 点赞表，这个表不能省略，与人的关系是一对一，与朋友圈动态的关系是多对一
 * Created by hydCoder on 2019/12/18 15:34
 */
@Entity
@Table(name = "TB_FABLOUS")
public class Fabulous {

    @Id
    @PrimaryKeyJoinColumn
    //主键生成存储的内型为UUID
    @GeneratedValue(generator = "uuid")
    //把uuid的生成器定义为uuid2 uuid2是常规的UUID toString
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    //不允许修改，不允许为null
    @Column(updatable = false, nullable = false)
    private String id;

    //对应朋友圈中的id
    @JoinColumn(name = "fabulousId")
    @ManyToOne(optional = false, fetch = FetchType.EAGER , cascade = CascadeType.ALL)
    private FriendCircle fabulous;

    @Column(nullable = false, insertable = false, updatable = false)
    private String fabulousId;

    //对应用户的id
    @JoinColumn(name = "userId")
    @OneToOne(optional = false, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private User user;

    @Column(nullable = false, insertable = false, updatable = false)
    private String userId;

    //定义为更新时间戳 在创建时就已经写入
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updateAt = LocalDateTime.now();

    //是否点赞
    @Column(nullable = false)
    private boolean isFabulous = false;

    public Fabulous() {
    }

    public Fabulous(User user, FriendCircle fabulous, boolean isFabulous) {
        this.fabulous = fabulous;
        this.user = user;
        this.isFabulous = isFabulous;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public FriendCircle getFabulous() {
        return fabulous;
    }

    public void setFabulous(FriendCircle fabulous) {
        this.fabulous = fabulous;
    }

    public String getFabulousId() {
        return fabulousId;
    }

    public void setFabulousId(String fabulousId) {
        this.fabulousId = fabulousId;
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

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    public boolean isFabulous() {
        return isFabulous;
    }

    public void setFabulous(boolean fabulous) {
        isFabulous = fabulous;
    }
}
