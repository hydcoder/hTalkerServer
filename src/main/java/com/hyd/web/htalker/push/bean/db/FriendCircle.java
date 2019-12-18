package com.hyd.web.htalker.push.bean.db;

import com.hyd.web.htalker.push.bean.api.friend.ReleaseFriendCircleModel;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Description: 朋友圈的数据表
 * Created by hydCoder on 2019/12/18 15:33
 */
@Entity
@Table(name = "TB_FRIEND_CIRCLE")
public class FriendCircle {
    @Id
    @PrimaryKeyJoinColumn
    //主键生成存储的内型为UUID
    @GeneratedValue(generator = "uuid")
    //把uuid的生成器定义为uuid2 uuid2是常规的UUID toString
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    //不允许修改，不允许为null
    @Column(updatable = false, nullable = false)
    private String id;

    //标题
    @Column(nullable = false)
    private String title;

    //内容
    @Column(nullable = false)
    private String content;

    //头像
    @Column(nullable = false)
    private String head;


    //内容不允许为空 类型为Text
    @Column(nullable = false, columnDefinition = "TEXT")
    private String imgs;

    //发布人
    @JoinColumn(name = "releaseId")
    @ManyToOne(optional = false, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private User release;

    //发布人Id
    @Column(nullable = false, updatable = false, insertable = false)
    private String releaseId;

    //定义为创建时间戳 在发布时就已经写入
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createAt = LocalDateTime.now();


    //一对多 一个朋友圈有多条评论记录
    //对应的数据库表的comment.friendCircleId
    @JoinColumn(name ="friendCircleId")
    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private Set<Comment> comments = new HashSet<>();

    //一对多 一个朋友圈有多条点赞记录
    //对应的数据库表的Fabulous.fabulousId
    @JoinColumn(name ="fabulousId")
    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private Set<Fabulous> fabulous = new HashSet<>();


    public FriendCircle() {
    }

    public FriendCircle(User release, ReleaseFriendCircleModel model) {
        this.content =model.getContent();
        this.head = release.getPortrait();
        this.title = release.getName();
        this.imgs = model.getImgs();
        this.release = release;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public User getRelease() {
        return release;
    }

    public void setRelease(User release) {
        this.release = release;
    }

    public String getReleaseId() {
        return releaseId;
    }

    public void setReleaseId(String releaseId) {
        this.releaseId = releaseId;
    }

    public String getImgs() {
        return imgs;
    }

    public void setImgs(String imgs) {
        this.imgs = imgs;
    }

    public Set<Fabulous> getFabulous() {
        return fabulous;
    }

    public void setFabulous(Set<Fabulous> fabulous) {
        this.fabulous = fabulous;
    }
}
