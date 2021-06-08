package tech.mathai.app.Controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tech.mathai.app.Entity.User;
import tech.mathai.app.Service.UserService;
import tech.mathai.app.payload.LogInfo;
import tech.mathai.app.payload.Response;
import tech.mathai.app.utils.CryptUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
@RequestMapping("/api3")
@CrossOrigin(origins = {"http://localhost:7001", "http://118.190.247.136:9001"}, allowCredentials = "true")
//@CrossOrigin(origins="*")
public class UserController {

    @Autowired
    private UserService userService;


    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") String id) {
        return userService.selectUserById(id);
    }

    @GetMapping("/all")
    public Response getUsers(@RequestParam("page") Integer pageNum, @RequestParam("size") Integer size,
                             @RequestParam(value = "column", required = false, defaultValue = "id") String column,
                             @RequestParam(value = "order", required = false, defaultValue = "asc") String order,
                             @RequestParam(value = "types", required = false, defaultValue = "0,1,2") List<Integer> types) {
        pageNum = pageNum == null ? 1 : pageNum;
        size = size == null ? 5 : size;
        PageHelper.startPage(pageNum, size);
        List<User> users = userService.getAllUsers(column, order, types);
        PageInfo pageInfo = new PageInfo(users);
        return Response.set("success", pageInfo);

    }

    @GetMapping("/type")
    public Response getAllusersByType(@RequestParam(value = "page", required = false) Integer pageNum,
                                      @RequestParam(value = "size", required = false) Integer size,
                                      @RequestParam(value = "types", required = false) List<Integer> types) {
        if (types != null) {
            if (pageNum != null && size != null) {
                pageNum = pageNum == null ? 1 : pageNum;
                size = size == null ? 5 : size;
                PageHelper.startPage(pageNum, size);
                List<User> users = userService.getUsersByType(types);
                PageInfo pageInfo = new PageInfo(users);
                return Response.set("success", pageInfo);
            } else {
                return Response.set("success", userService.getUsersByType(types));
            }
        } else {
            return Response.set("wrong parameter", null);
        }
    }

    @GetMapping("/select")
    public Response selectUsers(@RequestParam("type") String type, @RequestParam("keyword") String keyWord,
                                @RequestParam("page") Integer pageNum, @RequestParam("size") Integer size,
                                @RequestParam(value = "column", required = false, defaultValue = "id") String column,
                                @RequestParam(value = "order", required = false, defaultValue = "asc") String order,
                                @RequestParam(value = "types", required = false, defaultValue = "0,1,2") List<Integer> types) {
        pageNum = pageNum == null ? 1 : pageNum;
        size = size == null ? 5 : size;
        PageHelper.startPage(pageNum, size);
        List<User> users = userService.selectUser(type, keyWord, column, order, types);
        PageInfo pageInfo = new PageInfo(users);
        return Response.set("success", pageInfo);
    }

    @PostMapping("/")
    public Response addUser(@RequestBody User user) {
        userService.addUser(user);
        return Response.set("success", null);

    }

//    @PostMapping("/login")
//    public Response login(@RequestParam("id") String id, @RequestParam("password") String password) {
//        if (userService.userLogin(id, password)) {
//            return Response.set("success", null);
//        } else {
//            return Response.set("fail", null);
//        }
//    }

    @PutMapping("/")
    public Response updateUser(@RequestBody User user) {
        userService.updateUser(user);
        return Response.set("success", null);

    }

    @DeleteMapping("/{id}")
    public Response deleteUser(@PathVariable("id") String id) {
        userService.deleteUser(id);
        return Response.set("success", null);
    }

    @PostMapping("/login")
    public Response login(@RequestBody User userInfo, HttpServletRequest request) throws NoSuchAlgorithmException {
        User user = userService.checkUser(userInfo.getAccount(), userInfo.getPassword());
        if (user != null) {
            HttpSession session = request.getSession();
            String token = CryptUtil.hash(user.getAccount());
            session.setAttribute("token", token);
            session.setAttribute("account", user.getAccount());
            return Response.set("login success", new LogInfo(user.getId(), user.getName(), token));
        } else {
            return Response.set("login fail", null);
        }
    }

    @PostMapping("/verify")
    public Response verify(@RequestParam("token") String token, @RequestParam String account) {
        if (CryptUtil.verify(token, account)) {
            return Response.set(101, "success", null);
        } else {
            return Response.set(102, "fail", null);
        }

    }


    @GetMapping("/logout")
    public Response logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return Response.set("user is not log in", null);
        } else {
            session.invalidate();
            return Response.set("logout success", null);
        }
    }
}
