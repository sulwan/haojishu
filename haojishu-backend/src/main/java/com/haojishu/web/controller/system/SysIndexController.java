package com.haojishu.web.controller.system;

import com.haojishu.config.Global;
import com.haojishu.core.controller.BaseController;
import com.haojishu.domain.SysMenu;
import com.haojishu.domain.SysUser;
import com.haojishu.service.system.ISysMenuService;
import com.haojishu.utils.ShiroUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * 首页 业务处理
 *
 * @author sulwan@126.com
 */
@Controller
public class SysIndexController extends BaseController {
  @Autowired private ISysMenuService menuService;

  // 系统首页
  @GetMapping("/index")
  public String index(ModelMap mmap) {
    // 取身份信息
    SysUser user = ShiroUtils.getSysUser();
    // 根据用户id取出菜单
    List<SysMenu> menus = menuService.selectMenusByUser(user);
    mmap.put("menus", menus);
    mmap.put("user", user);
    mmap.put("copyrightYear", Global.getCopyrightYear());
    mmap.put("demoEnabled", Global.isDemoEnabled());
    return "index";
  }

  // 系统介绍
  @GetMapping("/system/main")
  public String main(ModelMap mmap) {
    mmap.put("version", Global.getVersion());
    return "main";
  }
}
