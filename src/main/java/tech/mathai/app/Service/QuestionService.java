package tech.mathai.app.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Service;
import tech.mathai.app.Entity.*;
import tech.mathai.app.Mapper.ChapterMapper;
import tech.mathai.app.Mapper.QuestionReportMapper;
import tech.mathai.app.utils.URLUtil;

import java.io.*;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.*;


/**
 * Created by Administrator on 2020/6/16.
 */
@Service
public class QuestionService {

    private Map<String,JSONObject> jsonFlat2Map(JSONArray p){
        Map<String,JSONObject> m=new HashMap<>();
        Queue<JSONArray> queue= new LinkedList<>();

        queue.add(p);

        while (queue.size()>0){
            p=queue.poll();
            for(int i=0;i<p.size();i++){
                JSONObject node = p.getJSONObject(i);
                JSONArray children=node.getJSONArray("children");
                String id = node.getString("id");
                m.put(id,node);

                if(children!=null)
                    queue.add(children);
            }
        }
        return m;
    }
    private JSONArray loadChapterFromDisk() throws IOException {
        String datapath =env.getProperty("json_chapter");;

        FileInputStream fs=new FileInputStream(datapath);
        ByteArrayOutputStream out=new ByteArrayOutputStream();
        byte[] buf=new byte[512];
        int offset=0;
        int len=0;
        while ((len=fs.read(buf,offset,512))!=-1){
            out.write(buf,0,len);
        }
        fs.close();
        String ret=new String(out.toByteArray(),"utf-8");
        JSONArray p=JSONObject.parseArray(ret);
        return p;
    }

    private void saveChapterToDisk(JSONArray p) throws IOException {
        String datapath = env.getProperty("json_chapter");
        FileOutputStream fs=new FileOutputStream(datapath);

        fs.write(p.toJSONString().getBytes("utf-8"));
        fs.close();
    }
    public String getChapter() throws IOException {

        String datapath = env.getProperty("json_chapter");
        String VERSION=getFileVersion();
        FileInputStream fs=new FileInputStream(datapath);

        ByteArrayOutputStream out=new ByteArrayOutputStream();
        byte[] buf=new byte[512];
        int offset=0;
        int len=0;
        while ((len=fs.read(buf,offset,512))!=-1){
            out.write(buf,0,len);
        }
        fs.close();
        String ret=new String(out.toByteArray(),"utf-8");

        JSONArray p=JSONObject.parseArray(ret);
        p.getJSONObject(0).put("version",VERSION);
        return p.toJSONString();
    }

    public String checkChapter(String param){
        JSONObject p=JSONObject.parseObject(param);


        String current_version = getFileVersion();
        String remove_version=p.getString("version");

        boolean equals = current_version.equals(remove_version);

        p.put("hasNewVersion",!equals);
        p.remove("version");


        return p.toJSONString();

    }
    private  String getFileVersion(){

        File file = new File(env.getProperty("json_chapter"));
        return ""+file.lastModified();
    }
    //ids是所有知识点的id,但最后一个是用户的id,也就是用户选择了这个题目！！！
    public String[] smartChooseQuestion(String[] ids) throws IOException {
        String userid=ids[ids.length-1];

        JSONObject param=new JSONObject();
        param.put("studentId",userid);
        param.put("zsdIds",ids);
        JSONObject postresult = URLUtil.Post(env.getProperty("python_smartChoose"), param);

        String[] ret=new String[0];
        if(postresult.getBoolean("success")){
            ret=postresult.getJSONArray("questionIds").toArray(new String[0]);
        }
        return ret;
    }
    /**
     * 根据studentId获得全部报告
     * */
    public List<QuestionReport> selectReport(UserAttr u){
        List<QuestionReport> reports=questionReportMapper.selectReport(u);
        for(int i=0;i<reports.size();i++){
            QuestionReport  r=reports.get(i);
            String[] split = r.getHwId().split("_");
            r.setQuestionType(split[2]);

        }
        return reports;
    }
    /**
     * 根据hwId,studentId获得报告
     * */
    public QuestionReport selectReport(QuestionReport report) {

        List<QuestionReport> questionReports = questionReportMapper.selectReportByHwId(report);
        if(questionReports.size()>0)
            return questionReports.get(0);
        return null;
    }
    //选择所有错题
    public List<Question> getErrors(UserAttr user){
        List<Question> ls=questionReportMapper.selectQuestion(user);
        return ls;
    }


