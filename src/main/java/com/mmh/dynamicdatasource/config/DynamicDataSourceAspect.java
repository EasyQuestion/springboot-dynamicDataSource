package com.mmh.dynamicdatasource.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 切换数据源Advice
 * @author mumh@2423528736@qq.com
 * @date 2019/5/30 16:15
 */
@Slf4j
@Aspect
@Order(-1)  //保证在@Transactional之前执行
@Component
public class DynamicDataSourceAspect {

    /**改变数据源*/
    @Before("@annotation(targetDataSource)")
    public void changeDataSource(JoinPoint joinPoint,TargetDataSource targetDataSource){
        String dbid = targetDataSource.name();

        if(!DynamicDataSourceContextHolder.isContainsDataSource(dbid)){
            log.error("数据源 [{}]不存在,请使用默认的数据源->{}",dbid,joinPoint.getSignature());
        }else{
            log.debug("使用数据源：{}->{}", dbid,joinPoint.getSignature());
            DynamicDataSourceContextHolder.setDataSourceType(dbid);
        }
    }

    @After("@annotation(targetDataSource)")
    public void clearDataSource(JoinPoint joinPoint,TargetDataSource targetDataSource){
        log.debug("清除数据源 {}->{}", targetDataSource.name(),joinPoint.getSignature());
        DynamicDataSourceContextHolder.clearDataSourceType();
    }
}
