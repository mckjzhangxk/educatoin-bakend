package tech.mathai.app.Mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import tech.mathai.app.Entity.Question;
import tech.mathai.app.Entity.QuestionReport;
import tech.mathai.app.Entity.StudentSubmit;
import tech.mathai.app.Entity.UserAttr;

import java.util.List;


/**
 * Created by zlsyt on 2020/6/19.
 */
@Mapper
public interface QuestionReportMapper {
    void insert(QuestionReport q);
    void insertStuAss(QuestionReport q);
    List<Question> selectQuestion(UserAttr user);
    List<QuestionReport> selectReport(UserAttr user);
    List<QuestionReport> selectReportByHwId(QuestionReport report);

      List<StudentSubmit>  getErrorAnswer(@Param("studentId") String studentId,@Param("questionId") String questionId);
}
