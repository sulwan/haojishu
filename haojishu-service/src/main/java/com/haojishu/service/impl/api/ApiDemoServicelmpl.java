package com.haojishu.service.impl.api;

import com.haojishu.service.api.IApiDemoService;
import org.springframework.stereotype.Service;

@Service("IApiDemoService")
public class ApiDemoServicelmpl implements IApiDemoService {

  @Override
  public String test() {
    return "这是测试Api模块方法";
  }
}
