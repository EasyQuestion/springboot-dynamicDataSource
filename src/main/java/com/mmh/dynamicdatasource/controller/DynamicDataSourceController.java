package com.mmh.dynamicdatasource.controller;

import com.mmh.dynamicdatasource.entity.Student;
import com.mmh.dynamicdatasource.entity.Teacher;
import com.mmh.dynamicdatasource.entity.User;
import com.mmh.dynamicdatasource.service.IDynamicDataSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author muminghui
 * @date 2019/9/27 17:16
 */
@RestController
@RequestMapping(value = "/dds")
public class DynamicDataSourceController {

    @Autowired
    private IDynamicDataSourceService service;
    @RequestMapping(value = "/user/{id}")
    public User getAllUserData(@PathVariable Integer id){
        return service.getUserData(id);
    }

    @RequestMapping(value = "/teacher/{id}")
    public Teacher getAllTeacherData(@PathVariable Integer id) {
        return service.getTeacherData(id);
    }

    @RequestMapping(value = "/student/{id}")
    public Student getAllStudentData(@PathVariable Integer id) {
        return service.getStudentData(id);
    }
}
