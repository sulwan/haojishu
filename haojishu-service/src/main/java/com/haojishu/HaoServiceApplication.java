package com.haojishu;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ImportResource;

@ImportResource("classpath:haojishu-service.xml")
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class HaoServiceApplication {

  private static final Log log = LogFactory.getLog(HaoServiceApplication.class);

  public static void main(String[] args) throws InterruptedException {
    new SpringApplicationBuilder(HaoServiceApplication.class)
        .web(WebApplicationType.NONE)
        .run(args);
    System.out.println("(♥◠‿◠)ﾉﾞ  服务端启动");
    synchronized (HaoServiceApplication.class) {
      while (true) {
        try {
          HaoServiceApplication.class.wait();
        } catch (InterruptedException e) {
          log.error("== synchronized error:", e);
        }
      }
    }
  }
}
