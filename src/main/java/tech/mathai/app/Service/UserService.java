package tech.mathai.app.Service;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import tech.mathai.app.Entity.User;

import java.security.NoSuchAlgorithmException;
import java.util.List;


public interface UserService {
    List<User> getAllUsers(String column, String order, List<Integer> types);

    List<User> getUsersByType(List<Integer> types);

    User selectUserById(String id);

    List<User> selectUser(String type, String keyWord, String column, String order, List<Integer> types);

    void updateUser(User user);

    void addUser(User user);

    void deleteUser(String id);

    User checkUserName(String name);

    User checkUser(@Param("username") String username, @Param("password") String password) throws NoSuchAlgorithmException;

    String login(User userinfo);
}
