package com.hosiky;

import com.github.jeffreyning.mybatisplus.conf.EnableMPP;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.hosiky.mapper")
@EnableMPP
public class carRentalSystemApplication {
    public static void main(String[] args) {

        SpringApplication.run(carRentalSystemApplication.class, args);
    }
}
