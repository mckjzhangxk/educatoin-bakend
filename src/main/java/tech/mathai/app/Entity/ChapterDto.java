package tech.mathai.app.Entity;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * Created by mathai on 21-3-19.
 */
@Data
public class ChapterDto {
    private String uid;
    private String name;
    private String parent;
    private String remark;
    private Integer isLeaf;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getIsLeaf() {
        return isLeaf;
    }

    public void setIsLeaf(Integer isLeaf) {
        this.isLeaf = isLeaf;
    }

//    private String uid;
//    private String name;
//    private String parent;
//    private String remark;
//    private Integer isLeaf;
    public void load(JSONObject message) {
        this.setUid(message.getString("id"));
        this.setName(message.getString("name"));
        this.setParent(message.getString("parent"));
        this.setRemark(message.getString("remark"));

        if(message.getBoolean("isQuestion")){
            this.setIsLeaf(1);
        }else {
            this.setIsLeaf(0);
        }

    }
}
