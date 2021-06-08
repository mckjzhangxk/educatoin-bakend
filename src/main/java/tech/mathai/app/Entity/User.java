package tech.mathai.app.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class User {
    private String id;
    private String name;
    private int age;
    @JsonProperty("username")
    private String account;
    private String password;
    private String phone;
    private String teacher;
    private String teacherId;
    private String parentId;
    private String parentName;
    private int type;

//    public User(@JsonProperty("username") String username, @JsonProperty("password") String password) {
//        this.account = username;
//        this.password = password;
//    }
//
//    public User(String id, String name, int age, String account, String password, String phone, String teacher,
//                String parentId, String parentName, int type) {
//        this.id = id;
//        this.name = name;
//        this.age = age;
//        this.account = account;
//        this.password = password;
//        this.phone = phone;
//        this.teacher = teacher;
//        this.parentId = parentId;
//        this.parentName = parentName;
//        this.type = type;
//    }
//
//    public User(String id, String name, int age, String account, String password, String phone, int type) {
//        this.id = id;
//        this.name = name;
//        this.age = age;
//        this.account = account;
//        this.password = password;
//        this.phone = phone;
//        this.type = type;
//    }
//
//
//    public User() {
//    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }
}
