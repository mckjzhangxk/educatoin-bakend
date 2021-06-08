package tech.mathai.app.Mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import tech.mathai.app.Entity.ChapterDto;
import tech.mathai.app.Entity.Pushment;
import tech.mathai.app.Entity.Question;

import java.util.List;

/**
 * Created by zlsyt on 2020/6/24.
 */
@Mapper
public interface ChapterMapper {
    int insertQuestion(Question question);
    int insertChapter(ChapterDto chapterDto);

    int update(Question question);
    int updateQuesionName(@Param("id")String id,@Param("name")String name);
    int updateChapterName(@Param("id")String id,@Param("name")String name);

    int updateZhangjie(Question question);

    void removeQuestionById(@Param("questionId")String id);

    void removeChapterById(@Param("chapterId") String chapterId);

    void removeQuestionByCatalog(@Param("catalogId")  String catalogId);
}
