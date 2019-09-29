package com.mmh.dynamicdatasource.config;

import java.lang.annotation.*;

/**
 * @author mumh@2423528736@qq.com
 * @date 2019/5/30 16:11
 * @Description 作用于类、接口或者方法上
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TargetDataSource {

    String name();
}
