package tech.mathai.app.payload;

import lombok.Data;

@Data
public class Response {
    private int status;
    private String message;
    private Object object;

    public Response(int status, String message, Object object) {
        this.status = status;
        this.message = message;
        this.object = object;
    }

    public Response(String message, Object object) {
        this.message = message;
        this.object = object;
    }


    public static Response set(String message, Object object) {
        return new Response(message, object);
    }

    public static Response set(int status, String message, Object object) {
        return new Response(status, message, object);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
