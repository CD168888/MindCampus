package com.mc.framework.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import com.mc.common.utils.SecurityUtils;

import java.util.Date;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {

        this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
        this.strictInsertFill(metaObject, "updateTime", Date.class, new Date());

        // 若依：自动填充创建人与更新人
        try {
            this.strictInsertFill(metaObject, "createBy", String.class, SecurityUtils.getUsername());
            this.strictInsertFill(metaObject, "updateBy", String.class, SecurityUtils.getUsername());
        } catch (Exception e) {
            // 未登录情况下（任务调度/系统插入）
            this.strictInsertFill(metaObject, "createBy", String.class, "system");
            this.strictInsertFill(metaObject, "updateBy", String.class, "system");
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date());

        try {
            this.strictUpdateFill(metaObject, "updateBy", String.class, SecurityUtils.getUsername());
        } catch (Exception e) {
            this.strictUpdateFill(metaObject, "updateBy", String.class, "system");
        }
    }
}
