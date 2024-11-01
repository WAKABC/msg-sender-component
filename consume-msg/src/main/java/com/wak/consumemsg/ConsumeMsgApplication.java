package com.wak.consumemsg;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.wak.consumemsg.mapper")
public class ConsumeMsgApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConsumeMsgApplication.class, args);
    }

}
