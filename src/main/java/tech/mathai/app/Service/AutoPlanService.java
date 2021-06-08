package tech.mathai.app.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import tech.mathai.app.Entity.Message;
import tech.mathai.app.Entity.QuestionReport;
import tech.mathai.app.Entity.StudentSubmit;
import tech.mathai.app.Entity.UserAttr;
import tech.mathai.app.Mapper.AssignmentMapper;
import tech.mathai.app.Mapper.PushmentMapper;
import tech.mathai.app.Mapper.QuestionReportMapper;
import tech.mathai.app.Mapper.UserAttrMapper;
import tech.mathai.app.utils.URLUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by Administrator on 2020/7/7.
 */
@Service
public class AutoPlanService {


    @Autowired
    private Environment env;

    public String loadUserPage(String userId) throws IOException {
        JSONObject jsonObject = AutoPlanStatic.readUserProfile(userId);


        JSONObject result = new JSONObject();
        JSONArray learned = new JSONArray();
        result.put("learned", learned);


        JSONArray courseList = jsonObject.getJSONArray("courseList");
        JSONObject plans = jsonObject.getJSONObject("plans");

        for (int i = 0; i < courseList.size(); i++) {
            String gradeId = courseList.getString(i);

            JSONArray planOfCourse = plans.getJSONArray(gradeId);
            String chapterId = null;
            if (planOfCourse.size() > 0)
                chapterId = planOfCourse.getString(planOfCourse.size() - 1);

            JSONObject g1 = new JSONObject();

            g1.put("id", gradeId);
            if (chapterId != null) {
                g1.put("current", chapterId);
                JSONObject jsonPlan = AutoPlanStatic.readUserCoursePlan(userId, chapterId);
                double progress = 100.0 * jsonPlan.getInteger("learned") / jsonPlan.getInteger("total");
                g1.put("progress", (int) progress);
            }

            learned.add(g1);
        }
        result.put("errors",jsonObject.getJSONArray("errors"));
        result.put("errorTime",jsonObject.getJSONObject("errorTime"));

        return result.toJSONString();
    }

    public String addUserCourse(String d) throws IOException {
        JSONObject jsonObject = JSONObject.parseObject(d);

        String userId = jsonObject.getString("userId");
        String courseId = jsonObject.getString("courseId");

        AutoPlanStatic.addCourse(userId, courseId);
        return null;
    }


    public String loadStudyPlanPage(String d) throws IOException {
        JSONObject jsonObject = JSONObject.parseObject(d);
        String userId = jsonObject.getString("userId");
        String courseId = jsonObject.getString("courseId");

        JSONArray plans = AutoPlanStatic.readUserProfile(userId).getJSONObject("plans").getJSONArray(courseId);

        JSONObject result = null;
        if (plans.size() > 0) {
            String chapterId = plans.getString(plans.size() - 1);
            result = AutoPlanStatic.readUserCoursePlan(userId, chapterId);

        }

        if (result == null) {
            result = new JSONObject();
        }
        return result.toJSONString();
    }

    public String changePlan(String d) throws IOException {
        /**
         * userId
         * courseId
         * zhangjieId
         * zsdId
         * */
        JSONObject newplan = JSONObject.parseObject(d);
        AutoPlanStatic.pushPlan(newplan);

        return loadStudyPlanPage(d);

    }

    public String finishOneChapter(String d) throws IOException {
        JSONObject newplan = JSONObject.parseObject(d);
        JSONObject result=new JSONObject();

        AutoPlanStatic.finishChapter(newplan);
        String s = AutoPlanStatic.popPlan(newplan);
        String ss=AutoPlanStatic.chooseNextChapter(newplan);

        if(s!=null){
            result.put("old",s);
        }
        if(ss!=null){
            result.put("new",ss);
        }
        result.put("success",true);
        if(s==null&&ss==null){
            result.put("success",false);
            result.put("message","没有自动推荐的章节了，同学，请自行浏览吧");
        }
        if(s==null&&ss!=null){
            result.put("selected",AutoPlanStatic.readUserCoursePlan(newplan.getString("userId"),ss));
        }
        if(s!=null&&ss==null){
            result.put("selected",AutoPlanStatic.readUserCoursePlan(newplan.getString("userId"),s));
        }
        return result.toJSONString();

    }
}
