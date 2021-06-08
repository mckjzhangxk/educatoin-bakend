package tech.mathai.app.Mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import tech.mathai.app.Entity.ChatMessage;
import tech.mathai.app.Entity.Message;

import java.util.List;

/**
 * Created by Administrator on 2020/7/10.
 */

@Mapper
public interface MessageMapper {
    int insert(Message msg);
    int insertChatMessage(ChatMessage msg);
    void removeChatMessage(ChatMessage msg);
    int deleteByUid(Message msg);
    List<Message> selectById(@Param("receiveId") String rid);

    List<String> selectChatMessage(@Param("fromId") String rid);
    List<ChatMessage> selectChatMessageByID(ChatMessage msg);
}
