package tech.mathai.app.Service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import tech.mathai.app.Config.MessageFactory;
import tech.mathai.app.Entity.ChatMessage;
import tech.mathai.app.Entity.ChatRoom;
import tech.mathai.app.Mapper.ChatRoomMapper;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by mathai on 20-10-22.
 */
@Service
public class ChatService {

    @Autowired
    public void setEnvironment(Environment xx) {
        env = xx;
    }

    public void recordChatMessage(JSONObject message) throws IOException {
        /**
         * topic->from->to->date
         * */
        String from = message.getString("from");
        String to = message.getString("to");
        String topic = message.getString("topic");


        message = handleMessage(cloneMessage(message));
        saveSendMessage(topic, from, to, message);
        saveReceiveMessage(topic, from, to, message);

    }


    public String getMessageList(JSONObject req) {
        String from = req.getString("from");
        String to = req.getString("to");
        String topic = req.getString("topic");
        {
            //我要发给to,那么表示为已经收到  来自 to的消息了
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setFromid(to);
            chatMessage.setToid(from);
            messageService.removeChatMessage(chatMessage);
        }

        String queryDate = req.getString("queryDate");

        String chat_path = getDir(getDir(env.getProperty("chat_path"), topic, from, to));

        File f = new File(chat_path);
        if (f.exists()) {
            String date = search(f.list(), queryDate);

            if (date != null) {
                String chat_target_dir = getDir(chat_path, date);
                try {
                    JSONObject timeMessage = MessageFactory.createTimeMessage(date);
                    String s = readChatFile(chat_target_dir);
                    String s1 = "[" + timeMessage + s + "]";
                    return s1;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "[]";
    }


    public String getExtraDataByToken(String token) {
        String chat_target_dir = getExtraCacheFilePath(token);
        String s = "";
        try {
            s = readFile(chat_target_dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

    private JSONObject cloneMessage(JSONObject message) {
        JSONObject r = new JSONObject();
        r.put("from", message.getString("from"));
        r.put("to", message.getString("to"));
        r.put("mediaType", message.getString("mediaType"));
        r.put("message", message.getString("message"));
        if (message.containsKey("extra"))
            r.put("extra", message.getString("extra"));
        return r;
    }

    private JSONObject handleMessage(JSONObject message) {
        /**
         * 把extra 消息转成image 进行保存
         * */
        if (message.containsKey("extra") == false) {
            return message;
        }
        String token = UUID.nameUUIDFromBytes(message.getString("message").getBytes()).toString().replace("-", "");

        try {
            Writer writer = getExtraCacheOutputRecordStream(token);
            if (writer != null) {
                writer.write(message.getString("extra"));
                writer.close();
            }
            message.put("extra", token);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message;
    }

    private void saveSendMessage(String topic, String sendId, String receiveId, JSONObject message) throws IOException {
        //把message保存到【发送者】的目录下面
        message.put("type", MessageFactory.MessageType.SEND);

        Writer outputRecordStream = getOutputRecordStream(topic, sendId, receiveId);
        outputRecordStream.append("," + message.toJSONString());
        outputRecordStream.close();

    }

    private void saveReceiveMessage(String topic, String sendId, String receiveId, JSONObject message) throws IOException {
        //把message保存到【接收者】的目录下面
        message.put("type", MessageFactory.MessageType.RECV);

        Writer outputRecordStream = getOutputRecordStream(topic, receiveId, sendId);
        outputRecordStream.append("," + message.toJSONString());
        outputRecordStream.close();
    }

    private Writer getOutputRecordStream(String topic, String alice, String bob) throws IOException {
        //return chat_path/topic/alice/bob
        return getOutputRecordStream(env.getProperty("chat_path"), topic, alice, bob);
    }


    private static Writer getOutputRecordStream(String basepath, String topic, String alice, String bob) throws IOException {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        String dir = getDir(basepath, topic, alice, bob, sf.format(new Date()));
        createIfNotExist(dir);

        Writer writer = new FileWriter(dir + File.separator + "1.json", true);
        return writer;
    }

    private Writer getExtraCacheOutputRecordStream(String cacheFileName) throws IOException {

        File f = new File(getExtraCacheFilePath(cacheFileName));
        if (f.exists()) {
            return null;
        }
        Writer writer = new FileWriter(f, true);
        return writer;
    }

    private String getExtraCachePath() {
        String dir = getDir(env.getProperty("chat_path"), "extra");
        createIfNotExist(dir);
        return dir;
    }

    private String getExtraCacheFilePath(String token) {
        String extraCachePath = getExtraCachePath();
        return extraCachePath + File.separator + token;
    }

    private static String readFile(String targetDir) throws IOException {
        FileInputStream reader = new FileInputStream(targetDir);
        byte[] buf = new byte[reader.available()];
        reader.read(buf);

        return new String(buf);
    }

    private static String readChatFile(String targetDir) throws IOException {
        FileInputStream reader = new FileInputStream(targetDir + File.separator + "1.json");

        byte[] buf = new byte[reader.available()];
        reader.read(buf);

        return new String(buf);
    }

    private static String createIfNotExist(String filepath) {
        File f = new File(filepath);
        if (f.exists() == false) {
            f.mkdirs();
        }
        return filepath;
    }


    private static String getDir(String... paths) {
        return String.join(File.separator, paths);
    }


    private static String search(String[] collection, String key) {
        /**
         * 在collection中找到 [最大的] <=key 的返回,如果都大于key,
         * 返回null
         *
         * key=null，返回最大的collection
         * */
        Arrays.sort(collection, string_compartor);
        if (key == null)
            return collection[collection.length - 1];
        int findIndex = Arrays.binarySearch(collection, key, string_compartor);

        if (findIndex < 0) {
            findIndex = -findIndex - 2;
            return findIndex >= 0 ? collection[findIndex] : null;
        } else {
            return collection[findIndex];
        }


    }

    public String createAndActiveRoom(ChatRoom room) {
        List<ChatRoom> select = chatRoomMapper.select(room);

        if (select.size() == 0) {
            String uuid = UUID.randomUUID().toString().replace("-", "");
            room.setUid(uuid);
            room.setValid("T");
            chatRoomMapper.insert(room);
        } else {
            room = select.get(0);
            room.setValid("T");
            chatRoomMapper.update(room);
        }
        JSONObject p = new JSONObject();
        p.put("room", room.getUid());
        fetchVideoChatSetup(p);
        p.put("success", true);
        return p.toJSONString();
    }

    public String hasVideoChat(ChatRoom room) {
        List<ChatRoom> chatRooms = chatRoomMapper.selectValid(room);
        JSONObject p = new JSONObject();
        p.put("success", false);

        if (chatRooms.size() > 0) {
            p.put("room", chatRooms.get(0).getUid());
            fetchVideoChatSetup(p);
            p.put("success", true);
        }
        return p.toJSONString();
    }

    public String removeRoomById(ChatRoom room) {

        JSONObject p = new JSONObject();
        chatRoomMapper.deleteRoomById(room);
        p.put("success", true);
        return p.toJSONString();
    }

    private void fetchVideoChatSetup(JSONObject p) {
        p.put("signalUrl", env.getProperty("signalUrl"));
        p.put("stunServers", env.getProperty("stunServers"));
    }

    private static Comparator string_compartor = new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            return o1.compareTo(o2);
        }
    };

    public static void main(String[] args) throws IOException {


        for (int i = 0; i < 10; i++) {
            System.out.println(UUID.nameUUIDFromBytes("/home/mathai/proj1ects/db/static/chat/MESSAGE_HANDLER/1897290/6153337".getBytes()));
        }

    }

    @Autowired
    private ChatRoomMapper chatRoomMapper;
    @Autowired
    private MessageService messageService;
    private static Environment env;


}
