package tech.mathai.app.payload;

import tech.mathai.app.Entity.User;

public class LogInfo {
    private String id;
    private String name;
    private String token;

    public LogInfo(String id, String name, String token) {
        this.id = id;
        this.name = name;
        this.token = token;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
