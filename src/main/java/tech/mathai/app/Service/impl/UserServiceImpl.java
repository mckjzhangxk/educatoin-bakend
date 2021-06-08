package tech.mathai.app.Service.impl;

import com.alibaba.fastjson.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.mathai.app.Entity.User;
import tech.mathai.app.Mapper.UserAttrMapper;
import tech.mathai.app.Mapper.UserMapper;
import tech.mathai.app.Service.UserService;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired(required = false)
    private UserMapper userMapper;
    @Autowired(required = false)
    private UserAttrMapper userAttrMapper;
    private Random random = new Random();

    @Override
    public List<User> getAllUsers(String column, String order, List<Integer> types) {
        return userMapper.getAllUsers(column, order, types);
    }

    @Override
    public List<User> getUsersByType(List<Integer> types) {
        return userMapper.getAllUsersByType(types);
    }

    @Override
    public User selectUserById(String id) {
        return userMapper.selectUserById(id);
    }

    @Override
    public List<User> selectUser(String type, String keyWord, String column, String order, List<Integer> types) {
        switch (type) {
            case "name":
                return userMapper.selectUserByName(keyWord, column, order, types);
            case "phone":
                return userMapper.selectUserByPhone(keyWord, column, order, types);
            case "account":
                return userMapper.selectUserByAccount(keyWord, column, order, types);
            default:
                return userMapper.getAllUsers(column, order, Arrays.asList(1, 0, 2));
        }
    }


    @Override
    public void updateUser(User user) {
        userMapper.updateUser(user);
        if (user.getType() == 0) {
            Random random = new Random();
            String parentId = user.getParentId();
            String teacher = user.getTeacherId();
            if (parentId != null && teacher != null) {
                userMapper.deleteStuParent(user.getId());
                userMapper.deleteStuTeacher(user.getId());
                userMapper.setStuParent(String.valueOf(random.nextInt(99999)), parentId, user.getId());
                userMapper.setStuTeacher(String.valueOf(random.nextInt(99999)), teacher, user.getId());
            }
        }
    }

    @Override
    public void addUser(User user) {
//        String pass = user.getPassword();
//        user.setPassword(BCrypt.hashpw(pass,BCrypt.gensalt()));
        userMapper.addUser(user);
        if (user.getType() == 0) {
            Random random = new Random();
            String id = String.valueOf(random.nextInt(99999));
            userMapper.setStuTeacher(id, user.getTeacher(), user.getId());
            String parentName = user.getParentName();
            String parentId = user.getParentId();
            User parent = new User();
            parent.setId(String.valueOf(random.nextInt(99999)));
            parent.setName(parentName);
            parent.setAccount(parentId);
            parent.setPassword(parentId);
            parent.setPhone(user.getPhone());
            parent.setTeacher("");
            parent.setType(1);
            userMapper.addUser(parent);
            userMapper.setStuParent(String.valueOf(random.nextInt(99999)), parent.getId(), user.getId());
            //默认的学生属性
            userAttrMapper.defaultInsert(UUID.randomUUID().toString().replace("-", ""), user.getId());
        }
    }


    @Override
    public void deleteUser(String id) {
        User user = userMapper.selectUserById(id);
        userMapper.deleteUser(id);
        switch (user.getType()) {
            case 0:
                userMapper.deleteUser(user.getParentId());
                userMapper.deleteStuTeacher(id);
                userMapper.deleteStuParent(id);
            case 1:
                userMapper.deleteStuParentByParentID(id);
            case 2:
                userMapper.deleteStuTeacherByTeacherID(id);
        }
    }

    @Override
    public User checkUserName(String name) {
        return userMapper.checkUserByName(name);
    }

    @Override
    public User checkUser(String username, String password) throws NoSuchAlgorithmException {
        return userMapper.getUser(username, password);

    }

    //    @Override
//    public boolean userLogin(String id, String password) {
//        User user = userMapper.selectUserById(id);
//        String pass = user.getPassword();
//        if(BCrypt.checkpw(password,pass)){
//            return true;
//        }else{
//            return false;
//        }
//    }


    @Override
    public String login(User userinfo) {
        JSONObject x = new JSONObject();

        if (userinfo.getPassword().equals("123") && userinfo.getName().equals("test")) {
            x.put("state", true);
            x.put("userid", "1111111111");
            x.put("type", 0);
        } else {
            x.put("state", false);
        }
        return x.toJSONString();
    }


}
