package com.haojishu.controller.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DemoIndexController {

  private String prefix = "web";

  @RequestMapping("/")
  public String test(ModelMap map) {
    map.put("title", "演示标题");
    return prefix + "/test";
  }
}
