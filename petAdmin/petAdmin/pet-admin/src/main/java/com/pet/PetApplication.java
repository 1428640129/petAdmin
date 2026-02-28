package com.pet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 启动程序
 * 
 * @author Pet
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class PetApplication
{
    public static void main(String[] args) {
        // System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(PetApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  Pet启动成功   ლ(´ڡ`ლ)ﾞ");
    }

}
