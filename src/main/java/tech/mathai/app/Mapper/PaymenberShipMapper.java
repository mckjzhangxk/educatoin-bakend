package tech.mathai.app.Mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import tech.mathai.app.Entity.PayMemberShip;
import tech.mathai.app.Entity.QuestionReport;
import tech.mathai.app.Entity.StudentSubmit;
import tech.mathai.app.Entity.UserAttr;

import java.util.List;

/**
 * Created by Administrator on 2020/7/7.
 */
@Mapper
public interface PaymenberShipMapper {
    public List<PayMemberShip> getPayMemberShip(@Param("userid") String useid);

    void insertPayMemberShip(PayMemberShip p);
}
