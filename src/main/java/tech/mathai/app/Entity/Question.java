package tech.mathai.app.Entity;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * Created by zlsyt on 2020/6/19.
 */
@Data
public class Question {
    private String uid;
    private String title;
    private String url;
    private String catalog;
    private String remark;
    private String answer;
    private float difficult;
    private float complex;
    private float creative;
    private String stime;


    @Override
    public String toString() {
        return "Question{" +
                "uid='" + uid + '\'' +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", catalog='" + catalog + '\'' +
                ", remark='" + remark + '\'' +
                ", answer='" + answer + '\'' +
                ", difficult=" + difficult +
                ", complex=" + complex +
                ", creative=" + creative +
                ", stime='" + stime + '\'' +
                '}';
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
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

    public String getStime() {
        return stime;
    }

    public void setStime(String stime) {
        this.stime = stime;
    }

    public void load(JSONObject questionJSON){

        this.setUid(questionJSON.getString("id"));
        this.setUrl(questionJSON.getString("url"));
        this.setDifficult(questionJSON.getFloat("nd"));
        this.setCreative(questionJSON.getFloat("cxd"));
        this.setComplex(questionJSON.getFloat("zhd"));
        this.setRemark(questionJSON.getString("zsd"));
        this.setAnswer(questionJSON.getString("answer"));
    }

    public void save(JSONObject questionJSON){
        questionJSON.put("url",url);
        questionJSON.put("nd",difficult);
        questionJSON.put("cxd",creative);
        questionJSON.put("zhd",complex);
        questionJSON.put("zsd",remark);
        questionJSON.put("answer",answer);

        try {
            JSONObject jsonObject = JSONObject.parseObject(answer);
            questionJSON.put("answer",jsonObject);
        }catch (Exception e){}


    }
}
