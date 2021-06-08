package tech.mathai.app.Entity;

import lombok.Data;

/**
 * Created by Administrator on 2020/7/10.
 */
@Data
public class ChatMessage {
    private String uid;
    private String fromid;
    private String toid;
    private String message;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFromid() {
        return fromid;
    }

    public void setFromid(String fromid) {
        this.fromid = fromid;
    }

    public String getToid() {
        return toid;
    }

    public void setToid(String toid) {
        this.toid = toid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
