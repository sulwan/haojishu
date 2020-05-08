package com.haojishu.service.impl.system;

import com.haojishu.constant.ShiroConstants;
import com.haojishu.constant.UserConstants;
import com.haojishu.domain.SysUser;
import com.haojishu.enums.UserStatus;
import com.haojishu.exception.user.*;
import com.haojishu.service.system.ISysLoginService;
import com.haojishu.service.system.ISysUserService;
import com.haojishu.shiro.service.SysPasswordService;
import com.haojishu.utils.DateUtils;
import com.haojishu.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * 登录校验方法
 *
 * @author sulwan@126.com
 */
@Service("ISysLoginService")
public class SysLoginServiceImpl implements ISysLoginService {
  @Autowired private SysPasswordService passwordService;

  @Autowired private ISysUserService userService;

  @Resource private RedisUtils redisUtil;

  /** 登录 */
  @Override
  public SysUser login(String username, String password) {

    String code = (String) redisUtil.get(username + ShiroConstants.CURRENT_CAPTCHA);
    // 验证码校验
    if (StringUtils.isEmpty(code)) {
      System.out.println("验证码错误");
      throw new CaptchaException();
    }
    // 用户名或密码为空 错误
    if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
      System.out.printf("用户名或密码为空");
      throw new UserNotExistsException();
    }
    // 密码如果不在指定范围内 错误
    if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
        || password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
      System.out.println("密码如果不在指定范围内错误");
      throw new UserPasswordNotMatchException();
    }

    // 用户名不在指定范围内 错误
    if (username.length() < UserConstants.USERNAME_MIN_LENGTH
        || username.length() > UserConstants.USERNAME_MAX_LENGTH) {
      System.out.println("用户名不在指定范围内");
      throw new UserPasswordNotMatchException();
    }

    // 查询用户信息
    SysUser user = userService.selectUserByLoginName(username);

    if (user == null && maybeMobilePhoneNumber(username)) {
      user = userService.selectUserByPhoneNumber(username);
    }

    if (user == null && maybeEmail(username)) {
      user = userService.selectUserByEmail(username);
    }

    if (user == null) {
      System.out.println("用户不存在错误");
      throw new UserNotExistsException();
    }

    if (UserStatus.DELETED.getCode().equals(user.getDelFlag())) {

      System.out.println("用户被删除错误");
      throw new UserDeleteException();
    }

    if (UserStatus.DISABLE.getCode().equals(user.getStatus())) {
      System.out.println("用户停用错误");
      throw new UserBlockedException();
    }

    passwordService.validate(user, password);

    redisUtil.del(username + ShiroConstants.CURRENT_CAPTCHA);

    System.out.println("用户登录成功");
    recordLoginInfo(user);
    return user;
  }

  private boolean maybeEmail(String username) {
    if (!username.matches(UserConstants.EMAIL_PATTERN)) {
      return false;
    }
    return true;
  }

  private boolean maybeMobilePhoneNumber(String username) {
    if (!username.matches(UserConstants.MOBILE_PHONE_NUMBER_PATTERN)) {
      return false;
    }
    return true;
  }

  /** 记录登录信息 */
  @Override
  public void recordLoginInfo(SysUser user) {
    String code = (String) redisUtil.get(user.getUserName() + ShiroConstants.CURRENT_IP);
    user.setLoginIp(code);
    user.setLoginDate(DateUtils.getNowDate());
    userService.updateUserInfo(user);
  }
}
