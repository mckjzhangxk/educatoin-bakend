package tech.mathai.app.Mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import tech.mathai.app.Entity.AnalysisDto;
import tech.mathai.app.Entity.StudentPlan;
import tech.mathai.app.Entity.UserAttr;

import java.util.List;

/**
 * Created by zlsyt on 2020/6/24.
 */
@Mapper
public interface StudentPlanMapper {
    List<AnalysisDto> getStudentAnalysis(@Param("studentId")String studentId);
    List<StudentPlan> select(UserAttr u);
    int delete(StudentPlan u);
    int insert(StudentPlan p);

    void insertZhuanti(StudentPlan plan);

    StudentPlan selectStudentJob(@Param("studentId")String studentId);
}
