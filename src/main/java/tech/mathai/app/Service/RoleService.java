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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class RoleService {

    @Autowired
    private Environment env;



    private JSONObject getDefaultReuslt(){
        JSONObject jsonObject = new JSONObject();


        jsonObject.put("Success",true);

        return jsonObject;
    }
    public String saveRole(String data) throws IOException {
        String json_chapter = env.getProperty("menu_path");
        JSONObject j=JSONObject.parseObject(data);

        FileOutputStream f=new FileOutputStream(json_chapter+"/menu.json");
        f.write(j.toJSONString().getBytes());
        f.close();

        return getDefaultReuslt().toJSONString();
    }

    public String queryRole() throws IOException {
        JSONObject defaultReuslt = getDefaultReuslt();


        String json_chapter = env.getProperty("menu_path");
        String filename=json_chapter+"/menu.json";
        File file=new File(filename);
        if(file.exists()==false){
            defaultReuslt.put("Success",false);

        }else{
            FileInputStream inputStream=new FileInputStream(file);
            byte[] buf=new byte[inputStream.available()];
            inputStream.read(buf);


            defaultReuslt.put("menu",JSONObject.parse(buf));
        }

        return defaultReuslt.toJSONString();
    }

}
