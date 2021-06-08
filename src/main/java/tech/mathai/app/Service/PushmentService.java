package tech.mathai.app.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import tech.mathai.app.Entity.Message;
import tech.mathai.app.Entity.Pushment;
import tech.mathai.app.Entity.StudentSubmit;
import tech.mathai.app.Entity.UserAttr;
import tech.mathai.app.Mapper.AssignmentMapper;
import tech.mathai.app.Mapper.MessageMapper;
import tech.mathai.app.Mapper.PushmentMapper;
import tech.mathai.app.utils.URLUtil;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by zlsyt on 2020/6/24.
 */
@Service
public class PushmentService {

    public List<Pushment> getPushments(UserAttr u){
        /**
         * 获得所有没有 完成 的推送，返回的List
         * 是 一个个独立的推送题目
         * */
        Pushment p=new Pushment();
        p.setStudentId(u.getId());

        List<Pushment> plans= pushmentMapper.selectUnhandle(p);

        //返回A,B两套题目
        List<Pushment> ret=new ArrayList<>();

        String lastTime="";
        String lastType="";
        for(Pushment plan:plans){
            if(!lastType.equals(plan.getType())||!lastTime.equals(plan.getStime())){
                ret.add(new Pushment());
            }
            lastTime=plan.getStime();
            lastType=plan.getType();

            Pushment c=ret.get(ret.size()-1);
            c.setUid(plan.getStime()+"_"+plan.getType());
            c.getQuestionIds().add(plan.getQuestionId());
            c.setValid(plan.getValid());
        }
        return ret;
    }

    public List<Pushment> getPushments(JSONObject query){
        /**
         * 返回的List是 一个个独立的推送题目
         * */
        Pushment p=new Pushment();
        p.setStudentId(query.getString("studentId"));
        p.setQuerytime(query.getString("startTime"));


        List<Pushment> plans= pushmentMapper.selectAfter(p);

        //返回A,B两套题目
        List<Pushment> ret=new ArrayList<>();

        String lastTime="";
        String lastType="";
        for(Pushment plan:plans){
            if(!lastType.equals(plan.getType())||!lastTime.equals(plan.getStime())){
                ret.add(new Pushment());
            }
            lastTime=plan.getStime();
            lastType=plan.getType();

            Pushment c=ret.get(ret.size()-1);
            c.setUid(plan.getStime()+"_"+plan.getType());
            c.getQuestionIds().add(plan.getQuestionId());
            c.setHandle(plan.getHandle());
        }
        return ret;
    }
    /**
     * 老师主动给学生推送,但是为了防止一天推送多次，
     * 使用命名规范 :日期_index,表示某一天的基地次
     * */
    public String  createPushments(UserAttr u) throws IOException {
        JSONObject param=new JSONObject();
        param.put("studentId",u.getId());

        JSONObject result = URLUtil.Post(env.getProperty("python_createPushment"), param);

        if(result!=null){
             return result.toJSONString();
        }else {
            JSONObject ret=new JSONObject();
            ret.put("success",false);
            ret.put("msg","网络连接错误，请联系管理员！");
            return ret.toJSONString();
        }
    }


    public JSONObject insertPushment(String d){
        JSONObject pushmentData=JSONObject.parseObject(d);
        return insertPushment(pushmentData.getString("studentId"),pushmentData.getJSONArray("questionIds"));
    }
    public JSONObject insertPushment(String studentId,JSONArray qids){



        String stime=new SimpleDateFormat("yyyy-MM-dd").format(getCurrentPushTime(0));
        Pushment p=new Pushment();
        p.setStudentId(studentId);
        p.setQuerytime(stime+"%");
        p.setQueryBtime(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(getCurrentPushTime(13)));

        Set<String> types=new HashSet<>();
        List<Pushment> select = pushmentMapper.select(p);
        stime=stime+"_"+select.size();
        for(Pushment x:select){
            types.add(x.getType());
        }


        String currentType=""+((char)('A'+types.size()));



        for(int i=0;i<qids.size();i++){
            p.setUid(UUID.randomUUID().toString().replace("-",""));
            p.setStudentId(studentId);
            p.setStime(stime);
            p.setQuestionId(qids.getString(i));
            p.setType(currentType);
            p.setValid("T");
            pushmentMapper.insert(p);
        }


        {
            Message message = new Message();
            message.setUid(UUID.randomUUID().toString().replace("-", ""));
            message.setReceive(studentId);

            message.setMessage("Hi~,同学，你有一条新的题目推送");
            message.setIds("");
            messageMapper.insert(message);
        }
        JSONObject ret=new JSONObject();
        ret.put("success",true);
        return ret;
    }


