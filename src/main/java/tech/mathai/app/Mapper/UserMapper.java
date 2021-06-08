package tech.mathai.app.Mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import tech.mathai.app.Entity.User;

import java.util.List;

@Mapper
public interface UserMapper {
    List<User> getAllUsers(@Param("column") String column,@Param("order") String order,@Param("types") List<Integer> types);
    User selectUserById(@Param("id") String id);
    List<User> selectUserByName(@Param("name") String name,@Param("column") String column,@Param("order") String order,@Param("types") List<Integer> types);
    List<User> selectUserByPhone(@Param("phone") String phone,@Param("column") String column,@Param("order") String order,@Param("types") List<Integer> types);
    List<User> selectUserByAccount(@Param("account") String account,@Param("column") String column,@Param("order") String order,@Param("types") List<Integer> types);
    void updateUser(User user);
    void addUser(User user);
    void deleteUser(@Param("id") String id);
    void deleteStuTeacher(@Param("id") String id);
    void deleteStuParent(@Param("id") String id);
    void deleteStuTeacherByTeacherID(@Param("id") String id);
    void deleteStuParentByParentID(@Param("id") String id);
    void setStuTeacher(@Param("id") String id,@Param("teacher") String teacher,@Param("student") String student);
    void setStuParent(@Param("id") String id,@Param("parent") String parent,@Param("student") String student);
    List<User> getAllUsersByType(@Param("list") List<Integer> type);
    User checkUserByName(@Param("username") String username);
    User getUser(@Param("username") String username,@Param("password") String password);
}
