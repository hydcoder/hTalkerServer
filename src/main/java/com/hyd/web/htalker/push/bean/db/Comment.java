package com.hyd.web.htalker.push.bean.db;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Description: 评论的数据表
 * Created by hydCoder on 2019/12/18 15:35
 */
@Entity
@Table(name = "TB_COMMENT")
public class Comment {

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
    @JoinColumn(name = "friendCircleId")
    @ManyToOne(optional = false, fetch = FetchType.EAGER , cascade = CascadeType.ALL)
    private FriendCircle friendCircle;

    @Column(nullable = false, insertable = false, updatable = false)
    private String friendCircleId;

    //定义为创建时间戳 在创建时就已经写入
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createAt = LocalDateTime.now();

    @Column
    private String content;//评论的内容

    //对应用户的id
    @JoinColumn(name = "commentId")
    @ManyToOne(optional = false, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private User comment;

    @Column(nullable = false, insertable = false, updatable = false)
    private String commentId;

    public Comment() {
    }

    public Comment(FriendCircle friendCircle, User comment, String content) {
        this.comment= comment;
        this.friendCircle = friendCircle;
        this.content = content;
    }

    public User getComment() {
        return comment;
    }

    public void setComment(User comment) {
        this.comment = comment;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public FriendCircle getFriendCircle() {
        return friendCircle;
    }

    public void setFriendCircle(FriendCircle friendCircle) {
        this.friendCircle = friendCircle;
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


}
