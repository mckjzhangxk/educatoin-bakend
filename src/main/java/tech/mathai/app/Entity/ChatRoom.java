package tech.mathai.app.Entity;

import lombok.Data;

/**
 * Created by mathai on 21-3-2.
 */

@Data
public class ChatRoom {

    private String uid;
    private String fromid;
    private String toid;
    private String valid;

    public String getValid() {
        return valid;
    }

    public void setValid(String valid) {
        this.valid = valid;
    }

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
}
