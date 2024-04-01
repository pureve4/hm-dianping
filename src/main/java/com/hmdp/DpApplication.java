package com.hmdp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Administrator
 * 黑马点评
 */
@MapperScan("com.hmdp.mapper")
@SpringBootApplication
public class DpApplication {
    public static void main(String[] args) {
        SpringApplication.run(DpApplication.class, args);
    }
}
