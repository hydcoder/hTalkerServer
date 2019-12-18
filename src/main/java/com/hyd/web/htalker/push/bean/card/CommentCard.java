package com.hyd.web.htalker.push.bean.card;

import com.google.gson.annotations.Expose;
import com.hyd.web.htalker.push.bean.db.Comment;
import com.hyd.web.htalker.push.bean.db.User;

import java.time.LocalDateTime;

/**
 * Description: 评论内容的卡片
 * Created by hydCoder on 2019/12/18 15:40
 */
public class CommentCard {

    @Expose
    private String id;
    @Expose
    private String friendCircleId;
    @Expose
    private LocalDateTime createAt;
    @Expose
    private String content;//评论的内容
    @Expose
    private User comment;//评论者

    public CommentCard(Comment comment) {
        this.id= comment.getId();
        this.friendCircleId = comment.getFriendCircleId();
        this.createAt = comment.getCreateAt();
        this.content = comment.getContent();
        this.comment = comment.getComment();

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFriendCircleId() {
        return friendCircleId;
    }

    public void setFriendCircleId(String friendCircleId) {
        this.friendCircleId = friendCircleId;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getComment() {
        return comment;
    }

    public void setComment(User comment) {
        this.comment = comment;
    }
}
