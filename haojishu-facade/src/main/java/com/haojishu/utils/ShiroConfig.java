package com.haojishu.utils;

import com.haojishu.shiro.realm.UserRealm;
import com.haojishu.shiro.session.OnlineSessionDAO;
import com.haojishu.shiro.session.OnlineSessionFactory;
import com.haojishu.shiro.web.filter.LogoutFilter;
import com.haojishu.shiro.web.filter.captcha.CaptchaValidateFilter;
import com.haojishu.shiro.web.filter.kickout.KickoutSessionFilter;
import com.haojishu.shiro.web.filter.online.OnlineSessionFilter;
import com.haojishu.shiro.web.filter.sync.SyncOnlineSessionFilter;
import com.haojishu.shiro.web.session.OnlineWebSessionManager;
import com.haojishu.shiro.web.session.SpringSessionValidationScheduler;
import com.haojishu.utils.spring.SpringUtils;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.crazycake.shiro.IRedisManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 权限配置加载
 *
 * @author sulwan@126.com
 */
@Configuration
public class ShiroConfig {
  public static final String PREMISSION_STRING = "perms[\"{0}\"]";

  // Session超时时间，单位为毫秒（默认30分钟）
  @Value("${shiro.session.expireTime}")
  private int expireTime;

  // 相隔多久检查一次session的有效性，单位毫秒，默认就是10分钟
  @Value("${shiro.session.validationInterval}")
  private int validationInterval;

  // 同一个用户最大会话数
  @Value("${shiro.session.maxSession}")
  private int maxSession;

  // 踢出之前登录的/之后登录的用户，默认踢出之前登录的用户
  @Value("${shiro.session.kickoutAfter}")
  private boolean kickoutAfter;

  // 验证码开关
  @Value("${shiro.user.captchaEnabled}")
  private boolean captchaEnabled;

  // 验证码类型
  @Value("${shiro.user.captchaType}")
  private String captchaType;

  // 设置Cookie的域名
  @Value("${shiro.cookie.domain}")
  private String domain;

  // 设置cookie的有效访问路径
  @Value("${shiro.cookie.path}")
  private String path;

  // 设置HttpOnly属性
  @Value("${shiro.cookie.httpOnly}")
  private boolean httpOnly;

  // 设置Cookie的过期时间，秒为单位
  @Value("${shiro.cookie.maxAge}")
  private int maxAge;

  // 登录地址
  @Value("${shiro.user.loginUrl}")
  private String loginUrl;

  // 权限认证失败地址
  @Value("${shiro.user.unauthorizedUrl}")
  private String unauthorizedUrl;

  // redis地址
  @Value("${spring.redis.host}")
  private String host;

  // redis端口
  @Value("${spring.redis.port}")
  private String port;

  // redis密码
  @Value("${spring.redis.password}")
  private String password;

  // 配置redisSessionDAO
  @Bean
  public RedisSessionDAO redisSessionDAO() {
    RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
    redisSessionDAO.setRedisManager(redisManager());
    return redisSessionDAO;
  }

  /**
   * 配置缓存器
   *
   * @return
   */
  @Bean
  public RedisCacheManager cacheManagers() {
    RedisCacheManager redisCacheManager = new RedisCacheManager();
    redisCacheManager.setRedisManager(redisManager());
    redisCacheManager.setPrincipalIdFieldName("userId");
    return redisCacheManager;
  }

  // 配置redisManager
  public IRedisManager redisManager() {
    RedisManager redisManager = new RedisManager();
    redisManager.setHost(host + ":" + port);
    if (StringUtils.isNotBlank(password)) {
      redisManager.setPassword(password);
    }
    //        redisManager.setTimeout((int) EXPIRE_SECONDS);
    return redisManager;
  }

  /** 自定义Realm */
  @Bean
  public UserRealm userRealm() {
    UserRealm userRealm = new UserRealm();
    userRealm.setCacheManager(cacheManagers());
    return userRealm;
  }

  /** 自定义sessionDAO会话 */
  @Bean
  public OnlineSessionDAO sessionDAO() {
    OnlineSessionDAO sessionDAO = new OnlineSessionDAO();
    return sessionDAO;
  }

  /** 自定义sessionFactory会话 */
  @Bean
  public OnlineSessionFactory sessionFactory() {
    OnlineSessionFactory sessionFactory = new OnlineSessionFactory();
    return sessionFactory;
  }

  /** 会话管理器 */
  @Bean
  public OnlineWebSessionManager sessionManager() {
    OnlineWebSessionManager manager = new OnlineWebSessionManager();
    // 加入缓存管理器
    manager.setCacheManager(cacheManagers());
    // 删除过期的session
    manager.setDeleteInvalidSessions(true);
    // 设置全局session超时时间
    manager.setGlobalSessionTimeout(expireTime * 60 * 1000);
    // 去掉 JSESSIONID
    manager.setSessionIdUrlRewritingEnabled(false);
    // 定义要使用的无效的Session定时调度器
    manager.setSessionValidationScheduler(
        SpringUtils.getBean(SpringSessionValidationScheduler.class));
    // 是否定时检查session
    manager.setSessionValidationSchedulerEnabled(true);
    // 自定义SessionDao
    manager.setSessionDAO(sessionDAO());
    // 自定义sessionFactory
    manager.setSessionFactory(sessionFactory());
    return manager;
  }

  /** 安全管理器 */
  @Bean(name = "securityManager")
  public SecurityManager securityManager(
      UserRealm userRealm, SpringSessionValidationScheduler springSessionValidationScheduler) {
    DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
    // 设置realm.
    securityManager.setRealm(userRealm);
    // 记住我
    securityManager.setRememberMeManager(rememberMeManager());
    // 注入缓存管理器;
    //        securityManager.setCacheManager(getEhCacheManager());
    // session管理器
    securityManager.setSessionManager(sessionManager());
    return securityManager;
  }

