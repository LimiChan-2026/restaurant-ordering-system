package com.kmbeast;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 餐厅订餐系统启动类
 */
@SpringBootApplication
@MapperScan("com.kmbeast.mapper")
public class RestaurantApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestaurantApplication.class, args);
        System.out.println("============================================");
        System.out.println("   餐厅订餐系统启动成功！端口：8081");
        System.out.println("============================================");
    }
}
