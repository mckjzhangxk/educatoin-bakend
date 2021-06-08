package tech.mathai.app.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.core.env.Environment;
import tech.mathai.app.utils.DataUtils;
import tech.mathai.app.utils.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by mathai on 21-4-8.
 */
public class AutoPlanStatic {
    private static JSONArray CONST_JSON_CHAPTER;
    private static Map<String, JSONObject> CONST_CHAPTER_FLAT;

    static {
        try {
            CONST_JSON_CHAPTER = loadChapterFromDisk();
            CONST_CHAPTER_FLAT = jsonFlat2Map(CONST_JSON_CHAPTER);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static JSONArray loadChapterFromDisk() throws IOException {
        String datapath = "/home/mathai/projects/db/static/chapterdb/st.json";

        FileInputStream fs = new FileInputStream(datapath);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[512];
        int offset = 0;
        int len = 0;
        while ((len = fs.read(buf, offset, 512)) != -1) {
            out.write(buf, 0, len);
        }
        fs.close();
        String ret = new String(out.toByteArray(), "utf-8");
        JSONArray p = JSONObject.parseArray(ret);
        return p;
    }

    private static Map<String, JSONObject> jsonFlat2Map(JSONArray p) {
        Map<String, JSONObject> m = new HashMap<>();
        Queue<JSONArray> queue = new LinkedList<>();

        queue.add(p);

        while (queue.size() > 0) {
            p = queue.poll();
            for (int i = 0; i < p.size(); i++) {
                JSONObject node = p.getJSONObject(i);
                JSONArray children = node.getJSONArray("children");
                String id = node.getString("id");
                m.put(id, node);

                if (children != null)
                    queue.add(children);
            }
        }
        return m;
    }

    private static String USER_PLANS_PATH(String userid) {
        return "/home/mathai/zxk/abc/" + userid + File.separator + "plans";
    }

    private static String USER_PROFILE_PATH(String userid) {
        return "/home/mathai/zxk/abc/" + userid + "/" + userid + ".json";
    }

    private static String COURSE_GRAPH_PATH(String courdeId) {
        return "/home/mathai/projects/db/graph/" + courdeId + ".json";
    }


    private static JSONObject _defaultProfileJSON_(String courseId) {
        JSONObject result = new JSONObject();

        JSONArray courseList = new JSONArray();
        if (courseId != null)
            courseList.add(courseId);
        result.put("courseList", courseList);


        JSONObject errorTime = new JSONObject();
        JSONArray errors = new JSONArray();
        result.put("errorTime", errorTime);
        result.put("errors", errors);

        JSONObject plans = new JSONObject();
        result.put("plans", plans);

        plans.put(courseId, new JSONArray());


        return result;
    }

    private static JSONObject __readUserProfile__(String userId) throws IOException {
        return FileUtils.readJson(USER_PROFILE_PATH(userId));
    }

    private static void __saveUserProfile__(String userId, JSONObject profile) throws IOException {
        FileUtils.write(USER_PROFILE_PATH(userId), profile);
    }

    private static JSONObject __readUserCoursePlan__(String userId, String courseId) throws IOException {
        String target = USER_PLANS_PATH(userId) + File.separator + courseId + ".json";
        return FileUtils.readJson(target);
    }

    private static void __saveUserCoursePlan(String userId, String chapterId, JSONObject p) throws IOException {
        String target = USER_PLANS_PATH(userId) + File.separator + chapterId + ".json";
        FileUtils.write(target, p);
    }


    private static void __setSelectZsd__(JSONObject plan, String zsdId) {
        if(zsdId==null) return;
        plan.put("selectNode", zsdId);
    }

    private static void __setFinishZsd__(JSONObject plan, String zsdId) {
        if (plan == null || zsdId == null) return;
        JSONArray nodes = plan.getJSONArray("nodes");
        for (int i = 0; i < nodes.size(); i++) {
            JSONObject p = nodes.getJSONObject(i);
            if (p.getString("id").equals(zsdId)) {
                p.put("end", true);
            }
        }
        int learened = plan.getInteger("learned") + 1;
        plan.put("learned", learened);
        plan.put("start", true);
    }

    private static JSONObject _newPlanItem_(String userid, String chapterId, String zsdId) throws IOException {

        JSONObject result = new JSONObject();
        result.put("id", chapterId);
        result.put("start", false);
        result.put("end", false);
        result.put("learned", 0);


        JSONObject node_and_edges = FileUtils.readJson(COURSE_GRAPH_PATH(chapterId));
        if (node_and_edges == null) return null;

        result.put("total", node_and_edges.getJSONArray("nodes").size());

        result.put("nodes", node_and_edges.getJSONArray("nodes"));
        result.put("edges", node_and_edges.getJSONArray("edges"));

        __setSelectZsd__(result, zsdId);


        __saveUserCoursePlan(userid, chapterId, result);
        return result;
    }


    public static void addCourse(String userid, String courseId) throws IOException {
        JSONObject p = __readUserProfile__(userid);
        if (p == null) {
            p = _defaultProfileJSON_(courseId);
            FileUtils.makep(USER_PLANS_PATH(userid));
            __saveUserProfile__(userid, p);
        } else {
            JSONObject plans = p.getJSONObject("plans");
            if (plans.containsKey(courseId)) return;
            p.getJSONObject("plans").put(courseId, new JSONArray());
            p.getJSONArray("courseList").add(0, courseId);
        }
        __saveUserProfile__(userid, p);
    }


    public static void addErrorQuestion(String userid, String questionId) throws IOException {
        JSONObject p = __readUserProfile__(userid);
        if (p == null) {
            return;
        } else {
            JSONObject errorTime = p.getJSONObject("errorTime");
            JSONArray errors = p.getJSONArray("errors");
            if (errorTime.containsKey(questionId)) {
                int i = errors.indexOf(questionId);
                errors.remove(i);
            }
            errors.add(0, questionId);
            errorTime.put(questionId, DataUtils.getCurrentTime());

        }
        __saveUserProfile__(userid, p);
    }

    public static void addErrorQuestion(String userid, String[] questionIds) throws IOException {
        JSONObject p = __readUserProfile__(userid);
        if (p == null) {
            return;
        } else {
            JSONObject errorTime = p.getJSONObject("errorTime");
            JSONArray errors = p.getJSONArray("errors");
            for (String questionId : questionIds) {
                if (errorTime.containsKey(questionId)) {
                    int i = errors.indexOf(questionId);
                    errors.remove(i);
                }

                JSONObject question_json = CONST_CHAPTER_FLAT.get(questionId);
                if (question_json == null) continue;
                JSONObject zsd_json = CONST_CHAPTER_FLAT.get(question_json.getString("parent"));
                if (zsd_json == null) continue;
                JSONObject chapter_json = CONST_CHAPTER_FLAT.get(zsd_json.getString("parent"));
                if (chapter_json == null) continue;

                JSONObject plan = readUserCoursePlan(userid, chapter_json.getString("id"));
                if (plan == null) continue;
                if (!plan.containsKey("error")) {
                    plan.put("error", new JSONObject());
                }

                JSONObject error = plan.getJSONObject("error");

                JSONObject error_info = new JSONObject();
                error_info.put("time", DataUtils.getCurrentTime());
                error_info.put("zsd", zsd_json.getString("id"));

                error.put(questionId, error_info);

                errors.add(0, questionId);
                errorTime.put(questionId, DataUtils.getCurrentTime());

                __saveUserCoursePlan(userid, chapter_json.getString("id"), plan);
            }


        }
        __saveUserProfile__(userid, p);
    }

    public static JSONObject readUserProfile(String userId) throws IOException {
        JSONObject jsonObject = __readUserProfile__(userId);
        if (jsonObject == null)
            return _defaultProfileJSON_(null);
        return jsonObject;
    }

    public static JSONObject readUserCoursePlan(String userid, String chapterId) throws IOException {
        return __readUserCoursePlan__(userid, chapterId);
    }


    public static boolean pushPlan(JSONObject d) throws IOException {
        String userId = d.getString("userId");
        String courseId = d.getString("courseId");
        String zhangjieId = d.getString("zhangjieId");
        String zsdId = d.getString("zsdId");

        String prevZsdId = d.getString("prevZsdId");

        for (int i = 0; i < 1; i++)
            if (prevZsdId != null) {
                JSONObject prevZsdNode = CONST_CHAPTER_FLAT.get(prevZsdId);
                if (prevZsdNode == null)
                    break;
                JSONObject chapter = CONST_CHAPTER_FLAT.get(prevZsdNode.getString("parent"));
                if (chapter == null)
                    break;

                String prevChapteId = chapter.getString("id");
                JSONObject p = __readUserCoursePlan__(userId, prevChapteId);
                if (p == null) break;
                __setFinishZsd__(p, prevZsdId);
                __saveUserCoursePlan(userId, prevChapteId, p);


                if (prevChapteId.equals(zhangjieId) == false) {
                    JSONObject p1 = __readUserCoursePlan__(userId, zhangjieId);
                    if (p1 == null) break;
                    __setFinishZsd__(p1, prevZsdId);
                    __saveUserCoursePlan(userId, zhangjieId, p1);
                }

            }

        JSONObject profile = __readUserProfile__(userId);

        JSONArray plansOfCourse = profile.getJSONObject("plans").getJSONArray(courseId);

        if (plansOfCourse.size() == 0) {
            plansOfCourse.add(zhangjieId);
            JSONObject already = readUserCoursePlan(userId, zhangjieId);
            if(already==null){
                JSONObject jsonObject = _newPlanItem_(userId, zhangjieId, zsdId);
                if (jsonObject == null) return false;
            }

        } else {
            int find = -1;
            for (int i = 0; i < plansOfCourse.size(); i++) {
                String __chapterId__ = plansOfCourse.getString(i);
                if (__chapterId__.equals(zhangjieId)) {
                    find = i;
                    break;
                }
            }

            if (find >= 0) {
                plansOfCourse.remove(find);
                plansOfCourse.add(zhangjieId);
                JSONObject p = __readUserCoursePlan__(userId, zhangjieId);
                __setSelectZsd__(p, zsdId);
                __saveUserCoursePlan(userId, zhangjieId, p);
            } else {
                plansOfCourse.add(zhangjieId);
                JSONObject already = readUserCoursePlan(userId, zhangjieId);
                if(already==null){
                    JSONObject jsonObject = _newPlanItem_(userId, zhangjieId, zsdId);
                    if (jsonObject == null) return false;
                }

            }
        }

        __saveUserProfile__(userId, profile);
        return true;

    }


    public static String popPlan(JSONObject d) throws IOException {
        //结束当前课程的学习计划，返回 之前的计划（学习的章节ID）
        String userId = d.getString("userId");
        String courseId = d.getString("courseId");


        JSONObject profile = readUserProfile(userId);
        JSONArray plan = profile.getJSONObject("plans").getJSONArray(courseId);
        if (plan.size() > 0) {
            plan.remove(plan.size() - 1);
        }
        __saveUserProfile__(userId, profile);
        if (plan.size() > 0)
            return plan.getString(plan.size() - 1);
        else return null;
    }
    public static void finishChapter(JSONObject d) throws IOException {
        //结束一个章节
        String userId = d.getString("userId");
        String chapterId = d.getString("zhangjieId");
        String zsdId = d.getString("zsdId");


        JSONObject chapterGraph = readUserCoursePlan(userId, chapterId);
        __setFinishZsd__(chapterGraph,zsdId);
        chapterGraph.put("end",true);
        chapterGraph.put("learned",chapterGraph.getIntValue("total"));
        __saveUserCoursePlan(userId,chapterId,chapterGraph);


        String parent = CONST_CHAPTER_FLAT.get(zsdId).getString("parent");
        if(!parent.equals(chapterId)){
            chapterGraph = readUserCoursePlan(userId, parent);
            __setFinishZsd__(chapterGraph,zsdId);
            __saveUserCoursePlan(userId,parent,chapterGraph);
        }
    }


    public static String  chooseNextChapter(JSONObject d) throws IOException {
        //选择下一个没有结束的 章节
        String userId = d.getString("userId");
        String chapterId = d.getString("zhangjieId");

        JSONObject chapterGraph = readUserCoursePlan(userId, chapterId);
        JSONArray nodes = chapterGraph.getJSONArray("nodes");

        List<JSONObject> startPoints=new ArrayList<>();

        for(int i=0;i<nodes.size();i++){
            String  cp= nodes.getJSONObject(i).getString("parent");
            if(!cp.equals(chapterId)){
                JSONObject _chapterGraph=readUserCoursePlan(userId, cp);
                if(_chapterGraph!=null &&!_chapterGraph.getBoolean("end")){
                    return cp;
                }else if(_chapterGraph!=null){
                    startPoints.add(_chapterGraph);
                }
            }
        }

        for(JSONObject start:startPoints){
            JSONObject param = new JSONObject();
            param.put("userId",userId);
            param.put("zhangjieId",start.getString("id"));
            String s = chooseNextChapter(param);
            if(s!=null) return s;
        }
        return null;
    }
    public static void main(String[] args) throws IOException {
//        _createDefault("196564", "22");

//        addCourse("zxk", "18.06");
//        addCourse("zxk", "cs15213");


//        addErrorQuestion("zxk","q01");
//        addErrorQuestion("zxk","q01");
//
//        addErrorQuestion("zxk", new String[]{"q1", "q2", "q3"});
//        addErrorQuestion("zxk", new String[]{"q3", "q2", "q1", "9c94bcfe137c11eb8de5e34128807c25", "268a0ff0144411eb8932799142100f31"});
//        _newPlanItem_("zxk", "9c943518137c11eb8de5e34128807c25","zsd");

//        JSONObject d = new JSONObject();
//        d.put("userId", "zxk");
//        d.put("courseId", "cs15213");
//        d.put("zhangjieId", "43f624c02dee11eb99b887cbc3aefd92");
//        d.put("zsdId", "hahaha");
//        pushPlan(d);

//        d.put("userId", "zxk");
//        d.put("courseId", "cs15213");
//        d.put("zhangjieId", "9c943518137c11eb8de5e34128807c25");
//        d.put("zsdId", "我的");
//
//
//        d.put("userId", "zxk");
//        d.put("courseId", "cs15213");
//        d.put("zhangjieId", "sss");
//        d.put("zsdId", "final result");
//
//        pushPlan(d);

        JSONObject d = new JSONObject();
        d.put("userId", "zxk");
        d.put("courseId", "cs15213");
        d.put("zhangjieId", "9c943518137c11eb8de5e34128807c25");
        d.put("zsdId","43f8c8562dee11eb99b887cbc3aefd92");
        finishChapter(d);

//        String s = popPlan(d);
//        System.out.println(s);//9c943518137c11eb8de5e34128807c25

//        System.out.println(chooseNextChapter(d));
    }
}
