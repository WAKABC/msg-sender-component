package com.wak.consumemsg.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wak.consumemsg.entities.Idempotent;
import com.wak.consumemsg.mapper.IdempotentMapper;
import com.wak.consumemsg.service.IdempotentService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Objects;

/**
 * @author wuankang
 * @Date 2024/10/31 17:06:35
 * @Description 幂等实现类
 * @Version 1.0
 */
@Service
public class IdempotentServiceImpl extends ServiceImpl<IdempotentMapper, Idempotent> implements IdempotentService {
    @Resource
    private IdempotentMapper idempotentMapper;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Override
    public int idempotent(String idempotentKey, Runnable runnable) {
        //根据 idempotentKey 查找记录，如果能找到!= null，说明业务已成功处理过
        LambdaQueryWrapper<Idempotent> eq = Wrappers.lambdaQuery(Idempotent.class).eq(Idempotent::getIdempotentKey, idempotentKey);
        Idempotent idempotent = this.idempotentMapper.selectOne(eq);
        if (Objects.nonNull(idempotent)) {
            return -1;
        }
        //执行业务，可能存在事务，需要在事务中执行
        transactionTemplate.executeWithoutResult(action -> {
            //执行业务
            runnable.run();
            //业务执行完毕后插入幂等记录
            Idempotent idempotent1 = new Idempotent();
            idempotent1.setIdempotentKey(idempotentKey);
            idempotent1.setId(IdUtil.fastSimpleUUID());
            this.idempotentMapper.insert(idempotent1);
        });
        return 1;
    }
}
