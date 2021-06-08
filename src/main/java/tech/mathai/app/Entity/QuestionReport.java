package tech.mathai.app.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.apache.ibatis.annotations.Param;

/**
 * Created by Administrator on 2020/6/18.
 */

@Data
public class QuestionReport {
    private String name;
    private String studentName;
    private String userid;
    private float acc;
    private float difficult;
    private  float complex;
    private float creative;
    private String report;
    private String currenterrid;
    private String[] errid;
    private String[] rightids;
    private String hwId;
    private String questionType;
    private String stime;
    private String score;
    private String testChapter;
    private String errorZsds;
    private String teacherReplay;

    public String[] getRightids() {
        return rightids;
    }

    public void setRightids(String[] rightids) {
        this.rightids = rightids;
    }

    public String getTeacherReplay() {
        return teacherReplay;
    }

    public void setTeacherReplay(String teacherReplay) {
        this.teacherReplay = teacherReplay;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getTestChapter() {
        return testChapter;
    }

    public void setTestChapter(String testChapter) {
        this.testChapter = testChapter;
    }

    public String getErrorZsds() {
        return errorZsds;
    }

    public void setErrorZsds(String errorZsds) {
        this.errorZsds = errorZsds;
    }

    public String getStime() {
        return stime;
    }

    public void setStime(String stime) {
        this.stime = stime;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    //    public QuestionReport(@JsonProperty("userid") String userid,@JsonProperty("acc") float acc,@JsonProperty("difficult") float difficult,
//                          @JsonProperty("complex") float complex,@JsonProperty("creative") float creative,@JsonProperty("report") String report,
//                          @JsonProperty("errid")String[] errid) {
//        this.userid = userid;
//        this.acc = acc;
//        this.difficult = difficult;
//        this.complex = complex;
//        this.creative = creative;
//        this.report = report;
//        this.errid = errid;
//    }


//    public Assignment(@JsonProperty("name") String name,@JsonProperty("teacherid") String teacherid,@JsonProperty("qtype") int qtype,@JsonProperty("questionids") List<String> questionids) {
//        this.name = name;
//        this.teacherid = teacherid;
//        this.qtype = qtype;
//        this.questionids = questionids;
//    }

    public String getHwId() {
        return hwId;
    }

    public void setHwId(String hwId) {
        this.hwId = hwId;
    }
    public String getCurrenterrid() {
        return currenterrid;
    }

    public void setCurrenterrid(String currenterrid) {
        this.currenterrid = currenterrid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public float getAcc() {
        return acc;
    }

    public void setAcc(float acc) {
        this.acc = acc;
    }

    public float getDifficult() {
        return difficult;
    }

    public void setDifficult(float difficult) {
        this.difficult = difficult;
    }

    public float getComplex() {
        return complex;
    }

    public void setComplex(float complex) {
        this.complex = complex;
    }

    public float getCreative() {
        return creative;
    }

    public void setCreative(float creative) {
        this.creative = creative;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public String[] getErrid() {
        return errid;
    }

    public void setErrid(String[] errid) {
        this.errid = errid;
    }
}
