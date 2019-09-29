package com.mmh.dynamicdatasource.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.bind.RelaxedDataBinder;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 启动动态数据源请在启动类中 添加 @Import(DynamicDataSourceRegister.class)
 * @author mumh@2423528736@qq.com
 * @date 2019/5/30 11:34
 */
@Slf4j
public class DynamicDataSourceRegister implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    private ConversionService conversionService = new DefaultConversionService();
    private PropertyValues dataSourcePropertyValues;

    /**如配置文件中未指定数据源类型，使用该默认值*/
    private static final String DATASOURCE_TYPE_DEFAULT = "com.alibaba.druid.pool.DruidDataSource";

    /**默认数据源*/
    private DataSource defaultDataSource;

    /**用户自定义数据源*/
    private Map<String,DataSource> slaveDataSource = new HashMap<>();

    private static String DB_NAME = "names";
    /**配置文件中前缀*/
    private static String DB_DEFAULT_VALUE = "custom.datasource";

    @Value("${custom.datasource.defaultname}")
    private String defaultDBname;

    @Override
    public void setEnvironment(Environment environment) {
        initDefaultDataSource(environment);
        initSlaveDataSource(environment);
    }

    /**
     * 初始化主数据源
     * @param env
     */
    private void initDefaultDataSource(Environment env){
        // 读取主数据源
        Map<String,Object> dsMap = new HashMap<>();
        RelaxedPropertyResolver propertyResolver = new RelaxedPropertyResolver(env, DB_DEFAULT_VALUE+".");
        dsMap.put("driver-class-name",propertyResolver.getProperty("driver-class-name"));
        dsMap.put("url",propertyResolver.getProperty("url"));
        dsMap.put("username",propertyResolver.getProperty("username"));
        dsMap.put("password",propertyResolver.getProperty("password"));
        defaultDataSource = buildDataSource(dsMap);
        //默认数据源放到动态数据源里
        slaveDataSource.put(defaultDBname,defaultDataSource);
        dataBinder(defaultDataSource, env);
    }

    /**为DataSource绑定更多数据*/
    private void dataBinder(DataSource dataSource, Environment env) {
        RelaxedDataBinder dataBinder = new RelaxedDataBinder(dataSource);
        //dataBinder.setValidator(new LocalValidatorFactory().run(this.applicationContext));
        dataBinder.setConversionService(conversionService);
        dataBinder.setIgnoreNestedProperties(false);
        dataBinder.setIgnoreInvalidFields(false);
        dataBinder.setIgnoreUnknownFields(true);
        if (dataSourcePropertyValues == null) {
            Map<String, Object> rpr = new RelaxedPropertyResolver(env, DB_DEFAULT_VALUE).getSubProperties(".");
            Map<String, Object> values = new HashMap<String, Object>(rpr);
            // 排除已经设置的属性
            values.remove("driver-class-name");
            values.remove("url");
            values.remove("username");
            values.remove("password");
            dataSourcePropertyValues = new MutablePropertyValues(values);
        }
        dataBinder.bind(dataSourcePropertyValues);
    }

    private void initSlaveDataSource(Environment env){
        // 读取配置文件获取更多数据源
        RelaxedPropertyResolver propertyResolver = new RelaxedPropertyResolver(env,DB_DEFAULT_VALUE+".");
        String dsPrefixs = propertyResolver.getProperty(DB_NAME);
        for(String dsPrefix:dsPrefixs.split(",")){
            Map<String,Object> dsMap = propertyResolver.getSubProperties(dsPrefix+".");
            DataSource ds = buildDataSource(dsMap);
            slaveDataSource.put(dsPrefix,ds);
            dataBinder(ds,env);
        }
    }

    private DataSource buildDataSource(Map<String,Object> dataSourceMap){
        try {
            Object type = dataSourceMap.get("type");
            if(type == null){
                type = DATASOURCE_TYPE_DEFAULT;
            }
            Class<? extends DataSource> dataSourceType = (Class<? extends DataSource>)Class.forName((String)type);
            String driverClassName = dataSourceMap.get("driver-class-name").toString();
            String url = dataSourceMap.get("url").toString();
            String username = dataSourceMap.get("username").toString();
            String password = dataSourceMap.get("password").toString();
            // 自定义DataSource配置
            DataSourceBuilder factory = DataSourceBuilder.create()
                    .driverClassName(driverClassName).url(url).type(dataSourceType)
                    .username(username).password(password);
            return factory.build();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        Map<Object,Object> targetDataSources = new HashMap<>();
        //添加默认数据源
        targetDataSources.put("dataSource",defaultDataSource);
        DynamicDataSourceContextHolder.dataSourceIds.add("dataSource");
        //添加其他数据源
        targetDataSources.putAll(slaveDataSource);
        for(String key:slaveDataSource.keySet()){
            DynamicDataSourceContextHolder.dataSourceIds.add(key);
        }

        //创建DynamicDataSource
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(DynamicDataSource.class);
        beanDefinition.setSynthetic(true);
        MutablePropertyValues mpv = beanDefinition.getPropertyValues();
        mpv.addPropertyValue("defaultTargetDataSource",defaultDataSource);
        mpv.addPropertyValue("targetDataSources",targetDataSources);

        registry.registerBeanDefinition("dataSource",beanDefinition);
        log.info("Dynamic DataSource Registry");
    }
}
