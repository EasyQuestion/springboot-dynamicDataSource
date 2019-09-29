package com.mmh.dynamicdatasource;

import com.mmh.dynamicdatasource.config.DynamicDataSourceRegister;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@Import(DynamicDataSourceRegister.class)
@SpringBootApplication
public class DynamicdatasourceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DynamicdatasourceApplication.class, args);
    }

}
