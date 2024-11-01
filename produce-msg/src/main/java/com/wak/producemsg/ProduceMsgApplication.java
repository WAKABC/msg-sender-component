package com.wak.producemsg;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author wuankang
 * @Date 2024/11/01 17:03:36
 * @Description TODO 生产消息应用
 * @Version 1.0.0
 */
@SpringBootApplication
@MapperScan(basePackages = {"com.wak.producemsg.mapper"})
public class ProduceMsgApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProduceMsgApplication.class, args);
    }

}
