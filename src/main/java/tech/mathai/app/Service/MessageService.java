package tech.mathai.app.Service;

import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import tech.mathai.app.Entity.ChatMessage;
import tech.mathai.app.Entity.Message;
import tech.mathai.app.Entity.UserAttr;
import tech.mathai.app.Mapper.MessageMapper;
import tech.mathai.app.Mapper.UserAttrMapper;

import java.util.List;

/**
 * Created by Administrator on 2020/7/10.
 */
@Service
public class MessageService {

    public void insertMessage(Message msg){
        messageMapper.insert(msg);
    }

    public Message getMessage(String receive){
        List<Message> messages = messageMapper.selectById(receive);
        if(messages.size()>0){
            Message message = messages.get(0);
            messageMapper.deleteByUid(message);
            return message;
        }
        return null;
    }

    public void insertChatMessage(ChatMessage msg){
        messageMapper.insertChatMessage(msg);
    }

    public void removeChatMessage(ChatMessage msg){
        messageMapper.removeChatMessage(msg);
    }


    public List<String> selectChatMessage(String rid){
       return messageMapper.selectChatMessage(rid);
    }


    public String getTeacherAndChatMessage(UserAttr user) {
        UserAttr userRet = null;
        if(user.getType()==0){
            userRet=m_dao.getTeacherByStudentId(user.getId());
        }else if(user.getType()==1){
            userRet= m_dao.getTeacherByParentId(user.getId());
        }

        JSONObject ret=new JSONObject();
        if(userRet!=null){
            ret.put("id",userRet.getId());
            ret.put("name",userRet.getName());

            ret.put("hasMessage",false);


            ChatMessage cm=new ChatMessage();
            cm.setToid(user.getId());
            cm.setFromid(userRet.getId());
            List<ChatMessage> teacher2others = messageMapper.selectChatMessageByID(cm);

            ret.put("hasMessage",teacher2others.size()>0);
        }
        return ret.toString();
    }

    @Autowired
    private UserAttrMapper m_dao;
    @Autowired
    private MessageMapper messageMapper;
}
