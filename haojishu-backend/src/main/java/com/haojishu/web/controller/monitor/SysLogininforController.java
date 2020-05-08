package com.haojishu.web.controller.monitor;

import com.haojishu.annotation.Log;
import com.haojishu.core.controller.BaseController;
import com.haojishu.core.domain.AjaxResult;
import com.haojishu.core.page.TableDataInfo;
import com.haojishu.domain.SysLogininfor;
import com.haojishu.enums.BusinessType;
import com.haojishu.service.system.ISysLogininforService;
import com.haojishu.utils.poi.ExcelUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 系统访问记录
 *
 * @author sulwan@126.com
 */
@Controller
@RequestMapping("/monitor/logininfor")
public class SysLogininforController extends BaseController {
  private String prefix = "monitor/logininfor";

  @Autowired private ISysLogininforService logininforService;

  @RequiresPermissions("monitor:logininfor:view")
  @GetMapping()
  public String logininfor() {
    return prefix + "/logininfor";
  }

  @RequiresPermissions("monitor:logininfor:list")
  @PostMapping("/list")
  @ResponseBody
  public TableDataInfo list(SysLogininfor logininfor) {
    startPage();
    List<SysLogininfor> list = logininforService.selectLogininforList(logininfor);
    return getDataTable(list);
  }

  @Log(title = "登陆日志", businessType = BusinessType.EXPORT)
  @RequiresPermissions("monitor:logininfor:export")
  @PostMapping("/export")
  @ResponseBody
  public AjaxResult export(SysLogininfor logininfor) {
    List<SysLogininfor> list = logininforService.selectLogininforList(logininfor);
    ExcelUtil<SysLogininfor> util = new ExcelUtil<SysLogininfor>(SysLogininfor.class);
    return util.exportExcel(list, "登陆日志");
  }

  @RequiresPermissions("monitor:logininfor:remove")
  @Log(title = "登陆日志", businessType = BusinessType.DELETE)
  @PostMapping("/remove")
  @ResponseBody
  public AjaxResult remove(String ids) {
    return toAjax(logininforService.deleteLogininforByIds(ids));
  }

  @RequiresPermissions("monitor:logininfor:remove")
  @Log(title = "登陆日志", businessType = BusinessType.CLEAN)
  @PostMapping("/clean")
  @ResponseBody
  public AjaxResult clean() {
    logininforService.cleanLogininfor();
    return success();
  }
}