    public StudentSubmit getErrorAnswer(JSONObject po) {
        String studentId=po.getString("studentId");
        String questionId=po.getString("questionId");
        List<StudentSubmit> errorAnswer = questionReportMapper.getErrorAnswer(studentId, questionId);
        if(errorAnswer.size()>0){
            StudentSubmit studentSubmit = errorAnswer.get(0);
            if(studentSubmit.getImgs()==null)
                studentSubmit.setImgs("");
            if(studentSubmit.getAudios()==null)
                studentSubmit.setAudios("");
            return studentSubmit;
        }
        else return null;
    }

    private String[] findHeadAndBody(String filename,String encodeType) throws IOException {
        /**
         * 把filename 以encodeType的方式打开，找到 head=<head>....</head>
         *
         * body=<body>..</body>之间的内容，返回
         * @param filename：输入文件的 【全路径】
         * @param encodeType：filename文件的编码
         *
         * @return String[0]=<head>....</head>|包括标签
         *          String[1]=<body>..</body>|不包括标签
         * */
        String[] ret=new String[2];

        File f=new File(filename);
        if(f.exists()){
            FileInputStream fin=new FileInputStream(filename);
            byte[] bs=new byte[fin.available()];
            fin.read(bs);
            String content=new String(bs,encodeType);

            //查找 head
            String endTag="</head>";
            int head_end_index = content.indexOf(endTag) + endTag.length();
            ret[0]=content.substring(0,head_end_index);


            String bodyTag="<body";
            int body_begin_index = content.indexOf(bodyTag) + bodyTag.length();

            String bodyEndTag=">";
            body_begin_index=content.indexOf(bodyEndTag,body_begin_index)+1;

            ret[1]=content.substring(body_begin_index,content.length()-"</body>".length());
            fin.close();
        }
        return ret;
    }
    //把传入的urls转成一个文件，返回文件名称

    private String generateQuestion(String srcPath,String targetPath,String[] urls) throws Exception {
        /**
         * 把srcPath 下面的urls 合并成文一个文件，合并的文件名=随即生成+“。html”,保存到targetPath下面

            输入的文件urls不可以为空

         * @param srcPath:原 的根目录部分
         * @param targetPath:目标 的根目录部分
         * @param urls:原  的文件部分,eg /2029/a.html
         *
         * @return 生成的文件【全路径】
         * */
        String encode="GBK";

        if(urls.length==0) throw new Exception("怎么可以不输入源文件呢？");


        String targetFile =targetPath+File.separator+ UUID.randomUUID().toString().replace("-", "")+".htm";
        FileOutputStream ous=new FileOutputStream(targetFile);




        for(int i=0;i<urls.length;i++){
            String url=urls[i];

            //find head,body

            String[] headAndBody = findHeadAndBody(srcPath + url,encode);

            String head=headAndBody[0],body=headAndBody[1];
            if(i==0){
                ous.write(head.getBytes(encode));
                ous.write("<body>".getBytes(encode));

            }
            ous.write(body.getBytes(encode));
            ous.write("<br /><br /><br /><br /><br /><br />".getBytes(encode));
        }

        ous.write("</body>".getBytes(encode));
        ous.close();

        return targetFile;
    }

    public String genQuestion2Html(String[] urls) throws Exception {
        String generate_path = env.getProperty("generate_path");
        String src_path = env.getProperty("spring.resources.static-locations").substring(5);

        String outfile = generateQuestion(src_path, generate_path, urls);
        String target_url=outfile.replace(src_path,"");


        return target_url;
    }

