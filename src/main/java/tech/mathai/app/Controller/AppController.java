package tech.mathai.app.Controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.exceptions.ClientException;
import org.apache.commons.codec.binary.Base64;
import org.jdom.JDOMException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tech.mathai.app.Entity.*;
import tech.mathai.app.Service.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;

import java.util.List;
import java.util.UUID;

/**
 * Created by Administrator on 2020/6/18.
 */
@RestController
@CrossOrigin("*")
public class AppController {


    @PostMapping(path = "getLinianUrl")
    public String getLinianUrl() {
        return "https://mp.weixin.qq.com/mp/homepage?__biz=MzAxODUzODQ5Nw==&hid=3";
    }

    @PostMapping(path = "uploadNewPage")
    public String uploadNewPage(@RequestBody String d) throws IOException {
        return questionService.uploadNewPage(d);
    }

    @PostMapping(path = "uploadGongLuePage")
    public String uploadGongLuePage(@RequestBody String d) throws IOException {
        return questionService.uploadGongLuePage(d);
    }

    @PostMapping(path = "tempUploadPage")
    public String tempUploadPage(@RequestBody String html) throws IOException {
        String s1 = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Document</title>\n" +
                "<link href=\"katex.css\" rel=\"stylesheet\"></llink>\n" +
                "</head>\n" +
                "<body>";
        String s2 = "</body>\n" +
                "</html>";
        String filename = UUID.randomUUID().toString() + ".html";
        FileOutputStream fileOutputStream = new FileOutputStream("/home/mathai/projects/db/static/tmpupload/" + filename);

        fileOutputStream.write(s1.getBytes());
        fileOutputStream.write(html.getBytes());
        fileOutputStream.write(s2.getBytes());
        fileOutputStream.close();
        return "http://192.168.1.36:7001/tmpupload/" + filename;
    }

    @PostMapping(path = "tempXuexiJihua")
    public String tempXuexiJihua() throws IOException {
        FileInputStream ios = new FileInputStream("/home/mathai/projects/db/graph/9c943518137c11eb8de5e34128807c25.json");
        byte[] bs = new byte[ios.available()];
        ios.read(bs);
        ios.close();

        return new String(bs);
    }


    @GetMapping(path = "helloWorld")
    public String helloWorld() {
        return "Hello World";
    }

    @GetMapping(path = "helloWorldParam")
    public String helloWorldParam(@RequestParam(name = "username") String username, @RequestParam(name = "password") String password) {
        return username + ":" + password;
    }


    @PostMapping(path = "helloWorldLogin")
    public String helloWorldLogin(@RequestBody UserAttr userinfo) {
        return "ok";
    }

    @PostMapping(path = "emptyParam")
    public String empty() {
        return "emptyParam";
    }

    @PostMapping(path = "listTest")
    public String listTest(@RequestBody String list) {
        JSONArray array = JSONArray.parseArray(list);
        return array.toJSONString();
    }


    @PostMapping(path = "getError")
    public String getError(@RequestBody UserAttr userinfo) {
        int a = 1 / 0;
        return "ok";
    }

    @PostMapping(path = "getHTMLFile")
    public String getFile(@RequestBody String s) throws IOException {
        return questionService.getHtmlFile(s);
    }

    /**
     * ?????????????????????????????????????????????app
     */
    @PostMapping(path = "login")
    public UserAttr login(@RequestBody UserAttr userinfo) {
        return userAttrService.login(userinfo);
    }

    @PostMapping(path = "setUserType")
    public UserAttr setUserType(@RequestBody UserAttr userinfo) {
        return userAttrService.setUserType(userinfo);
    }

    @PostMapping(path = "setRelation")
    public String setRelation(@RequestBody String data) {
        return userAttrService.setRelation(data);
    }


    @PostMapping(path = "askVertifyCode")
    public String askVertifyCode(@RequestBody String data) {
        return userAttrService.askVertifyCode(data);
    }

