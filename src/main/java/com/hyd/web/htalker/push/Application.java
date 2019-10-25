package com.hyd.web.htalker.push;

import com.hyd.web.htalker.push.provider.GsonProvider;
import com.hyd.web.htalker.push.service.AccountService;
import org.glassfish.jersey.server.ResourceConfig;

import java.util.logging.Logger;

/**
 * Description: hTalker
 * Created by Administrator on 2019/10/15 18:00
 */
public class Application extends ResourceConfig {

    public Application() {
        // 注册逻辑处理的包名
        packages(AccountService.class.getPackage().getName());

        // 注册json转换器
//        register(JacksonJsonProvider.class);
        // 替换为Gson解析器
        register(GsonProvider.class);

        // 注册日志打印输出
        register(Logger.class);
    }
}
