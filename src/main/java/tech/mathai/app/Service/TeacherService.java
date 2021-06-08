package tech.mathai.app.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.mathai.app.Entity.Assignment;
import tech.mathai.app.Entity.UserAttr;
import tech.mathai.app.Mapper.TeacherMapper;

import java.util.*;

/**
 * Created by zlsyt on 2020/6/20.
 */
@Service
public class TeacherService {

    public void publicAssignment( Assignment asm){
        asm.setUid(UUID.randomUUID().toString().replace("-",""));
        for(String cid:asm.getQuestionids()){
            asm.setCurrentid(cid);
            teacherMapper.insert(asm);
        }
    }

    public  List<Assignment> getAssignment(UserAttr teacher){

        List<Assignment> tm=new ArrayList<>();
        List<Assignment> asms=teacherMapper.select(teacher);

        for(int i=0;i<asms.size();i++){
            if(i==0){
                tm.add(asms.get(i));
                tm.get(tm.size()-1).getQuestionids().add(asms.get(i).getCurrentid());
                continue;
            }
            Assignment prev=asms.get(i-1);
            Assignment curr=asms.get(i);
            if(prev.getUid().equals(curr.getUid())){
                tm.get(tm.size()-1).getQuestionids().add(curr.getCurrentid());
            }else {
                tm.add(asms.get(i));
                tm.get(tm.size()-1).getQuestionids().add(asms.get(i).getCurrentid());
            }
        }

        return tm;
    }
    public List<UserAttr> getStudents(UserAttr user) {
        List<UserAttr> r=teacherMapper.getStudents(user);
        return r;
    }
    @Autowired
    private TeacherMapper teacherMapper;


}
