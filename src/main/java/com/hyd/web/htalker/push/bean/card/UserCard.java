package com.hyd.web.htalker.push.bean.card;

import com.google.gson.annotations.Expose;

import java.time.LocalDateTime;

/**
 * Description: 返回给客户端的用户信息
 * Created by Administrator on 2019/10/25 15:02
 */
public class UserCard {

    @Expose
    private String id;

    @Expose
    private String desc;

    @Expose
    private String name;

    @Expose
    private String phone;

    @Expose
    private String portrait;

    @Expose
    private int sex = 0;

    // 用户粉丝的数量
    @Expose
    private int following;

    // 用户关注别的用户的数量
    @Expose
    private int follows;

    // 我与当前User的关注状态，是否已经关注了这个人
    @Expose
    private boolean isFollow;

    // 用户信息的最后更新时间
    @Expose
    private LocalDateTime modifyAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    public int getFollows() {
        return follows;
    }

    public void setFollows(int follows) {
        this.follows = follows;
    }

    public boolean isFollow() {
        return isFollow;
    }

    public void setFollow(boolean follow) {
        isFollow = follow;
    }

    public LocalDateTime getModifyAt() {
        return modifyAt;
    }

    public void setModifyAt(LocalDateTime modifyAt) {
        this.modifyAt = modifyAt;
    }
}
