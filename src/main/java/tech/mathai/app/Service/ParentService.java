package tech.mathai.app.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.mathai.app.Entity.UserAttr;
import tech.mathai.app.Mapper.ParentMapper;

import java.util.List;

/**
 * Created by zlsyt on 2020/6/20.
 */
@Service
public class ParentService {



    public List<UserAttr> getStudents(UserAttr user) {
        List<UserAttr> r= parentMapper.getStudents(user);
        return r;
    }
    @Autowired
    private ParentMapper parentMapper;


}
