package com.haojishu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.ImportResource;

/**
 * 启动程序
 *
 * @author sulwan@126.com
 */
@ImportResource("classpath:haojishu-backend.xml")
@SpringBootApplication(
    exclude = {
      DataSourceAutoConfiguration.class,
      DataSourceTransactionManagerAutoConfiguration.class,
      HibernateJpaAutoConfiguration.class
    })
public class HaoBackendApplication {
  public static void main(String[] args) {
    // System.setProperty("spring.devtools.restart.enabled", "false");
    SpringApplication.run(HaoBackendApplication.class, args);
    System.out.println("(♥◠‿◠)ﾉﾞ  好技术后台启动成功");
  }
}
