package com.hyd.web.htalker.push.bean.api.friend;

import com.google.common.base.Strings;
import com.google.gson.annotations.Expose;

/**
 * Description: 发布朋友圈的Model
 * Created by hydCoder on 2019/12/18 15:37
 */
public class ReleaseFriendCircleModel {

    @Expose
    private String content;//内容
    @Expose
    private String releaseId;//发布人Id
    @Expose
    private String imgs;//朋友圈图片


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public String getImgs() {
        return imgs;
    }

    public void setImgs(String imgs) {
        this.imgs = imgs;
    }

    public String getReleaseId() {
        return releaseId;
    }

    public void setReleaseId(String releaseId) {
        this.releaseId = releaseId;
    }

    /**
     * 检测
     *
     * @param model
     * @return
     */
    public static boolean check(ReleaseFriendCircleModel model) {
        return model != null &&
                !(Strings.isNullOrEmpty(model.content)
                        || Strings.isNullOrEmpty(model.releaseId)
                        || Strings.isNullOrEmpty(model.imgs));
    }
}
