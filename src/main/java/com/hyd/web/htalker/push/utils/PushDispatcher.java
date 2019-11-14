package com.hyd.web.htalker.push.utils;

import com.gexin.rp.sdk.base.IBatch;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.google.common.base.Strings;
import com.hyd.web.htalker.push.bean.api.base.PushModel;
import com.hyd.web.htalker.push.bean.db.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Description: 消息推送工具类
 * Created by hydCoder on 2019/11/13 10:50
 */
public class PushDispatcher {

    // 详见【概述】-【服务端接入步骤】-【STEP1】说明，获得的应用配置
    private final static String appId = "D84oRwbUcd5PcBQf4VXMS4";
    private final static String appKey = "V7vbjRyr5F9DqZZOuN3Sq2";
    private final static String masterSecret = "KigRhwgg2k9a9IF6CnETY5";
    private final static String host = "http://sdk.open.api.igexin.com/apiex.htm";

    private final IGtPush iGtPush;
    // 要收到消息的人和内容的列表
    private final List<BatchBean> beans = new ArrayList<>();

    public PushDispatcher() {
        // 最根本的发送者
        iGtPush = new IGtPush(host, appKey, masterSecret);
    }

    /**
     * 添加一条消息
     *
     * @param receiver 接收者
     * @param model    接收的推送model
     * @return 是否添加成功
     */
    public boolean add(User receiver, PushModel model) {
        // 基础检查，必须要有接受者的设备Id
        if (receiver == null || model == null || Strings.isNullOrEmpty(receiver.getPushId())) {
            return false;
        }

        String pushString = model.getPushString();
        if (Strings.isNullOrEmpty(pushString)) {
            return false;
        }

        // 构建一个目标+消息内容
        BatchBean bean = buildMessage(receiver.getPushId(), pushString);
        beans.add(bean);
        return true;
    }

    /**
     * 对要发送的数据进行格式化封装
     *
     * @param pushId     接收者的设备Id
     * @param pushString 要接收的数据
     * @return BatchBean
     */
    private BatchBean buildMessage(String pushId, String pushString) {
        // 构建透传消息，不是通知栏显示，而是在MessageReceiver中收到
        TransmissionTemplate template = new TransmissionTemplate();
        template.setAppId(appId);
        template.setAppkey(appKey);
        template.setTransmissionContent(pushString);
        template.setTransmissionType(2); // 透传消息接受方式设置，1：立即启动APP，2：客户端收到消息后需要自行处理

        SingleMessage message = new SingleMessage();
        message.setData(template); // 把透传消息设置到单消息模板中
        message.setOffline(true);  // 是否运行离线发送
        message.setOfflineExpireTime(24 * 3600 * 1000); // 离线消息时长

        // 设置推送目标，填入appId和clientId
        Target target = new Target();
        target.setAppId(appId);
        target.setClientId(pushId);

        return new BatchBean(message, target);
    }

    // 进行消息最终发送
    public boolean submit() {
        // 构建打包的工具类
        IBatch batch = iGtPush.getBatch();

        // 标识是否有数据要发送
        boolean haveData = false;

        for (BatchBean bean : beans) {
            try {
                batch.add(bean.message, bean.target);
                haveData = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 没有数据就直接返回
        if (!haveData) {
            return false;
        }

        IPushResult result = null;
        try {
            result = batch.submit();
        } catch (Exception e) {
            e.printStackTrace();

            // 失败情况下尝试重试一次
            try {
                batch.retry();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        if (result != null) {
            try {
                Logger.getLogger("PushDispatcher").log(Level.INFO, result.getResponse().get("result").toString());
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Logger.getLogger("PushDispatcher").log(Level.WARNING, "推送服务器响应异常！！！");
        return true;
    }

    // 给每个人发送消息的一个bean封装
    private static class BatchBean {
        SingleMessage message;
        Target target;

        public BatchBean(SingleMessage message, Target target) {
            this.message = message;
            this.target = target;
        }
    }
}
