package tech.mathai.app.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by zlsyt on 2020/6/19.
 */
public class UserAttr extends User {

    public float getCreative() {
        return creative;
    }

    public void setCreative(float creative) {
        this.creative = creative;
    }

    public float getComplex() {
        return complex;
    }

    public void setComplex(float complex) {
        this.complex = complex;
    }

    public float getDifficult() {
        return difficult;
    }

    public void setDifficult(float difficult) {
        this.difficult = difficult;
    }

    public float getErrcount() {
        return errcount;
    }

    public void setErrcount(float errcount) {
        this.errcount = errcount;
    }

    public float getTotalcount() {
        return totalcount;
    }

    public void setTotalcount(float totalcount) {
        this.totalcount = totalcount;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }




    public float getPcreative() {
        return pcreative;
    }

    public void setPcreative(float pcreative) {
        this.pcreative = pcreative;
    }

    public float getPcomplex() {
        return pcomplex;
    }

    public void setPcomplex(float pcomplex) {
        this.pcomplex = pcomplex;
    }

    public float getPdifficult() {
        return pdifficult;
    }

    public void setPdifficult(float pdifficult) {
        this.pdifficult = pdifficult;
    }

    public float getTcreative() {
        return tcreative;
    }

    public void setTcreative(float tcreative) {
        this.tcreative = tcreative;
    }

    public float getTcomplex() {
        return tcomplex;
    }

    public void setTcomplex(float tcomplex) {
        this.tcomplex = tcomplex;
    }

    public float getTdifficult() {
        return tdifficult;
    }

    public void setTdifficult(float tdifficult) {
        this.tdifficult = tdifficult;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getHeadImageUrl() {
        return headImageUrl;
    }

    public void setHeadImageUrl(String headImageUrl) {
        this.headImageUrl = headImageUrl;
    }

    public String getStudentMessage() {
        return studentMessage;
    }

    public void setStudentMessage(String studentMessage) {
        this.studentMessage = studentMessage;
    }

    public String getParentMessage() {
        return parentMessage;
    }

    public void setParentMessage(String parentMessage) {
        this.parentMessage = parentMessage;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    private String uid;
    private float creative;
    private float complex;
    private float difficult;
    private float pcreative;
    private float pcomplex;
    private float pdifficult;
    private float tcreative;
    private float tcomplex;
    private float tdifficult;

    private float errcount;
    private float totalcount;




    private String studentMessage;
    private String parentMessage;


    private String openId;
    private String headImageUrl;
    private String room;

}
