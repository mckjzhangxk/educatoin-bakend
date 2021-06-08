package tech.mathai.app.Mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import tech.mathai.app.Entity.Pushment;
import tech.mathai.app.Entity.StudentPlan;
import tech.mathai.app.Entity.UserAttr;

import java.util.List;

/**
 * Created by zlsyt on 2020/6/24.
 */
@Mapper
public interface PushmentMapper {
    List<Pushment> select(Pushment pushment);
    List<Pushment> selectUnhandle(Pushment pushment);
    List<Pushment> selectAfter(Pushment pushment);
    int insert(Pushment pushment);
    int update(Pushment pushment);
    int remove(Pushment pushment);

    int updateStudentPushment(@Param("studentId") String sid, @Param("stime") String stime, @Param("type") String type);
}