    public String updateNode(String data) throws IOException {
        JSONObject questionJSON = JSONObject.parseObject(data);


        //update database
        JSONArray chapter = loadChapterFromDisk();
        Map<String, JSONObject> m = jsonFlat2Map(chapter);
        if(m.containsKey(questionJSON.getString("id"))){
            JSONObject p = m.get(questionJSON.getString("id"));
            p.put("name",questionJSON.getString("name"));

            if(questionJSON.containsKey("remark")){
                p.put("remark",questionJSON.getString("remark"));
            }
            saveChapterToDisk(chapter);

            chapterMapper.updateQuesionName(p.getString("id"), p.getString("name"));
            chapterMapper.updateChapterName(p.getString("id"), p.getString("name"));

        }

        String VERSION=getFileVersion();
        JSONObject returnVal=new JSONObject();
        returnVal.put("version",VERSION);
        return returnVal.toJSONString();
    }
    public String uploadQuestionNode(String data) throws IOException {
        /**
         * @param data:   (userId,question)
         *            userId-老师的ID，
         *            question-节点的json对象
         * */
        JSONObject message = JSONObject.parseObject(data);
        String teacherId=message.getString("userId");
        JSONObject questionJSON=message.getJSONObject("question");


        Question question = new Question();
        question.load(questionJSON);

        if(questionJSON.containsKey("uuid")){
            question.setUid(questionJSON.getString("uuid"));
        }
        //update database
        JSONArray chapter = loadChapterFromDisk();
        Map<String, JSONObject> m = jsonFlat2Map(chapter);
        if(m.containsKey(question.getUid())){
            JSONObject p = m.get(question.getUid());
            question.save(p);
            saveChapterToDisk(chapter);

            int update=chapterMapper.update(question);

        }

        String VERSION=getFileVersion();
        JSONObject returnVal=new JSONObject();
        returnVal.put("version",VERSION);




        return returnVal.toJSONString();
    }

    public String addNewChapterNode(String data) throws IOException {
        JSONObject message = JSONObject.parseObject(data);
        //update database
        JSONArray chapter = loadChapterFromDisk();
        Map<String, JSONObject> m = jsonFlat2Map(chapter);

        String parentid=message.getString("parent");
        if(m.containsKey(parentid)){
            JSONObject p=m.get(parentid);
            p.getJSONArray("children").add(message);
            saveChapterToDisk(chapter);

            if(message.containsKey("url")){
                Question q=new Question();
                q.load(message);
                q.setTitle(message.getString("name"));
                q.setCatalog(p.getString("id"));
                chapterMapper.insertQuestion(q);
                //保存文件
                String bpath = env.getProperty("spring.resources.static-locations").replace("file:", "");


                String folder = q.getUrl().substring(0, q.getUrl().lastIndexOf("/"));

                File file = new File(bpath+folder);
                if(file.exists()==false){
                    file.mkdir();
                }
                FileOutputStream outputStream=new FileOutputStream(bpath+q.getUrl());
                outputStream.write("请输入新的内容".getBytes());
                outputStream.close();
            }else {
                ChapterDto ch=new ChapterDto();
                ch.load(message);
                ch.setParent(p.getString("id"));
                chapterMapper.insertChapter(ch);
            }
        }
        String VERSION=getFileVersion();
        JSONObject returnVal=new JSONObject();
        returnVal.put("version",VERSION);
        return returnVal.toJSONString();
    }
    public String updateZhangJieNode(String data) throws IOException {
        JSONObject message = JSONObject.parseObject(data);

        String teacherId=message.getString("userId");
        JSONObject nodeJSON=message.getJSONObject("node");


        //update database
        JSONArray chapter = loadChapterFromDisk();
        Map<String, JSONObject> m = jsonFlat2Map(chapter);

        String nodeId=nodeJSON.getString("id");
        if(m.containsKey(nodeId)){
            JSONObject p=m.get(nodeId);
            p.put("remark",nodeJSON.getString("remark"));

            saveChapterToDisk(chapter);
            Question question = new Question();
            question.setUid(nodeJSON.getString("id"));
            question.setRemark(nodeJSON.getString("remark"));
            int update=chapterMapper.updateZhangjie(question);

        }

        String VERSION=getFileVersion();
        JSONObject returnVal=new JSONObject();
        returnVal.put("version",VERSION);
        return returnVal.toJSONString();
    }

