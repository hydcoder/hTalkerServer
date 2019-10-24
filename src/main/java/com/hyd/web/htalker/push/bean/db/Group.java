package com.hyd.web.htalker.push.bean.db;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Description: hTalker
 * Created by Administrator on 2019/10/23 11:44
 */
@Entity
@Table(name = "TB_GROUP")
public class Group {

    // 主键
    @Id
    @PrimaryKeyJoinColumn
    // 主键生成存储的类型为UUID, 自动生成
    @GeneratedValue(generator = "uuid")
    // 把生成器的生成规则定义为uuid2,uuid2是常规的toString
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    // 不允许为null， 不允许修改
    @Column(nullable = false, updatable = false)
    private String id;

    // 群描述
    @Column(nullable = false)
    private String description;

    // 群名称
    @Column(nullable = false)
    private String name;

    // 群头像
    @Column(nullable = false)
    private String picture;

    // 定义为创建时间戳，在创建时就写入数据库
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createAt = LocalDateTime.now();

    // 定义为更新时间戳，在更新时就写入数据库
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updateAt = LocalDateTime.now();

    // 群的创建者
    // optional: 可选为false, 一个群必须要有一个创建者
    // fetch: 加载方式FetchType.EAGER，急加载
    // 意味着加载群的信息的时候就必须加载owner的信息
    // cascade: 联级级别为ALL，所有的更新（更新，删除等）都将进行关系更新
    @ManyToOne(optional = false, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "ownerId")
    private User owner;
    @Column(nullable = false, updatable = false, insertable = false)
    private String ownerId;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
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
}
