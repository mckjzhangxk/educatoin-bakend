package tech.mathai.app.Service;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

import org.springframework.stereotype.Service;

@Service
public class SMSService {

    private final String product = "Dysmsapi";
    private final String domain = "dysmsapi.aliyuncs.com";

    private final String accessKeyId = "LTAI4GJuKC6K7z9BGpzAvVuE";
    private final String getAccessKeySecret = "BOTHob1CGhrI4PtNpsWVV5NZwgmf9T";
    private final String signName="数人数学";



    /*
     * PhoneNumbers  接收短信的手机号码
     * SignName  短信签名名称
     * TemplateCode  短信模板ID
     * TemplateParam	短信模板变量对应的实际值，JSON格式
     * */
    public String sendMessage(String phoneNumber, String templateCode,
                              String templateParam, String signName) throws ClientException {
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou",
                accessKeyId, getAccessKeySecret);

        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();

        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("PhoneNumbers", phoneNumber);
        request.putQueryParameter("SignName", signName);
        request.putQueryParameter("TemplateCode", templateCode);
        request.putQueryParameter("TemplateParam", templateParam);

        CommonResponse response = client.getCommonResponse(request);
        return response.getData();
    }


     public JSONObject sendYangzhengMa(String phone,String code){
         JSONObject params=new JSONObject();
         params.put("code", code);

         String sms_205690326 = null;
         try {
             sms_205690326 = sendMessage(phone, "SMS_205690326", params.toJSONString(), signName);
             return JSONObject.parseObject(sms_205690326);
         } catch (ClientException e) {
             e.printStackTrace();
         }
         JSONObject jsonObject = new JSONObject();
         jsonObject.put("Code","Error");
         jsonObject.put("Message","系统发送内部异常");

         return jsonObject;
     }

    public JSONObject sendUserInfo(String phone,String username,String password){
        JSONObject params=new JSONObject();
        params.put("username", username);
        params.put("password", password);

        String sms_205690326 = null;
        try {
            sms_205690326 = sendMessage(phone, "SMS_205881060", params.toJSONString(), signName);
            return JSONObject.parseObject(sms_205690326);
        } catch (ClientException e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Code","Error");
        jsonObject.put("Message","系统发送内部异常");

        return jsonObject;
    }

}
