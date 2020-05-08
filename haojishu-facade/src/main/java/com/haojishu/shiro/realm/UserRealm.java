package com.haojishu.shiro.realm;

import com.haojishu.constant.ShiroConstants;
import com.haojishu.domain.SysUser;
import com.haojishu.exception.user.*;
import com.haojishu.service.system.ISysLoginService;
import com.haojishu.service.system.ISysMenuService;
import com.haojishu.service.system.ISysRoleService;
import com.haojishu.utils.RedisUtils;
import com.haojishu.utils.ShiroUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

/**
 * 自定义Realm 处理登录 权限
 *
 * @author sulwan@126.com
 */
public class UserRealm extends AuthorizingRealm {
  private static final Logger log = LoggerFactory.getLogger(UserRealm.class);

  @Autowired private ISysMenuService menuService;

  @Autowired private ISysRoleService roleService;

  @Autowired private ISysLoginService loginService;
  @Autowired RedisUtils redisUtil;

  /** 授权 */
  @Override
  protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection arg0) {
    SysUser user = ShiroUtils.getSysUser();
    // 角色列表
    Set<String> roles = new HashSet<String>();
    // 功能列表
    Set<String> menus = new HashSet<String>();
    SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
    // 管理员拥有所有权限
    if (user.isAdmin()) {
      info.addRole("admin");
      info.addStringPermission("*:*:*");
    } else {
      roles = roleService.selectRoleKeys(user.getUserId());
      menus = menuService.selectPermsByUserId(user.getUserId());
      // 角色加入AuthorizationInfo认证对象
      info.setRoles(roles);
      // 权限加入AuthorizationInfo认证对象
      info.setStringPermissions(menus);
    }
    return info;
  }

  /** 登录认证 */
  @Override
  protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
      throws AuthenticationException {
    UsernamePasswordToken upToken = (UsernamePasswordToken) token;
    String username = upToken.getUsername();
    System.out.println("登录方法：" + username);
    String password = "";
    if (upToken.getPassword() != null) {
      password = new String(upToken.getPassword());
    }

    SysUser user = null;
    try {
      String ip = (String) ShiroUtils.getSession().getAttribute(ShiroConstants.CURRENT_IP);
      redisUtil.set(username + ShiroConstants.CURRENT_IP, ip);

      user = loginService.login(username, password);
    } catch (CaptchaException e) {
      throw new AuthenticationException(e.getMessage(), e);
    } catch (UserNotExistsException e) {
      throw new UnknownAccountException(e.getMessage(), e);
    } catch (UserPasswordNotMatchException e) {
      throw new IncorrectCredentialsException(e.getMessage(), e);
    } catch (UserPasswordRetryLimitExceedException e) {
      throw new ExcessiveAttemptsException(e.getMessage(), e);
    } catch (UserBlockedException e) {
      throw new LockedAccountException(e.getMessage(), e);
    } catch (RoleBlockedException e) {
      throw new LockedAccountException(e.getMessage(), e);
    } catch (Exception e) {
      log.info("对用户[" + username + "]进行登录验证..验证未通过{}", e.getMessage());
      throw new AuthenticationException(e.getMessage(), e);
    }
    SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user, password, getName());
    return info;
  }

  /** 清理缓存权限 */
  public void clearCachedAuthorizationInfo() {
    this.clearCachedAuthorizationInfo(SecurityUtils.getSubject().getPrincipals());
  }
}
