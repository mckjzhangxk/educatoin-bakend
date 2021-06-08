package tech.mathai.app.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.exceptions.ClientException;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.mathai.app.Entity.User;
import tech.mathai.app.Entity.UserAttr;
import tech.mathai.app.Mapper.UserAttrMapper;
import tech.mathai.app.Mapper.UserMapper;


import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created by zlsyt on 2020/6/19.
 */
@Service
public class UserAttrService {

    public String unbind(UserAttr u) {
        JSONObject result=new JSONObject();
        m_user_dao.deleteStuTeacher(u.getId());
        return result.toJSONString();
    }

    public UserAttr checkloginWithOpenId(String openid){
        return m_userAttr_dao.selectUserByOpenId(openid);
    }

    public UserAttr registerWithOpenId(JSONObject userinfo){

        Random random = new Random();
        String id = String.valueOf(random.nextInt(99999));
        String account=PREFIX+getNum(10);
        String password=account;

        UserAttr user=new UserAttr();

        user.setId(id);
        user.setAccount(account);
        user.setPassword(password);

        user.setType(-1);
        user.setName(userinfo.getString("nickname"));
        user.setHeadImageUrl(userinfo.getString("headimgurl"));
        user.setOpenId(userinfo.getString("openid"));

        m_userAttr_dao.addUser(user);

        return user;
    }

    //验证登录，返回所有属性
    public UserAttr login(UserAttr userinfo){
        UserAttr userattr= m_userAttr_dao.checkUser(userinfo);
        if(userattr==null)
            userattr=new UserAttr();
        return userattr;
    }

    public UserAttr setUserType(UserAttr userinfo) {

        m_userAttr_dao.updateUserType(userinfo);
        if(userinfo.getType()==0){
            //默认的学生属性
            m_userAttr_dao.defaultInsert(UUID.randomUUID().toString().replace("-", ""), userinfo.getId());
        }


        return userinfo;
    }


    public UserAttr getStudentInfo(UserAttr userinfo) {
        UserAttr userattr= m_userAttr_dao.getStudentById(userinfo);
        return userattr;
    }
    public UserAttr getTeacherByStudentId(String sid){
        return m_userAttr_dao.getTeacherByStudentId(sid);
    }
    public UserAttr getParentByStudentId(String sid) {
        return m_userAttr_dao.getParentByStudentId(sid);
    }


    public int updateOfTeacher(UserAttr userinfo){
        return m_userAttr_dao.updateOfTeacher(userinfo);
    }
    public int updateOfParent(UserAttr userinfo){
        return m_userAttr_dao.updateOfParent(userinfo);
    }

    public String askVertifyCode(String data) {
        JSONObject jsonObject = JSONObject.parseObject(data);
        String phone=jsonObject.getString("phone");
        Integer interval = jsonObject.getInteger("interval");


        JSONObject ret=new JSONObject();
        List<User> userByPhone = m_userAttr_dao.getUserByPhone(phone);
        if(userByPhone.size()>0){
            ret.put("success",false);
            ret.put("message","该手机号已经被注册");
            return ret.toJSONString();
        }

        {
            JSONObject vscode_result=sendVertifyCode(phone);
            boolean success=vscode_result.getString("Code").equals("OK");

            if(success){
                VSCode vsCode = new VSCode();
                vsCode.code=vscode_result.getString("code");
                vsCode.life=interval+LIFE;
                PHONE_VSCODE.put(phone,vsCode);

                ret.put("message",vsCode.code);
                ret.put("success",true);
            }else {
                ret.put("message",vscode_result.getString("Message"));
                ret.put("success",false);
            }


        }
        return ret.toJSONString();
    }

