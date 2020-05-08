package com.haojishu.exception.file;

import com.haojishu.exception.base.BaseException;

/**
 * 文件信息异常类
 * 
 * @author sulwan@126.com
 */
public class FileException extends BaseException
{
    private static final long serialVersionUID = 1L;

    public FileException(String code, Object[] args)
    {
        super("file", code, args, null);
    }

}
