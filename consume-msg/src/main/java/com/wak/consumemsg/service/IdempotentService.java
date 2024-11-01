package com.wak.consumemsg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wak.consumemsg.entities.Idempotent;

/**
 * @author wuankang
 * @Date 2024/10/31 17:06:26
 * @Description TODO 幂等服务
 * @Version 1.0.0
 */
public interface IdempotentService extends IService<Idempotent> {

    /**
     * 幂等接口
     *
     * @param idempotentKey 幂等key
     * @param runnable      幂等逻辑
     * @return int
     */
    int idempotent(String idempotentKey, Runnable runnable);

    /**
     * 幂等
     *
     * @param busId    业务ID
     * @param busType  业务类型
     * @param runnable 幂等逻辑
     * @return int
     */
    default int idempotent(String busId, String busType, Runnable runnable) {
        String idempotentKey = String.format("%s:%s", busId, busType);
        return this.idempotent(idempotentKey, runnable);
    }
}
