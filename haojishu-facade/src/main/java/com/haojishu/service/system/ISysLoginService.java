package com.haojishu.service.system;

import com.haojishu.domain.SysUser;
import org.springframework.stereotype.Component;

@Component
public interface ISysLoginService {
  /** 登录 */
  public SysUser login(String username, String password);

  /** 记录登录信息 */
  public void recordLoginInfo(SysUser user);
}
