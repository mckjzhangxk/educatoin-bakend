package tech.mathai.app.Config;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tech.mathai.app.Entity.ChatMessage;
import tech.mathai.app.Service.ChatService;
import tech.mathai.app.Service.MessageService;

import java.io.IOException;
import java.util.*;

/**
 * Created by mathai on 20-10-10.
 */
//https://www.devglan.com/spring-boot/spring-websocket-integration-example-without-stomp
@Service
public class SocketHandler extends TextWebSocketHandler {


    @Override//5105ee6e-bda6-4dcd-a0e1-14dfa914aedf
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        sessions_users.put(session.getId(),null);
        //printCache();
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        JSONObject request = JSONObject.parseObject(message.getPayload());
        String sessionId=session.getId();

        if(request.containsKey("register")){
            String userId=request.getString("userId");
            users_sessions.put(userId,session);
            if(sessions_users.containsKey(sessionId)){
                sessions_users.put(sessionId,userId);
            }
            //printCache();
        }else {
            //默认所有消息都会插入表，用途提醒使用
            ChatMessage msg=new ChatMessage();
            msg.setUid(UUID.randomUUID().toString().replace("-",""));
            msg.setFromid(request.getString("from"));
            msg.setToid(request.getString("to"));
            msg.setMessage(request.getString("message"));
            messageService.removeChatMessage(msg);
            messageService.insertChatMessage(msg);

            request.put("type",MessageFactory.MessageType.RECV);
            String toUser = request.getString("to");

            //及时发送
            if(users_sessions.containsKey(toUser)){
                session=users_sessions.get(toUser);
                session.sendMessage(new TextMessage(request.toString()));
            }

            //保存
            Timer timer=new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        chatService.recordChatMessage(request);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            },1);


        }


    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String sessionId=session.getId();
        if(sessions_users.containsKey(sessionId)){
            String userId=sessions_users.get(sessionId);
            users_sessions.remove(userId);
            sessions_users.remove(sessionId);
        }
        super.afterConnectionClosed(session, status);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        super.handleTransportError(session, exception);
    }


    private void printCache(){
        System.out.println("users_sessions:"+users_sessions.size());
        System.out.println("sessions_users:"+sessions_users.size());
        System.out.println("----------------------------------------------");
    }


    @Autowired
    public void setMessageService(MessageService service){
        messageService=service;
    }
    private ChatService chatService=new ChatService();
    private static MessageService messageService;

    public static Map<String,WebSocketSession> users_sessions=new HashMap<>();
    public static Map<String,String> sessions_users=new HashMap<>();
}