    @PostMapping(path = "askForPassword")
    public String askForPassword(@RequestBody String data) {
        return userAttrService.askForPassword(data);
    }


    @PostMapping(path = "registerNewUserByPhone")
    public String registerNewUserByPhone(@RequestBody UserAttr user) {
        return userAttrService.registerNewUserByPhone(user);
    }

    @PostMapping(path = "getParentByStudentId")
    public UserAttr getParentByStudentId(@RequestBody String sid) {
        return userAttrService.getParentByStudentId(sid.trim());
    }

    @PostMapping(path = "getTeacherByStudentId")
    public UserAttr getTeacherByStudentId(@RequestBody String sid) {
        return userAttrService.getTeacherByStudentId(sid.trim());
    }


    @PostMapping(path = "getChatMessage")
    public List<String> selectChatMessage(@RequestBody String rid) {
        return messageService.selectChatMessage(rid);
    }


    @PostMapping(path = "getTeacherAndChatMessage")
    public String getTeacherAndChatMessage(@RequestBody UserAttr user) {
        return messageService.getTeacherAndChatMessage(user);
    }

    @PostMapping(path = "createAndActiveRoom")
    public String createAndActiveRoom(@RequestBody ChatRoom room) {
        return chatService.createAndActiveRoom(room);
    }

    @PostMapping(path = "hasVideoChat")
    public String hasVideoChat(@RequestBody ChatRoom room) {
        return chatService.hasVideoChat(room);
    }

    @PostMapping(path = "removeRoomById")
    public String removeRoomById(@RequestBody ChatRoom room) {
        return chatService.removeRoomById(room);
    }

    /**
     * ?????????????????????
     */
    @PostMapping(path = "getChapter")
    public String getChapter() throws IOException {
        return questionService.getChapter();
    }

    @GetMapping(path = "getChapter")
    public String getChapter1() throws IOException {
        return questionService.getChapter();
    }

    @PostMapping(path = "deleteChapterById")
    public int deleteChapterById(@RequestBody String chapterId) throws IOException {
        return questionService.removeChapterNode(chapterId);
    }

    @PostMapping(path = "updateChapterNode")
    public String updateChapterNode(@RequestBody String d) throws IOException {
        return questionService.updateNode(d);
    }

    @PostMapping(path = "addNewChapterNode")
    public String addNewChapterNode(@RequestBody String d) throws IOException {
        return questionService.addNewChapterNode(d);
    }

    /**
     * ????????????????????????
     */
    @PostMapping(path = "getChapterVersion")
    public String getChapterVersion(@RequestBody String param) throws IOException {
        return questionService.checkChapter(param);
    }

    /**
     * ??????????????????ids[-1]????????????id,?????????????????????????????????
     */
    @PostMapping(path = "smartChooseQuestion")
    public String[] smartChooseQuestion(@RequestBody String[] ids) throws IOException {
        return questionService.smartChooseQuestion(ids);
    }

    @PostMapping(path = "getStudentById")
    public UserAttr getStudentInfo(@RequestBody UserAttr userinfo) {
        return userAttrService.getStudentInfo(userinfo);
    }

    @PostMapping(path = "updateOfTeacher")
    public int updateOfTeacher(@RequestBody UserAttr userinfo) {
        return userAttrService.updateOfTeacher(userinfo);
    }

    @PostMapping(path = "updateOfParent")
    public int updateOfParent(@RequestBody UserAttr userinfo) {
        return userAttrService.updateOfParent(userinfo);
    }

    /**
     * ????????????
     */
    @PostMapping(path = "getMessage")
    public Message getMessage(@RequestBody UserAttr user) {
//        System.out.println(user.getId());
        return messageService.getMessage(user.getId());
    }


    /*
* ???????????????????????????
* */
    @PostMapping(path = "getErrors")
    public List<Question> getErrors(@RequestBody UserAttr user) {
        return questionService.getErrors(user);
    }

