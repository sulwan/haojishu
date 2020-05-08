package com.haojishu.controller.demo;

import com.haojishu.service.api.IApiDemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DemoIndexController {

  @Autowired private IApiDemoService apiDemoService;

  @RequestMapping("/test")
  public String test() {
    return apiDemoService.test();
  }
}
