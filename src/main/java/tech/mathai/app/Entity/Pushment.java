package tech.mathai.app.Entity;

import lombok.Data;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zlsyt on 2020/6/24.
 */
@Data
public class Pushment {

    private String uid;
    private String studentId;
    private String questionId;
    private String type;
    private String stime;
    private String querytime;
    private String queryBtime;
    private String valid;
    private String handle;

    private List<String> questionIds=new ArrayList<>();


    public List<String> getQuestionIds() {
        return questionIds;
    }

    public void setQuestionIds(List<String> questionIds) {
        this.questionIds = questionIds;
    }

    public String getQuerytime() {
        return querytime;
    }

    public void setQuerytime(String querytime) {
        this.querytime = querytime;
    }

    public String getQueryBtime() {
        return queryBtime;
    }

    public void setQueryBtime(String queryBtime) {
        this.queryBtime = queryBtime;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStime() {
        return stime;
    }

    public void setStime(String stime) {
        this.stime = stime;
    }

    public String getValid() {
        return valid;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public void setValid(String valid) {
        this.valid = valid;
    }
}
