package tech.mathai.app.Mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import tech.mathai.app.Entity.QuestionReport;
import tech.mathai.app.Entity.StudentSubmit;
import tech.mathai.app.Entity.UserAttr;

import java.util.List;

/**
 * Created by Administrator on 2020/7/7.
 */
@Mapper
public interface AssignmentMapper {
    public List<QuestionReport> getUnhandleHomework(UserAttr user);
    public List<StudentSubmit> select(@Param("studentId") String studentId, @Param("hwId") String hwId);
    public void insertStudentSubmit(StudentSubmit q);
    public void remove(StudentSubmit q);

    public int update(QuestionReport report);

    public void updateStudentSubmitById(@Param("questionid") String questionid,
                                        @Param("studentId") String studentId,
                                        @Param("hwId") String hwId,
                                        @Param("result") String result,
                                        @Param("teacherComment") String teacherComment);
}
