package tech.mathai.app.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import tech.mathai.app.Entity.AnalysisDto;
import tech.mathai.app.Entity.StudentPlan;
import tech.mathai.app.Entity.UserAttr;
import tech.mathai.app.Mapper.StudentPlanMapper;
import tech.mathai.app.utils.URLUtil;

import java.util.*;

/**
 * Created by zlsyt on 2020/6/24.
 */
@Service
public class StudentPlanService {

    public StudentPlan selectplans(UserAttr u){
        List<StudentPlan> plans=m_dao.select(u);
        StudentPlan ret=m_dao.selectStudentJob(u.getId());
        if(ret==null){
            ret=new StudentPlan();
            ret.setValid("F");
        }
        ret.setStudentId(u.getId());
        for(StudentPlan p:plans){
            if("T".equals(p.getType())){
                String zsdId=p.getZsdId();
                if(zsdId!=null)
                    ret.getTeacher_selected().add(zsdId);
                String zhuantiId=p.getZhuantiId();
                if(zhuantiId!=null)
                    ret.getTeacher_selected_zhuantiIds().add(zhuantiId);
            }else if("P".equals(p.getType())){
                ret.getParent_selected().add(p.getZsdId());
            }else if("S".equals(p.getType())){
                ret.getStudent_selected().add(p.getZsdId());
            }
        }
        return ret;
    }

    public int savePlan(StudentPlan plan){
        List<String> tmp;

        String type=plan.getType();
        if("T".equals(type)){
            tmp=plan.getTeacher_selected();
        }else if("P".equals(type)){
            tmp=plan.getParent_selected();
        }else if("S".equals(type)){
            tmp=plan.getStudent_selected();
        }else {
            tmp=new ArrayList<>();
        }

        int flag=m_dao.delete(plan);
        int count=0;
        for(String zsdid:tmp){
            plan.setUid(UUID.randomUUID().toString().replace("-",""));
            plan.setZsdId(zsdid);
            count+=m_dao.insert(plan);
        }
        return count;
    }
    public String saveStudentPlan(StudentPlan plan) {
        int flag=m_dao.delete(plan);
        List<String> tmp=plan.getTeacher_selected();
        for(String zsdid:tmp){
            plan.setUid(UUID.randomUUID().toString().replace("-",""));
            plan.setZsdId(zsdid);
            m_dao.insert(plan);
        }
        tmp=plan.getTeacher_selected_zhuantiIds();
        for(String zsdid:tmp){
            plan.setUid(UUID.randomUUID().toString().replace("-",""));
            plan.setZhuantiId(zsdid);
            m_dao.insertZhuanti(plan);
        }


        JSONObject python_param=new JSONObject();
//        ['studentId','pattern','peridic','pushDate']
        python_param.put("studentId",plan.getStudentId());
        python_param.put("pattern",plan.getPattern());
        python_param.put("peridic",plan.getPeridic());
        python_param.put("pushDate",plan.getStartdate());


        JSONObject python_result = URLUtil.Post(env.getProperty("python_createAutoJob"), python_param);

        if(python_result!=null){
            return python_result.toJSONString();
        }else {
            JSONObject ret=new JSONObject();
            ret.put("success",false);
            ret.put("msg","网络连接错误，请联系管理员！");
            return ret.toJSONString();
        }

    }
    public String getStudentAnalysis(String userId){
        JSONObject acc=new JSONObject();
        JSONObject count=new JSONObject();
        List<AnalysisDto> studentAnalysis = m_dao.getStudentAnalysis(userId);


        for(AnalysisDto dto:studentAnalysis){
            String catelog=dto.getCatalog();
            float right=0;
            float total=1;
            if(acc.containsKey(catelog)){
                right=acc.getFloat(catelog);
                total=1+count.getFloat(catelog);
            }
            if(dto.getResult().equals("T")){
                right+=1.0f;
            }

            acc.put(catelog, right);
            count.put(catelog,total);
        }

        JSONObject ret=new JSONObject();

        if(acc.size()>0){
            ret.put("right",acc);
            ret.put("count",count);
            ret.put("result",1);
        }else {
            ret.put("result",0);

        }


        return ret.toJSONString();
    }

    public String cancleTask(UserAttr u) {
        JSONObject python_param=new JSONObject();
        python_param.put("studentId",u.getId());
        JSONObject python_result = URLUtil.Post(env.getProperty("python_cancleTask"), python_param);

        if(python_result==null){
            python_result=new JSONObject();
            python_result.put("success",false);
            python_result.put("message","服务器通信异常");

        }
        return python_result.toJSONString();
    }
    @Autowired
    private StudentPlanMapper m_dao;
    @Autowired
    private Environment env;


}
