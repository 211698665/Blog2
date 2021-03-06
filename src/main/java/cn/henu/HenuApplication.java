package cn.henu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan("cn.henu.dao") //配置mybatis扫描mapper
@EnableCaching
@EnableTransactionManagement
public class HenuApplication {
    public static void main(String[] args) {
        SpringApplication.run(HenuApplication.class, args);
    }
}