    public String askForPassword(String data) {
        JSONObject jsonObject = JSONObject.parseObject(data);
        String phone=jsonObject.getString("phone");
        Integer interval = jsonObject.getInteger("interval");


        JSONObject ret=new JSONObject();
        List<User> userByPhone = m_userAttr_dao.getUserByPhone(phone);
        if(userByPhone.size()==0){
            ret.put("success",false);
            ret.put("message","该手机号没有关联账号，请确认使用【注册时使用的手机】来找回密码");
            return ret.toJSONString();
        }
        if(userByPhone.size()>1){
            ret.put("success",false);
            ret.put("message","该手机号关联多个账号，请联系【管理员】进行修正");
            return ret.toJSONString();
        }

        JSONObject vscode_result=sendVertifyCode(phone);
        boolean success=vscode_result.getString("Code").equals("OK");

        if(success){
            ret.put("message",vscode_result.getString("code"));
            User user = userByPhone.get(0);
            ret.put("username",user.getAccount());
            ret.put("password",user.getPassword());
            ret.put("success",true);



        }else {
            ret.put("message",vscode_result.getString("Message"));
            ret.put("success",false);
        }

        return ret.toJSONString();
    }
    public String registerNewUserByPhone(UserAttr user) {
        /**
         *
         * 通过手机号注册账号
         *
         * 我徐奥把用户名，密码，自动生成，然后返回给客户端
         * */
        JSONObject ret=new JSONObject();

        Random random = new Random();
        String id = String.valueOf(random.nextInt(99999));
        String account=PREFIX+getNum(10)+getSuffix(user.getType());
        String password=account;


        user.setId(id);
        user.setAccount(account);
        user.setPassword(password);

        m_user_dao.addUser(user);

        if(user.getType()==0){
            //默认的学生属性
            m_userAttr_dao.defaultInsert(UUID.randomUUID().toString().replace("-", ""), user.getId());
        }

        String phone = user.getPhone();
        if(PHONE_VSCODE.containsKey(phone)){
            PHONE_VSCODE.remove(phone);
        }
        ret.put("success",true);
        ret.put("username",account);
        ret.put("password",password);
        ret.put("type",user.getType());


        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                SMSService smsService = new SMSService();
                smsService.sendUserInfo(phone,account,password);
            }
        },0);


        return ret.toJSONString();
    }



    private String getSuffix(int type) {
        switch (type){
            case 0:return "s";
            case 1:return "p";
            case 2:return "t";
        }
        return "t";
    }


    private String _message(UserAttr b,String chenghu,String targetId){
        String ss=b.getId().equals(targetId)?"无需再次绑定":"请先联系【管理员】,与【他/她】解绑定";
       return  "无效绑定,你已经与 "+" ["+b.getName()+"]"+chenghu+"绑定过了,"+ss;
    }
    public String setRelation(String data){
        /**
         * data:json
         * typemy
         * typeyou
         * idmy
         * idyou
         * */
        JSONObject result=new JSONObject();
        result.put("success",true);

        JSONObject d=JSONObject.parseObject(data);

        int typeMy=d.getInteger("typemy");
        int typeYou=d.getInteger("typeyou");

        String idMy=d.getString("idmy");
        String idYou=d.getString("idyou");

        Random random = new Random();
        String id = String.valueOf(random.nextInt(99999));

        if(typeMy==0&&typeYou==1){//我是学生
            UserAttr parent = m_userAttr_dao.getParentByStudentId(idMy);
            if(parent==null)
                m_user_dao.setStuParent(id,idYou,idMy);
            else {
                if(idYou.equals(parent.getId())){
                    result.put("message","你已经绑定了该家长，无需重复绑定");
                }else {
                    result.put("message","你已经和["+parent.getName()+"]绑定了");
                }
                result.put("success",false);
            }
        }else if(typeMy==1&&typeYou==0){//我是家长
            UserAttr parent = m_userAttr_dao.getParentByStudentId(idYou);
            if(parent==null)
                m_user_dao.setStuParent(id,idMy,idYou);
            else {
                if(idMy.equals(parent.getId())){
                    result.put("message","你已经绑定了该同学，无需重复绑定");
                }else {
                    result.put("message","该同学已经和["+parent.getName()+"]绑定了");
                }
                result.put("success",false);
            }

        }else if(typeMy==0&&typeYou==2){//我是学生
            UserAttr teacher = m_userAttr_dao.getTeacherByStudentId(idMy);
            if(teacher==null){
                m_user_dao.setStuTeacher(id, idYou, idMy);
            }else{
                result.put("message",_message(teacher,"老师",idYou));
                result.put("success",false);
            }

        }else if(typeMy==2&&typeYou==0){//我是老师
            UserAttr teacher = m_userAttr_dao.getTeacherByStudentId(idYou);
            if(teacher==null){
                m_user_dao.setStuTeacher(id,idMy,idYou);
            }else{
                if(idMy.equals(teacher.getId())){
                    result.put("message","你已经绑定了该同学，无需重复绑定");
                }else {
                    result.put("message","该同学已经和["+teacher.getName()+"]老师绑定了");
                }

                result.put("success",false);
            }

        }else {
            result.put("message","无效添加,家长之间，学生之间，老师之间暂不支持互相添加");
            result.put("success",false);
        }


        return result.toJSONString();
    }
    private JSONObject sendVertifyCode(String phone){
        Random random = new Random();

        String code = ""+getNum(4);

        SMSService smsService = new SMSService();
        JSONObject result = smsService.sendYangzhengMa(phone, code);
        result.put("code",code);

        return result;
    }
    public static  String getNum(int n) {
        Random random = new Random();

        String s="";
        for(int i=0;i<n;i++){
            s+=random.nextInt(10);
        }
        return s;
    }
    @Autowired
    private UserAttrMapper m_userAttr_dao;

    @Autowired
    private UserMapper m_user_dao;

    private static Map<String,VSCode> PHONE_VSCODE=new HashMap<>();

    private static String PREFIX="wd";


    private static Timer s_timer=new Timer();
    {
        s_timer.schedule(new TimerTask() {
            @Override
            synchronized public void run() {
                if(PHONE_VSCODE==null) return;


                List<String> remove=new ArrayList<String>();
                Set<String> phones = PHONE_VSCODE.keySet();
                for(String phone:phones){
                    VSCode vsCode = PHONE_VSCODE.get(phone);
                    vsCode.life-=PERIOD/1000.f;
                    if(vsCode.life<0)
                        remove.add(phone);
                }

                for(String s:remove){
                    PHONE_VSCODE.remove(s);
                }
            }
        },0,(int)PERIOD);
    }




    class VSCode{
        public float life;
        public String code;
    }
    public static final float PERIOD=500;//ms
    private static final float LIFE =600;//s


    public static void main(String[] args) throws ClientException, NoSuchAlgorithmException {
        String s="appid=wxd930ea5d5a258f4f&nonceStr=1101000000140429eb40476f8896f4c9&packageValue=Sign=WXPay&partnerId=1900000109&prepayId=1101000000140415649af9fc314aa427&timeStamp=1398746574&key=wxd930ea5d5a258f4f";
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(s.getBytes());
        String s1 =  new BigInteger(1, md.digest()).toString(16);
        System.out.println(s1.toUpperCase());
    }
}
