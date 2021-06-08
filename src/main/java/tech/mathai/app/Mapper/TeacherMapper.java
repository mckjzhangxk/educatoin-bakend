package tech.mathai.app.Mapper;

import org.apache.ibatis.annotations.Mapper;
import tech.mathai.app.Entity.Assignment;
import tech.mathai.app.Entity.UserAttr;


import java.util.List;

/**
 * Created by zlsyt on 2020/6/20.
 */
@Mapper
public interface TeacherMapper {
    public int insert(Assignment asm);
    public List<Assignment> select(UserAttr user);

    List<UserAttr> getStudents(UserAttr user);
}
