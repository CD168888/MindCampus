package com.mc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 启动程序
 *
 * @author caidu
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class RuoYiApplication {
    public static void main(String[] args) {
        // System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(RuoYiApplication.class, args);
        System.out.println(
                " (♥◠‿◠)ﾉﾞ  MC项目启动成功   ლ(´ڡ`ლ)ﾞ \n" +
                        "   _____________   _____________   \n" +
                        "  |   M   M     | |   C   C     |  \n" +
                        "  |   MM MM     | |  C     C    |  \n" +
                        "  |   M M M     | |  C          |  \n" +
                        "  |   M   M     | |  C          |  \n" +
                        "  |   M   M     | |  C     C    |  \n" +
                        "  |___M___M_____| |___C___C_____|  \n"
        );
    }
}
