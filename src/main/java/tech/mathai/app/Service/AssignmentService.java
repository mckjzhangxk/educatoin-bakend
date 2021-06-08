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
public class AssignmentService {
    public List<List<QuestionReport>> getUnhandleHomework(String studentIds) {

        studentIds=JSONObject.parseObject(studentIds).getString("id");
        List<List<QuestionReport>> ret=new ArrayList<>();

        if(studentIds.length()>0){
            String[] split1 = studentIds.split(",");
            List<QuestionReport> ls=new ArrayList<>();
            for(String sid:split1){
                UserAttr u=new UserAttr();
                u.setId(sid);
                ls= assignMapper.getUnhandleHomework(u);
                //返回list of hwid, eg 2020-07-11_90_A
                for(int i=0;i<ls.size();i++){
                    QuestionReport r=ls.get(i);
                    String[] split = r.getHwId().split("_");
                    r.setStime(split[0]);
                    r.setQuestionType(split[2]);
                }
                ret.add(ls);
            }
        }
        return ret;
    }

    public List<StudentSubmit> select(String studentId,String hwId){
        List<StudentSubmit> ls= assignMapper.select(studentId,hwId);
        return ls;
    }

    /**
     * 学生把做的题目提交给了系统，入库后，会通知老师
     *
     * */
    public boolean submitQueston(JSONObject po) {

        String studentid = po.getString("userid");
        String hwid=po.getString("hwId");
        String teacherId=po.getString("teacherId");
        String studentName=po.getString("studentName");

        //插入学生提交的表
        JSONArray questions=po.getJSONArray("questions");
        for(int i=0;i<questions.size();i++){
            JSONObject ob=questions.getJSONObject(i);
            StudentSubmit sb=new StudentSubmit();
            sb.setUid(UUID.randomUUID().toString().replace("-",""));
            sb.setStudentid(studentid);
            sb.setHwid(hwid);
            sb.setHandle("F");
            sb.setQuestionId(ob.getString("questionId"));
            sb.setResult(ob.getString("result"));
            if(ob.containsKey("myAnswer"))
                sb.setMyAnswer(ob.getString("myAnswer"));
            if(ob.getString("audioAnswer")!=null){
                int index=env.getProperty("audio_path").lastIndexOf(File.separator);
                String prefix=env.getProperty("audio_path").substring(index+1);
                sb.setAudios(prefix+"/"+ob.getString("audioAnswer"));
            }


            sb.setImgs(ob.getString("imageAnswer"));

            assignMapper.insertStudentSubmit(sb);
        }
        String[] sp=hwid.split("_");

        String stimme=sp[0];
        String stype=sp[2];
        //记录学生完成当前推送
        pushmentMapper.updateStudentPushment(studentid,stimme+"_"+sp[1],stype);
        //提示消息
        Message message=new Message();
        message.setReceive(teacherId);
        message.setMessage(studentName+"同学提交了于"+stimme+"发布的"+stype+"卷，请及时查看!");
        message.setUid(UUID.randomUUID().toString().replace("-",""));
        message.setIds(studentid+Message.SPIT+hwid);
        messageService.insertMessage(message);
        return true;
    }