    public int removeChapterNode(String chId) throws IOException {

        //update database
        JSONArray chapter = loadChapterFromDisk();
        Map<String, JSONObject> m = jsonFlat2Map(chapter);
        if(m.containsKey(chId)){
            JSONObject p=m.get(chId);
            if(p==null)return 0;
            p=m.get(p.getString("parent"));
            if(p==null)return 0;

            JSONArray children = p.getJSONArray("children");
            for(int i=0;i<children.size();i++){
                JSONObject o = children.getJSONObject(i);
                String id = o.getString("id");
                if(id.equals(chId)){
                    children.remove(o);
                    deleteFromDataBase(m.get(chId));
                    saveChapterToDisk(chapter);
                    return 1;
                }
            }
        }
        return 0;
    }

    private void deleteFromDataBase(JSONObject p){
        //删除 一道题目
        if(p.containsKey("children")==false){
            //delete question by id
            chapterMapper.removeQuestionById(p.getString("id"));
            return;
        }
        //要根据 id删除的 chapter 表中的记录
        List<String> removeChapterId=new ArrayList<>();
        //要根据catalog删除的 question表中的记录
        List<String> zhishidianId=new ArrayList<>();


        Stack<JSONObject> stack=new Stack<>();
        stack.push(p);

        while (stack.isEmpty()==false){
            p=stack.pop();
            removeChapterId.add(p.getString("id"));

            //是zsd
            if(p.getBoolean("isQuestion")){
                zhishidianId.add(p.getString("id"));
                continue;
            }
            JSONArray children=p.getJSONArray("children");
            for(int i=0;i<children.size();i++){
                stack.push(children.getJSONObject(i));
            }
        }

        //delet chapter by id
        for(String chapterId:removeChapterId){
            chapterMapper.removeChapterById(chapterId);
        }
        //delete question by catalog
        for(String catalogId:zhishidianId){
            chapterMapper.removeQuestionByCatalog(catalogId);
        }
    }
    public String getHtmlFile(String s) throws IOException {

        String filename=env.getProperty("spring.resources.static-locations").replace("file:","")+s;
        File file=new File(filename);
        if(file.exists()==false) return "请输入";

        FileInputStream ins=new FileInputStream(env.getProperty("spring.resources.static-locations").replace("file:","")+s);
        byte[] b=new byte[ins.available()];
        ins.read(b);


        String gbk = new String(b, "gbk");
        if(!gbk.contains("charset=gb2312")){
            gbk=new String(b);
        }
        String tag1="<p";
        String tag2="</body>";
        int ii1=gbk.indexOf(tag1);
        int ii2=gbk.indexOf(tag2);

        if(ii1!=-1&&ii1<ii2)
            return gbk.substring(ii1,ii2);
        else return gbk;
    }
    private void saveFile(String uploadpath,String html) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(uploadpath);
        String s1="<!DOCTYPE html><html><head><meta http-equiv=Content-Type content=\"text/html; charset=utf-8\"><meta name=\"viewport\" content=\"width=device width, initial-scale=1.0\"><title>Document</title><link href=\"../css/katex.min.css\" rel=\"stylesheet\"></link></head><body>";
        String s2="</body></html>";