  /** 退出过滤器 */
  public LogoutFilter logoutFilter() {
    LogoutFilter logoutFilter = new LogoutFilter();
    logoutFilter.setCacheManager(cacheManagers());
    logoutFilter.setLoginUrl(loginUrl);
    return logoutFilter;
  }

  /** Shiro过滤器配置 */
  @Bean
  public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
    ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
    // Shiro的核心安全接口,这个属性是必须的
    shiroFilterFactoryBean.setSecurityManager(securityManager);
    // 身份认证失败，则跳转到登录页面的配置
    shiroFilterFactoryBean.setLoginUrl(loginUrl);
    // 权限认证失败，则跳转到指定页面
    shiroFilterFactoryBean.setUnauthorizedUrl(unauthorizedUrl);
    // Shiro连接约束配置，即过滤链的定义
    LinkedHashMap<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
    // 对静态资源设置匿名访问
    filterChainDefinitionMap.put("/favicon.ico**", "anon");
    filterChainDefinitionMap.put("/ruoyi.png**", "anon");
    filterChainDefinitionMap.put("/css/**", "anon");
    filterChainDefinitionMap.put("/docs/**", "anon");
    filterChainDefinitionMap.put("/fonts/**", "anon");
    filterChainDefinitionMap.put("/img/**", "anon");
    filterChainDefinitionMap.put("/ajax/**", "anon");
    filterChainDefinitionMap.put("/js/**", "anon");
    filterChainDefinitionMap.put("/hao/**", "anon");
    filterChainDefinitionMap.put("/druid/**", "anon");
    filterChainDefinitionMap.put("/captcha/captchaImage**", "anon");
    // 退出 logout地址，shiro去清除session
    filterChainDefinitionMap.put("/logout", "logout");
    // 不需要拦截的访问
    filterChainDefinitionMap.put("/login", "anon,captchaValidate");
    // 系统权限列表
    // filterChainDefinitionMap.putAll(SpringUtils.getBean(IMenuService.class).selectPermsAll());

    Map<String, Filter> filters = new LinkedHashMap<String, Filter>();
    filters.put("onlineSession", onlineSessionFilter());
    filters.put("syncOnlineSession", syncOnlineSessionFilter());
    filters.put("captchaValidate", captchaValidateFilter());
    filters.put("kickout", kickoutSessionFilter());
    // 注销成功，则跳转到指定页面
    filters.put("logout", logoutFilter());
    shiroFilterFactoryBean.setFilters(filters);

    // 所有请求需要认证
    filterChainDefinitionMap.put("/**", "user,kickout,onlineSession,syncOnlineSession");
    shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);

    return shiroFilterFactoryBean;
  }

  /** 自定义在线用户处理过滤器 */
  @Bean
  public OnlineSessionFilter onlineSessionFilter() {
    OnlineSessionFilter onlineSessionFilter = new OnlineSessionFilter();
    onlineSessionFilter.setLoginUrl(loginUrl);
    return onlineSessionFilter;
  }

  /** 自定义在线用户同步过滤器 */
  @Bean
  public SyncOnlineSessionFilter syncOnlineSessionFilter() {
    SyncOnlineSessionFilter syncOnlineSessionFilter = new SyncOnlineSessionFilter();
    return syncOnlineSessionFilter;
  }

  /** 自定义验证码过滤器 */
  @Bean
  public CaptchaValidateFilter captchaValidateFilter() {
    CaptchaValidateFilter captchaValidateFilter = new CaptchaValidateFilter();
    captchaValidateFilter.setCaptchaEnabled(captchaEnabled);
    captchaValidateFilter.setCaptchaType(captchaType);
    return captchaValidateFilter;
  }

  /** cookie 属性设置 */
  public SimpleCookie rememberMeCookie() {
    SimpleCookie cookie = new SimpleCookie("rememberMe");
    cookie.setDomain(domain);
    cookie.setPath(path);
    cookie.setHttpOnly(httpOnly);
    cookie.setMaxAge(maxAge * 24 * 60 * 60);
    return cookie;
  }

  /** 记住我 */
  public CookieRememberMeManager rememberMeManager() {
    CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
    cookieRememberMeManager.setCookie(rememberMeCookie());
    cookieRememberMeManager.setCipherKey(Base64.decode("fCq+/xW488hMTCD+cmJ3aQ=="));
    return cookieRememberMeManager;
  }

  /** 同一个用户多设备登录限制 */
  public KickoutSessionFilter kickoutSessionFilter() {
    KickoutSessionFilter kickoutSessionFilter = new KickoutSessionFilter();
    kickoutSessionFilter.setCacheManager(cacheManagers());
    kickoutSessionFilter.setSessionManager(sessionManager());
    // 同一个用户最大的会话数，默认-1无限制；比如2的意思是同一个用户允许最多同时两个人登录
    kickoutSessionFilter.setMaxSession(maxSession);
    // 是否踢出后来登录的，默认是false；即后者登录的用户踢出前者登录的用户；踢出顺序
    kickoutSessionFilter.setKickoutAfter(kickoutAfter);
    // 被踢出后重定向到的地址；
    kickoutSessionFilter.setKickoutUrl("/login?kickout=1");
    return kickoutSessionFilter;
  }

  /** 开启Shiro注解通知器 */
  @Bean
  public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(
      @Qualifier("securityManager") SecurityManager securityManager) {
    AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor =
        new AuthorizationAttributeSourceAdvisor();
    authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
    return authorizationAttributeSourceAdvisor;
  }
}