    /*
* ???????????????????????????
* */
    @PostMapping(path = "getReport")
    public List<QuestionReport> selectReport(@RequestBody UserAttr u) {
        return questionService.selectReport(u);
    }

    @PostMapping(path = "getSignleReport")
    public QuestionReport selectReport(@RequestBody QuestionReport report) {
        return questionService.selectReport(report);
    }

    /**
     * ??????????????????????????????
     */
    @PostMapping(path = "getStudentsByParent")
    public List<UserAttr> getStudents(@RequestBody UserAttr user) {
        return parentService.getStudents(user);
    }

    /**
     * ??????????????????????????????
     */
    @PostMapping(path = "getPushments")
    public List<Pushment> getPushments(@RequestBody UserAttr u) {
        return pushmentService.getPushments(u);
    }


    /**
     * ??????????????????????????????
     */
    @PostMapping(path = "getStudentPushments")
    public List<Pushment> getStudentPushments(@RequestBody String d) {

        return pushmentService.getPushments(JSONObject.parseObject(d));
    }


    @PostMapping(path = "removeStudentPushment")
    public String removeStudentPushment(@RequestBody String d) {
        return pushmentService.removeStudentPushment(JSONObject.parseObject(d));
    }

    /**
     * ???????????????????????????
     */
    @PostMapping(path = "createPushments")
    public String createPushments(@RequestBody UserAttr u) throws IOException {
        return pushmentService.createPushments(u);
    }

    @PostMapping(path = "insertPushment")
    public String insertPushment(@RequestBody String d) {
        return pushmentService.insertPushment(d).toJSONString();
    }

    @PostMapping(path = "insertPushment2Students")
    public void insertPushment2Students(@RequestBody String d) {
        pushmentService.insertPushment2Students(d);
    }

    /**
     * ??????????????????????????????
     */
    @PostMapping(path = "setPushmantValid")
    public int setPushmantValid() {
        return pushmentService.setPushmentValid();
    }

    @PostMapping(path = "publicAssignment")
    public void publicAssignment(@RequestBody Assignment asm) {
        teacherService.publicAssignment(asm);
    }


    @PostMapping(path = "getAssignment")
    public List<Assignment> getAssignment(@RequestBody UserAttr user) {
        return teacherService.getAssignment(user);
    }

    /**
     * ????????????????????????????????????
     */
    @PostMapping(path = "getStudents")
    public List<UserAttr> getStudentsByTeacher(@RequestBody UserAttr user) {
        return teacherService.getStudents(user);
    }

    /**
     * ???????????????????????????????????????????????????(?????????+??????)
     */
    @PostMapping(path = "getPlan")
    public StudentPlan getPlan(@RequestBody UserAttr u) {
        return studentPlanService.selectplans(u);
    }


    @PostMapping(path = "cancleTask")
    public String cancleTask(@RequestBody UserAttr u) {
        return studentPlanService.cancleTask(u);
    }

    /**
     * ?????????????????????????????????(?????????+??????)
     */
    @PostMapping(path = "updatePlan")
    public int updatePlan(@RequestBody StudentPlan plan) {
        return studentPlanService.savePlan(plan);
    }


    /**
     * ?????????????????????????????????(?????????+??????)
     */
    @PostMapping(path = "saveStudentPlan")
    public String saveStudentPlan(@RequestBody StudentPlan plan) {
        return studentPlanService.saveStudentPlan(plan);
    }

    @PostMapping(path = "getUnhandleHomework")
    public List<List<QuestionReport>> getUnhandleHomework(@RequestBody String studentIds) {
        return assignmentService.getUnhandleHomework(studentIds);
    }

