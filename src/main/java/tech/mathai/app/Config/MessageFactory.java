package tech.mathai.app.Config;


import com.alibaba.fastjson.JSONObject;

public class MessageFactory {
    public enum  MediaType{TEXT,IMAGE,URL,AUDIO};

    public class MessageType{
        public static final  int SEND=0;
        public static final  int RECV=1;
        public static final  int TIME=2;
    };

    public static JSONObject createTimeMessage(String msg)  {
        JSONObject p = new JSONObject();
        p.put("message",msg);
        p.put("type",MessageType.TIME);
        return p;
    }
}
