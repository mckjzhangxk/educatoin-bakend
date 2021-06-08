package tech.mathai.app.Mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import tech.mathai.app.Entity.QuestionReport;
import tech.mathai.app.Entity.User;
import tech.mathai.app.Entity.UserAttr;

import java.util.List;

/**
 * Created by zlsyt on 2020/6/19.
 */
@Mapper
public interface UserAttrMapper {
    UserAttr checkUser(UserAttr x);
    UserAttr selectUserAttr(QuestionReport q);
    UserAttr selectStudentNameAndParentId(@Param("studentId") String sid);
    UserAttr getTeacherByStudentId(@Param("studentId") String sid);
    UserAttr getTeacherByParentId(@Param("parentId") String sid);
    UserAttr getStudentByParentId(@Param("parentId") String parentId);



    UserAttr getParentByStudentId(@Param("studentId")  String sid);



    void insert(UserAttr u);
    void defaultInsert(@Param("id") String id,@Param("userid") String userid);
    void update(UserAttr u);
    int updateOfTeacher(UserAttr u);
    int updateOfParent(UserAttr u);

    UserAttr getStudentById(UserAttr userinfo);

    List<User> getUserByPhone(@Param("phone") String phone);


    void addUser(User user);
    UserAttr selectUserByOpenId(@Param("openid") String openid);
    void updateUserType(User user);
}
