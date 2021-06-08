package tech.mathai.app.Service;

import com.alibaba.fastjson.JSONObject;
import org.jdom.JDOMException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.mathai.app.Entity.PayMemberShip;
import tech.mathai.app.Mapper.PaymenberShipMapper;
import tech.mathai.app.utils.DataUtils;
import tech.mathai.app.utils.URLUtil;
import tech.mathai.app.utils.WXUtils;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.List;
import java.util.UUID;

/**
 * Created by mathai on 20-12-3.
 */
@Service
public class WxService {



    public static String wxTongYiXiaDan(JSONObject orderInfo) throws NoSuchAlgorithmException, JDOMException, IOException {
        String url="https://api.mch.weixin.qq.com/pay/unifiedorder";



        String s="appid="+APPID
                +"&body="+orderInfo.getString("body")
                +"&mch_id="+PARTNERID
                +"&nonce_str="+ WXUtils.nonceStr()
                +"&notify_url="+NOTIFY_URL
                +"&out_trade_no="+WXUtils.getOrderNum()
                +"&sign_type=MD5"
                +"&spbill_create_ip="+WXUtils.getIp()
                +"&total_fee="+orderInfo.getInteger("total_fee")
                +"&trade_type=APP"
                +"&key="+Key;

        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(s.getBytes());
        String sign =  new BigInteger(1, md.digest()).toString(16);

        s+="&sign="+sign.toUpperCase();


        String param=WXUtils.str2Xml(s);

        String s1 = URLUtil.PostS(url, param);

        String ret=WXUtils.xml2Json(s1).toJSONString();

        System.out.println("===================================");
        System.out.println(ret);
        System.out.println("===================================");
        return ret;

    }

    public String insertPaymentOrder(PayMemberShip p){
        /**
         * 用户购买的会员后，我把购买记录插入到数据库
         * */
        JSONObject ret=new JSONObject();
        ret.put("success",true);
        p.setUuid(UUID.randomUUID().toString().replace("-", ""));
        p.setOrderTime(DataUtils.getCurrentTime());

        String last_expire_time=DataUtils.getCurrentTime();
        List<PayMemberShip> pays = getPayMemberShip(p.getUserid());
        if(pays.size()>0){
            String db_last_expire_time = pays.get(0).getExpireTime();
            //最靠后的时间的基础上，加上购买的月份
            if(db_last_expire_time.compareTo(last_expire_time)>0)
                last_expire_time=db_last_expire_time;
        }
        try {
            p.setExpireTime(DataUtils.addTime(last_expire_time, p.getMouths()));
        } catch (ParseException e) {
            ret.put("success",false);
            ret.put("message",e.getMessage());
            e.printStackTrace();
            return ret.toJSONString();
        }

        m_paymapper.insertPayMemberShip(p);
        ret.put("activate",true);
        ret.put("expireTime",p.getExpireTime());
        return ret.toJSONString();
    }

    public String getUserAccountActivateInfo(PayMemberShip p) {
        JSONObject ret=new JSONObject();

        List<PayMemberShip> payMemberShip = getPayMemberShip(p.getUserid());
        if(payMemberShip.size()==0){
            ret.put("activate",false);
            ret.put("expireTime","");
        }else {
            PayMemberShip ps = payMemberShip.get(0);
            ret.put("activate",ps.getExpireTime().compareTo(DataUtils.getCurrentTime())>0);
            ret.put("expireTime",ps.getExpireTime());
        }
        return ret.toJSONString();
    }
    private List<PayMemberShip> getPayMemberShip(String userid){
        return m_paymapper.getPayMemberShip(userid);
    }



    public JSONObject getAccessToken(String code){
        String url="https://api.weixin.qq.com/sns/oauth2/access_token?appid="+APPID+"&secret="+APPSECRET+"&code="+code+"&grant_type=authorization_code";
        JSONObject getinfo = URLUtil.Get(url, "");

        if(getinfo.containsKey("errcode")){
            return null;
        }else {
            return getinfo;
        }
    }

    public JSONObject getWXUserInfo(String token,String openid){
        String url="https://api.weixin.qq.com/sns/userinfo?access_token="+token+"&openid="+openid;
        JSONObject getinfo = URLUtil.Get(url, "");

        if(getinfo.containsKey("errcode")){
            return null;
        }else {
            return getinfo;
        }
    }

    @Autowired
    private PaymenberShipMapper m_paymapper;

    public static final String APPID="wx7db9ff64eedecc25";
    public static final String APPSECRET="f8956112e9c5c102fc3c96761ed59316";
    public static final String PARTNERID="1604812164";
    public static final String Key="a50b1f6986f647418475d12d099b2867";
    public static final String NOTIFY_URL="http://mathai.tech:7002/wxnotify";

    public static void main(String[] args) throws JDOMException, IOException, NoSuchAlgorithmException {
//        String s="<xml>\n" +
//                "   <return_code><![CDATA[SUCCESS]]></return_code>\n" +
//                "   <return_msg><![CDATA[OK]]></return_msg>\n" +
//                "   <appid><![CDATA[wx2421b1c4370ec43b]]></appid>\n" +
//                "   <mch_id><![CDATA[10000100]]></mch_id>\n" +
//                "   <nonce_str><![CDATA[IITRi8Iabbblz1Jc]]></nonce_str>\n" +
//                "   <sign><![CDATA[7921E432F65EB8ED0CE9755F0E86D72F]]></sign>\n" +
//                "   <result_code><![CDATA[SUCCESS]]></result_code>\n" +
//                "   <prepay_id><![CDATA[wx201411101639507cbf6ffd8b0779950874]]></prepay_id>\n" +
//                "   <trade_type><![CDATA[APP]]></trade_type>\n" +
//                "</xml>";
//
        JSONObject d=new JSONObject();
//
        d.put("body","APP支付测试");
        d.put("total_fee",1);


        System.out.println(wxTongYiXiaDan(d));


//        String ss="appid=wx2421b1c4370ec43b&attach=支付测试&body=APP支付测试&mch_id=10000100&nonce_str=1add1a30ac87aa2db72f57a2375d8fec&notify_url=http://wxpay.wxutil.com/pub_v2/pay/notify.v2.php&out_trade_no=1415659990&spbill_create_ip=14.23.150.211&total_fee=1&trade_type=APP&key=wx2421b1c4370ec43b";

//        System.out.println(str2Xml(ss));
    }

}
