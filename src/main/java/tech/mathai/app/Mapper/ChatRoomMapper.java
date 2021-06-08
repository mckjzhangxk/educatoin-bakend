package tech.mathai.app.Mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import tech.mathai.app.Entity.ChatRoom;
import tech.mathai.app.Entity.User;

import java.util.List;

@Mapper
public interface ChatRoomMapper {
    int insert(ChatRoom room);
    int update(ChatRoom room);
    List<ChatRoom> select(ChatRoom room);
    List<ChatRoom> selectValid(ChatRoom room);

    int deleteRoomById(ChatRoom room);
}