    public void submitAudio(JSONObject po) {
        String uid = po.getString("uid");
        String data = po.getString("data");

        byte[] bytes = Base64.decodeBase64(data);


        try {
            FileOutputStream fout=new FileOutputStream(env.getProperty("audio_path")+File.separator+uid);
            fout.write(bytes);
            fout.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public UserAttr getStudentAttr(String id){
        QuestionReport report=new QuestionReport();
        report.setUserid(id);
        UserAttr userAttr = userAttrMapper.selectUserAttr(report);
        return userAttr;
    }

    public String submitZizhuReport(QuestionReport report){
        for(String cid:report.getErrid()){
            report.setCurrenterrid(cid);
            questionReportMapper.insert(report);
        }

        UserAttr userAttr = userAttrMapper.selectUserAttr(report);
        if(userAttr==null){
            userAttr=new UserAttr();
            userAttr.setTotalcount(report.getErrid().length+report.getRightids().length);
            userAttr.setErrcount(report.getErrid().length);
            userAttr.setComplex(report.getComplex());
            userAttr.setCreative(report.getCreative());
            userAttr.setDifficult(report.getDifficult());
            userAttr.setId(report.getUserid());
            userAttr.setUid(UUID.randomUUID().toString().replace("-",""));
            userAttrMapper.insert(userAttr);
        }else {
            userAttr.setTotalcount(userAttr.getTotalcount()+(report.getErrid().length+report.getRightids().length));
            userAttr.setErrcount(userAttr.getErrcount()+report.getErrid().length);
            userAttr.setDifficult(report.getDifficult());
            userAttr.setComplex(report.getComplex());
            userAttr.setCreative(report.getCreative());
            //学生属性更新
            userAttrMapper.update(userAttr);
        }
        JSONObject result=new JSONObject();
        result.put("success",true);
        return report.toString();
    }

    public String submitZizhuReport1(QuestionReport report) throws IOException {
        String result = submitZizhuReport(report);

        AutoPlanStatic.addErrorQuestion(report.getUserid(),report.getErrid());

        return result;
    }
    //插入所有的错题，并更新个人属性，并记录学生完成作业情况
    public boolean submitReport(QuestionReport report){
        //错题的插入
        for(String cid:report.getErrid()){
            report.setCurrenterrid(cid);
            questionReportMapper.insert(report);
        }

        UserAttr userAttr = userAttrMapper.selectUserAttr(report);
        if(userAttr==null){
            userAttr=new UserAttr();
            userAttr.setTotalcount(10);
            userAttr.setErrcount(report.getErrid().length);
            userAttr.setComplex(report.getComplex());
            userAttr.setCreative(report.getCreative());
            userAttr.setDifficult(report.getDifficult());
            userAttr.setId(report.getUserid());
            userAttr.setUid(UUID.randomUUID().toString().replace("-",""));
            userAttrMapper.insert(userAttr);
        }else {
            userAttr.setTotalcount(userAttr.getTotalcount()+(report.getErrid().length+report.getRightids().length));
            userAttr.setErrcount(userAttr.getErrcount()+report.getErrid().length);
            userAttr.setDifficult(report.getDifficult());
            userAttr.setComplex(report.getComplex());
            userAttr.setCreative(report.getCreative());
            //学生属性更新
            userAttrMapper.update(userAttr);
        }
        //插入学生完成作业的情况
        if(report.getHwId()!=null){
            String hwid= report.getHwId();
            //报告的插入
            questionReportMapper.insertStuAss(report);
            //设置批改完成
            assignMapper.update(report);

            {
                //更新主观题的评分与 老师的评语
                String teacherReplay = report.getTeacherReplay();
                JSONArray r = JSONObject.parseArray(teacherReplay);
                for(int i=0;i<r.size();i++){
                    JSONObject item = r.getJSONObject(i);
                    assignMapper.updateStudentSubmitById(item.getString("id"),report.getUserid(),hwid, item.getString("result"),item.getString("teacherComment"));
                }

            }
            String stimme=hwid.substring(0,hwid.length()-1);
            String stype=hwid.substring(hwid.length()-1);

            Message message=new Message();
            message.setUid(UUID.randomUUID().toString().replace("-",""));
            //查询家长id,学生名字
            UserAttr userinfo = userAttrMapper.selectStudentNameAndParentId(report.getUserid());

            message.setReceive(userinfo.getParentId());
            message.setMessage(userinfo.getName()+"的" + stimme + "的" + stype + "卷评分完毕，点击查看报告");
            message.setIds(report.getUserid()+Message.SPIT+hwid);
            //给家长发消息
            messageService.insertMessage(message);

            //通知智慧brain~
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        JSONObject param=new JSONObject();
                        param.put("studentId",report.getUserid());
                        param.put("hwId",report.getHwId());
                        param.put("isRight",report.getAcc()>0.5);
                        URLUtil.Post(env.getProperty("python_feedback"), param);
                    }catch (Exception e){

                    }

                }
            },0);
        }
        return true;
    }

    @Autowired
    private MessageService messageService;
    @Autowired
    private AssignmentMapper assignMapper;
    @Autowired
    private UserAttrMapper userAttrMapper;
    @Autowired
    private QuestionReportMapper questionReportMapper;

    @Autowired
    private PushmentMapper pushmentMapper;

    @Autowired
    private Environment env;
}
