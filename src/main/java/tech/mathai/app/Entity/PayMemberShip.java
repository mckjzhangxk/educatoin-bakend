package tech.mathai.app.Entity;

import lombok.Data;

/**
 * Created by mathai on 20-12-14.
 */
@Data
public class PayMemberShip {
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public Integer getMouths() {
        return mouths;
    }

    public void setMouths(Integer mouths) {
        this.mouths = mouths;
    }

    private String uuid;
    private String orderTime;
    private Float price;
    private String userid;
    private String expireTime;
    private Integer mouths;
}