    /**
     * ?????????????????????????????????????????????????????????
     */
    @PostMapping(path = "getStudentSubmit")
    public List<StudentSubmit> getStudentSubmit(@RequestBody String d) {
        JSONObject data = JSONObject.parseObject(d);
        String studentId = data.getString("studentId");
        String hwId = data.getString("hwId");
        List<StudentSubmit> select = assignmentService.select(studentId, hwId);
        return select;
    }

    /**
     * ???????????????????????????????????????
     */
    @PostMapping(path = "submitQueston")
    public boolean submitQueston(@RequestBody String data) {
        JSONObject po = JSONObject.parseObject(data);
        assignmentService.submitQueston(po);
        return true;
    }

    /**
     * ??????????????? ??????????????? ??????
     * <p>
     * data:json?????????,{userId,question}
     * userId:str????????????????????????ID
     * question:{id,url,nd,cxd,zhd,zsd,answer}
     * <p>
     * id:str,?????????id
     * url:str,?????????url
     * nd:float,??????
     * cxd:float,?????????
     * zhd:float,?????????
     * zsd:str,??????????????????
     * answer:str,??????
     */
    @PostMapping(path = "uploadQuestionNode")
    public String uploadQuestionNode(@RequestBody String data) throws IOException {
        return questionService.uploadQuestionNode(data);
    }

    @PostMapping(path = "updateZhangJieNode")
    public String updateZhangJieNode(@RequestBody String data) throws IOException {
        return questionService.updateZhangJieNode(data);
    }

    /**
     * ??????????????????????????????????????????
     */
    @PostMapping(path = "getErrorAnswer")
    public StudentSubmit getErrorAnswer(@RequestBody String data) {
        JSONObject po = JSONObject.parseObject(data);
        return questionService.getErrorAnswer(po);
    }

    @PostMapping(path = "genQuestion")
    public String genQuestion(@RequestBody String param) throws Exception {
        JSONObject po = JSONObject.parseObject(param);
        String[] urls = po.getJSONArray("urls").toArray(new String[]{});

        String response = null;
        if (po.getInteger("type") == 0) {
            response = questionService.genQuestion2Html(urls);
        } else if (po.getInteger("type") == 0) {

        }
        po.put("response", response);
        po.remove("urls");

        return po.toJSONString();
    }

    /**
     * ????????????????????????uid???data??????
     */
    @PostMapping(path = "submitAudio")
    public boolean submitAudio(@RequestBody String data) {
        JSONObject po = JSONObject.parseObject(data);
        assignmentService.submitAudio(po);
        return true;
    }

    /**
     * ????????????????????????????????????
     */
    @PostMapping(path = "subReport")
    public boolean subReport(@RequestBody QuestionReport report) {
        return assignmentService.submitReport(report);
    }

    @PostMapping(path = "submitZizhuReport")
    public String submitZizhuReport(@RequestBody QuestionReport report) {
        return assignmentService.submitZizhuReport(report);
    }

    @PostMapping(path = "submitZizhuReport1")
    public String submitZizhuReport1(@RequestBody QuestionReport report) throws IOException {
        return assignmentService.submitZizhuReport1(report);
    }

    @PostMapping(path = "getStudentAttr")
    public UserAttr getStudentAttr(@RequestBody String d) {
        JSONObject data = JSONObject.parseObject(d);
        String stuId = data.getString("studentId");
        return assignmentService.getStudentAttr(stuId);
    }

    @PostMapping(path = "unbind")
    public String unbind(@RequestBody UserAttr u) {
        return userAttrService.unbind(u);
    }

    @PostMapping(path = "getStudentAnalysis")
    public String getStudentAnalysis(@RequestBody String userId) {
        return studentPlanService.getStudentAnalysis(userId.trim());
    }


    @PostMapping(path = "getMessageList")
    public String getMessageList(@RequestBody String messageRequest) {

        return chatService.getMessageList(JSONObject.parseObject(messageRequest));
    }


    @PostMapping(path = "getExtraDataByToken")
    public String getExtraDataByToken(@RequestBody String token) {

        return chatService.getExtraDataByToken(token);
    }

