package com.flux.starter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @ClassName FluxAuthApplication
 * @Author njq
 * @Date 2026/10/28 22:10
 * @Description 认证中心启动类
 */
@SpringBootApplication(scanBasePackages = "com.flux")
@MapperScan("com.flux.**.mapper")
public class FluxAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(FluxAuthApplication.class, args);
    }
}
