package com.hyd.web.htalker.push.bean.card;

import com.google.gson.annotations.Expose;
import com.hyd.web.htalker.push.bean.db.Fabulous;
import com.hyd.web.htalker.push.bean.db.FriendCircle;
import com.hyd.web.htalker.push.utils.Hib;

import java.time.LocalDateTime;

/**
 * Description: 朋友圈的卡片
 * Created by hydCoder on 2019/12/18 15:43
 */
public class FriendCircleCard {

    @Expose
    private String id;
    @Expose
    private String title;
    @Expose
    private String content;
    @Expose
    private String head;
    @Expose
    private String imgs;
    @Expose
    private String releaseId;

    @Expose
    private LocalDateTime createAt;// 发布时间

    @Expose
    private int commentSize;//评论数量

    //是否点赞
    @Expose
    private boolean isFabulous = false;

    //点赞数量
    @Expose
    private int fabulousSize = 0;

    public FriendCircleCard(FriendCircle friendCircle, boolean isFabulous) {
        this.id = friendCircle.getId();
        this.content = friendCircle.getContent();
        this.head = friendCircle.getHead();
        this.imgs = friendCircle.getImgs();
        this.title = friendCircle.getTitle();
        this.releaseId = friendCircle.getReleaseId();
        this.createAt = friendCircle.getCreateAt();
        this.isFabulous = isFabulous;
        //this.commentSize = friendCircle.getComments().size();//懒加载会报错
        Hib.queryOnly(session -> {
            session.load(friendCircle, friendCircle.getId());
            //这个时候仅仅只是进行了数量查询，并没有查询整个集合
            //要查询整个集合，必须在session存在情况下进行遍历
            //或者使用 Hibernate.initialize(user.getFollowers());
            commentSize = friendCircle.getComments().size();
            // 这边是获取所有表的数量，还是有问题的。要过滤一下的
            fabulousSize=0;
            for (Fabulous fabulous : friendCircle.getFabulous()) {
                if(fabulous.isFabulous()==true){
                    fabulousSize++;
                }
            }
        });
    }
    public FriendCircleCard(FriendCircle friendCircle) {
        this(friendCircle,false);
    }
    public String getImgs() {
        return imgs;
    }

    public void setImgs(String imgs) {
        this.imgs = imgs;
    }

    public int getCommentSize() {
        return commentSize;
    }

    public void setCommentSize(int commentSize) {
        this.commentSize = commentSize;
    }

    public boolean isFabulous() {
        return isFabulous;
    }

    public void setFabulous(boolean fabulous) {
        isFabulous = fabulous;
    }

    public int getFabulousSize() {
        return fabulousSize;
    }

    public void setFabulousSize(int fabulousSize) {
        this.fabulousSize = fabulousSize;
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

    public String getReleaseId() {
        return releaseId;
    }

    public void setReleaseId(String releaseId) {
        this.releaseId = releaseId;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }
}