    @PostMapping("/sendMessage")
    public String sendMessage(@RequestBody String payload) throws ClientException {
        JSONObject data = JSON.parseObject(payload);
        String phoneNumber = data.getString("phoneNumber");
        String templateCode = data.getString("templateCode");
        String params = data.getString("templateParam");
        String signName = data.getString("signName");
        return smsService.sendMessage(phoneNumber, templateCode, params, signName);
    }


    @PostMapping("/wxTongYiXiaDan")
    public String wxTongYiXiaDan(@RequestBody String d) throws JDOMException, NoSuchAlgorithmException, IOException {
        return wxService.wxTongYiXiaDan(JSON.parseObject(d));
    }


    @PostMapping("/getUserAccountActivateInfo")
    public String getUserAccountActivateInfo(@RequestBody PayMemberShip p) {
        return wxService.getUserAccountActivateInfo(p);
    }

    @PostMapping("/insertPaymentOrder")
    public String insertPaymentOrder(@RequestBody PayMemberShip p) {
        return wxService.insertPaymentOrder(p);
    }


    @PostMapping("/wxLogin")
    public UserAttr wxLogin(@RequestBody String code) {
        JSONObject accessToken = wxService.getAccessToken(code);

        if (accessToken == null) {
            return null;

        } else {
            UserAttr user = userAttrService.checkloginWithOpenId(accessToken.getString("openid"));

            if (user == null) {

                JSONObject wxUserInfo = wxService.getWXUserInfo(accessToken.getString("access_token"), accessToken.getString("openid"));
                user = userAttrService.registerWithOpenId(wxUserInfo);
            }
            return user;
        }

    }

    @PostMapping("/uploadGraph")
    public String uploadGraph(@RequestBody String data) throws IOException {

        return questionService.uploadGraph(data);
    }

    @PostMapping("/queryGraph")
    public String queryGraph(@RequestBody String chapterId) throws IOException {
        return questionService.queryGraph(chapterId);
    }


    @PostMapping("/saveRole")
    public String saveRole(@RequestBody String data) throws IOException {
        return roleService.saveRole(data);
    }


    @PostMapping("/queryRole")
    public String queryRole() throws IOException {
        return roleService.queryRole();
    }


    @PostMapping("/loadUserPage")
    public String loadUserPage(@RequestBody String userId) throws IOException {
        return autoPlanService.loadUserPage(userId);
    }

    @PostMapping("/addUserCourse")
    public String addUserCourse(@RequestBody String d) throws IOException {
        return autoPlanService.addUserCourse(d);
    }


    @PostMapping("/loadStudyPlanPage")
    public String loadStudyPlanPage(@RequestBody String d) throws IOException {
        return autoPlanService.loadStudyPlanPage(d);
    }


    @PostMapping("/changePlan")
    public String changePlan(@RequestBody String d) throws IOException {
        /**
         * userId
         * courseId
         * zhangjieId
         * zsdId
         * */
        return autoPlanService.changePlan(d);
    }

    @PostMapping("/finishOneChapter")
    public String finishOneChapter(@RequestBody String d) throws IOException {
        /**
         * userId
         * courseId
         * zhangjieId
         * zsdId
         * */
        return autoPlanService.finishOneChapter(d);
    }

    @Autowired
    private AutoPlanService autoPlanService;
    @Autowired
    private WxService wxService;
    @Autowired
    private ChatService chatService;
    @Autowired
    private UserAttrService userAttrService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private ParentService parentService;
    @Autowired
    private PushmentService pushmentService;
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private StudentPlanService studentPlanService;
    @Autowired
    private AssignmentService assignmentService;
    @Autowired
    private SMSService smsService;

    @Autowired
    private RoleService roleService;

    public static void main(String[] args) {
        System.out.println(Charset.forName("gbk"));
    }
}