    public void insertPushment2Students(String d){
        JSONObject pushmentData=JSONObject.parseObject(d);
        JSONArray studentIds = pushmentData.getJSONArray("studentIds");
        JSONArray questionIds = pushmentData.getJSONArray("questionIds");

        for(int i=0;i<studentIds.size();i++){
            insertPushment(studentIds.getString(i),questionIds);
        }

    }
    public int setPushmentValid(){
        Pushment plan=new Pushment();
        plan.setQuerytime(getTypeAPushTime());
        return pushmentMapper.update(plan);
    }
    //获得推送时间，本周四
    private static Date getPushTime(int hours){
        int offset[]={-3,-4,-5,-6,0,-1,-2};
        Date d=new Date();
        Calendar cl=Calendar.getInstance();
        cl.setTime(d);
        int dayofWeek=cl.get(Calendar.DAY_OF_WEEK);
        cl.add(Calendar.DAY_OF_MONTH,offset[dayofWeek-1]);

        cl.set(Calendar.HOUR,0);
        cl.set(Calendar.MINUTE,0);
        cl.set(Calendar.SECOND,0);
        cl.add(Calendar.HOUR,hours);
        return cl.getTime();
    }
    //获得推送时间，当前时间
    private static Date getCurrentPushTime(int hours){
        Date d=new Date();
        Calendar cl=Calendar.getInstance();
        cl.setTime(d);
        cl.set(Calendar.HOUR,0);
        cl.set(Calendar.MINUTE,0);
        cl.set(Calendar.SECOND,0);
        cl.add(Calendar.HOUR,hours);
        return cl.getTime();
    }
    private static String getTypeAPushTime(){
        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
        Date d=getPushTime(0);
        return sf.format(d);
    }
    private static  String getTypeBPushTime(){
        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date d=getPushTime(13);
        return sf.format(d);
    }

    public String removeStudentPushment(JSONObject p) {
        String studentId = p.getString("studentId");
        String hwId=p.getString("hwId");
        String[] sps=hwId.split("_");
        String stime=sps[0]+"_"+sps[1];
        String type=sps[2];

        Pushment pushment=new Pushment();
        pushment.setStudentId(studentId);
        pushment.setStime(stime);
        pushment.setType(type);
        pushmentMapper.remove(pushment);


        StudentSubmit studentSubmit=new StudentSubmit();
        studentSubmit.setHwid(hwId);
        studentSubmit.setStudentid(studentId);
        assignmentMapper.remove(studentSubmit);


        return "{}";
    }


    @Autowired
    private AssignmentMapper assignmentMapper;

    @Autowired
    private PushmentMapper pushmentMapper;
    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private Environment env;

    public static void main(String[] args){
        System.out.println(getTypeAPushTime());
        System.out.println(getTypeBPushTime());
//        int offset[]={-3,-4,-5,-6,0,-1,-2};
//        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//        Date d=new Date();
//
//        for(int i=-10;i<10;i++){
//            Calendar cl=Calendar.getInstance();
//            cl.setTime(d);
//            cl.add(Calendar.DAY_OF_MONTH,i);
//            System.out.print(sf.format(cl.getTime())+"-->");
//            int dayofWeek=cl.get(Calendar.DAY_OF_WEEK);
//
//            cl.add(Calendar.DAY_OF_MONTH,offset[dayofWeek-1]);
//            cl.set(Calendar.HOUR,0);
//            cl.set(Calendar.MINUTE,0);
//            cl.set(Calendar.SECOND,0);
//            System.out.println(sf.format(cl.getTime()));
//        }



    }

}
