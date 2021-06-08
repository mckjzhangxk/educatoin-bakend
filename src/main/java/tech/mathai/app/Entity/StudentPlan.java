package tech.mathai.app.Entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zlsyt on 2020/6/24.
 */
@Data
public class StudentPlan {
    private String uid;
    private String studentId;
    private String type;
    private String zsdId;
    private String zhuantiId;
    private String valid;
    private List<String> student_selected =new ArrayList<>();
    private List<String> teacher_selected=new ArrayList<>();
    private List<String> parent_selected=new ArrayList<>();

    private String startdate;
    private String pattern;
    private Integer peridic;
    private List<String> teacher_selected_zhuantiIds=new ArrayList<>();

    public String getValid() {
        return valid;
    }

    public void setValid(String valid) {
        this.valid = valid;
    }

    public String getZhuantiId() {
        return zhuantiId;
    }

    public void setZhuantiId(String zhuantiId) {
        this.zhuantiId = zhuantiId;
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public Integer getPeridic() {
        return peridic;
    }

    public void setPeridic(Integer peridic) {
        this.peridic = peridic;
    }

    public List<String> getTeacher_selected_zhuantiIds() {
        return teacher_selected_zhuantiIds;
    }

    public void setTeacher_selected_zhuantiIds(List<String> teacher_selected_zhuantiIds) {
        this.teacher_selected_zhuantiIds = teacher_selected_zhuantiIds;
    }

    public void setStudent_selected(List<String> student_selected) {
        this.student_selected = student_selected;
    }

    public void setTeacher_selected(List<String> teacher_selected) {
        this.teacher_selected = teacher_selected;
    }

    public void setParent_selected(List<String> parent_selected) {
        this.parent_selected = parent_selected;
    }

    public List<String> getStudent_selected() {
        return student_selected;
    }

    public List<String> getTeacher_selected() {
        return teacher_selected;
    }

    public List<String> getParent_selected() {
        return parent_selected;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getZsdId() {
        return zsdId;
    }

    public void setZsdId(String zsdId) {
        this.zsdId = zsdId;
    }


}
