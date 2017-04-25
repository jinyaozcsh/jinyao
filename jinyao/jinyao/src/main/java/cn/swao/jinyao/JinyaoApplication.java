package cn.swao.jinyao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan({ "cn.swao.*", "cn.langya.*", "com.eastnb.*", "com.mall.*", "com.work", "cn.swao.jinyao" })
@EntityScan({ "cn.swao.*", "cn.langya.*", "com.eastnb.*", "com.mall.*", "com.work", "cn.swao.jinyao" })
@EnableJpaRepositories({ "cn.swao.*", "cn.langya.*", "com.eastnb.*", "com.mall.*", "com.work", "cn.swao.jinyao" })
public class JinyaoApplication {

    public static void main(String[] args) {
        SpringApplication.run(JinyaoApplication.class, args);
    }
}
