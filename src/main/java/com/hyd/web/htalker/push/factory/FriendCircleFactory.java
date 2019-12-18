package com.hyd.web.htalker.push.factory;

import com.hyd.web.htalker.push.bean.db.Comment;
import com.hyd.web.htalker.push.bean.db.Fabulous;
import com.hyd.web.htalker.push.bean.db.FriendCircle;
import com.hyd.web.htalker.push.utils.Hib;

import java.util.List;

/**
 * Description: 朋友圈表查询工具类
 * Created by hydCoder on 2019/12/18 15:31
 */
public class FriendCircleFactory {
    //通过FriendCircle的Id找到这个动态
    public static FriendCircle findById(String friendCircleId) {
        return Hib.query(session -> session.get(FriendCircle.class, friendCircleId));
    }

    /**
     * 获取所有的朋友圈
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<FriendCircle> list() {
        return Hib.query(session -> {
            //查询的条件 按照时间倒序查询，这里应该做分页的
            return (List<FriendCircle>) session.createQuery("from FriendCircle order by createAt desc")
                    .setMaxResults(20)//最多20条数据
                    .list();
        });

    }

    /**
     * 获取朋友圈中的评论消息
     *
     * @param friendCircle
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<Comment> getComments(FriendCircle friendCircle) {
        //重新加载一次用户信息到friendCircle中，和当前的session绑定

        return (List<Comment>) Hib.query(session -> {
            session.load(friendCircle, friendCircle.getId());
            return session.createQuery("from Comment where friendCircleId =:friendCircleId")
                    .setParameter("friendCircleId", friendCircle.getId())
                    .setMaxResults(20).list();
        });
    }

    /***
     * 获取用户有没有点赞
     * @return
     */
    public static boolean getFabulousState(String userId, String friendCircleId) {
        //按照用户的id和朋友圈的id查询
        Fabulous fabulous = getFabulous(userId, friendCircleId);
        //如果有记录，查看是已点赞还是未点赞，没有记录就是未点赞
        if (fabulous == null)
            return false;
        return fabulous.isFabulous();
    }

    /**
     * 获取有没有点赞表
     *
     * @param userId         用户Id
     * @param friendCircleId 动态id
     * @return Fabulous
     */
    public static Fabulous getFabulous(String userId, String friendCircleId) {
        return (Fabulous) Hib.query(session -> session.createQuery
                ("from Fabulous where userId=:userId and fabulousId=:friendCircleId")
                .setParameter("userId", userId)
                .setParameter("friendCircleId", friendCircleId)
                .uniqueResult()
        );
    }
}
