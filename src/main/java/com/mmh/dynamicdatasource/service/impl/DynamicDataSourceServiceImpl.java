package com.mmh.dynamicdatasource.service.impl;

import com.mmh.dynamicdatasource.config.TargetDataSource;
import com.mmh.dynamicdatasource.dao.StudentMapper;
import com.mmh.dynamicdatasource.dao.TeacherMapper;
import com.mmh.dynamicdatasource.dao.UserMapper;
import com.mmh.dynamicdatasource.entity.Student;
import com.mmh.dynamicdatasource.entity.Teacher;
import com.mmh.dynamicdatasource.entity.User;
import com.mmh.dynamicdatasource.service.IDynamicDataSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author muminghui
 * @date 2019/9/27 17:17
 */
@Service
@TargetDataSource(name = "")
public class DynamicDataSourceServiceImpl implements IDynamicDataSourceService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private TeacherMapper teacherMapper;
    @Autowired
    private StudentMapper studentMapper;

    @Override
    public User getUserData(Integer id) {
        return userMapper.selectByPrimaryKey(id);
    }

    /**指定数据源-ds1*/
    @TargetDataSource(name="ds1")
    @Override
    public Teacher getTeacherData(Integer id) {
        return teacherMapper.selectByPrimaryKey(id);
    }

    /**指定数据源-ds2*/
    @TargetDataSource(name="ds2")
    @Override
    public Student getStudentData(Integer id) {
        return studentMapper.selectByPrimaryKey(id);
    }
}
