package com.mmh.dynamicdatasource.service;

import com.mmh.dynamicdatasource.entity.Student;
import com.mmh.dynamicdatasource.entity.Teacher;
import com.mmh.dynamicdatasource.entity.User;

/**
 * @author muminghui
 * @date 2019/9/27 17:17
 */
public interface IDynamicDataSourceService {
    User getUserData(Integer id);

    Teacher getTeacherData(Integer id);

    Student getStudentData(Integer id);
}
