package com.haojishu.task;

import com.haojishu.utils.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 定时任务调度测试
 * 
 * @author sulwan@126.com
 */
@Component("hjsTask")
public class HjsTask
{
    public void hjsMultipleParams(String s, Boolean b, Long l, Double d, Integer i)
    {
        System.out.println(StringUtils.format("执行多参方法： 字符串类型{}，布尔类型{}，长整型{}，浮点型{}，整形{}", s, b, l, d, i));
    }

    public void hjsParams(String params)
    {
        System.out.println("执行有参方法：" + params);
    }

    public void hjsNoParams()
    {
        System.out.println("执行无参方法");
    }
}
