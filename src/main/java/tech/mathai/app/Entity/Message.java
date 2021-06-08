package tech.mathai.app.Entity;

import lombok.Data;

/**
 * Created by Administrator on 2020/7/10.
 */
@Data
public class Message {
    private String uid;
    private String receive;
    private String ids;
    private String message;
    public static final String SPIT="#";

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getReceive() {
        return receive;
    }

    public void setReceive(String receive) {
        this.receive = receive;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
