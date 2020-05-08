package com.haojishu.exception.user;

import com.haojishu.exception.base.BaseException;


/**
 * 用户信息异常类
 * 
 * @author sulwan@126.com
 */
public class UserException extends BaseException
{
    private static final long serialVersionUID = 1L;

    public UserException(String code, Object[] args)
    {
        super("user", code, args, null);
    }
}