        fileOutputStream.write(s1.getBytes());
        fileOutputStream.write(html.getBytes());
        fileOutputStream.write(s2.getBytes());
        fileOutputStream.close();
    }


    public String uploadNewPage(String d) throws IOException {
        JSONObject jsonObject=JSONObject.parseObject(d);
        String html = jsonObject.getString("html");
        String gongxue = jsonObject.getString("gongxue");
        String jiexi = jsonObject.getString("jiexi");

        String context=jsonObject.getString("context");

        String uploadpath = env.getProperty("spring.resources.static-locations").replace("file:", "");

        if(context.equals("#")){

        }else {
            uploadpath=uploadpath+context;
        }

        saveFile(uploadpath,html);

        String ext=uploadpath.substring(uploadpath.lastIndexOf("."));

        saveFile(uploadpath.replace(ext, "") + "步骤" + ext, gongxue);
        saveFile(uploadpath.replace(ext,"")+"解析"+ext,jiexi);

        JSONObject returnVal=new JSONObject();
        returnVal.put("success",true);
        return returnVal.toJSONString();


    }

    public String uploadGongLuePage(String d) throws IOException {
        JSONObject jsonObject=JSONObject.parseObject(d);
        String html = jsonObject.getString("html");

        String context=jsonObject.getString("context");

        JSONObject node=jsonObject.getJSONObject("node");
        if(node.containsKey("uuid")){
            node.put("id",node.getString("uuid"));
        }

        updateNode(node.toJSONString());

        String uploadpath = env.getProperty("spring.resources.static-locations").replace("file:", "");




        String filedir = context.substring(0, context.indexOf("/",1));
        File dir=new File(uploadpath+filedir);
        if(dir.exists()==false){
            dir.mkdir();
        }

        uploadpath=uploadpath+context;
        saveFile(uploadpath, html);




        JSONObject returnVal=new JSONObject();
        returnVal.put("success",true);
        return returnVal.toJSONString();


    }

    public String uploadGraph(String data) throws IOException {
        JSONObject p=JSONObject.parseObject(data);
        String id = p.getString("id");
        String graph=p.getJSONObject("graph").toJSONString();

        FileOutputStream fileOutputStream = new FileOutputStream(env.getProperty("graph_path")+File.separator+id+".json");
        fileOutputStream.write(graph.getBytes());
        fileOutputStream.close();

        JSONObject returnVal=new JSONObject();
        returnVal.put("success", true);
        return returnVal.toJSONString();
    }
    public String queryGraph(String id) throws IOException {
        String filename=env.getProperty("graph_path")+File.separator+id+".json";
        File file = new File(filename);
        JSONObject returnVal=new JSONObject();
        returnVal.put("success", false);
        if(file.exists()){
            returnVal.put("success", true);

            FileInputStream ins=new FileInputStream(filename);
            byte[] b=new byte[ins.available()];
            ins.read(b);
            String gbk = new String(b);
            JSONObject j=JSONObject.parseObject(gbk);
            returnVal.put("graph",j);
        }
        return returnVal.toJSONString();
    }

    @Autowired
    private ChapterMapper chapterMapper;
    @Autowired
    private QuestionReportMapper questionReportMapper;
    @Autowired
    private Environment env;


    public static void main(String[] args) throws Exception {

        QuestionService questionService = new QuestionService();

        String[] urls={
//                "/2020-09-17/勾股、实数-212.htm",
//                "/2020-09-17/概率、相似-87 .htm",
//                "/2020-09-17/二元一次、数据-126.htm",
//                "/2020-09-17/反比例函数-92.htm",
//                "/2020-09-17/坐标系、一次-13335.htm",
                "/2020-09-17/勾股、实数-120.htm"
        };
//        String srcPath="/home/mathai/projects/db/static";
//        String targetPath="/home/mathai/projects/db/static/generate";
////
//        System.out.println(questionService.generateQuestion(srcPath,targetPath,urls));

//        JSONArray objects = questionService.loadChapterFromDisk("/home/mathai/myproj/edu-back/data/static/chapterdb/st.json");
//        Map<String, JSONObject> stringJSONObjectMap = questionService.jsonFlat2Map(objects);
//        System.out.println(stringJSONObjectMap.get("d6a5a348f63211eaacd313503f47a969"));

    }



}
