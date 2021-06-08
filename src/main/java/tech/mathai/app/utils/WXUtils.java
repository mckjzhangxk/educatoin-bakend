package tech.mathai.app.utils;

import com.alibaba.fastjson.JSONObject;
import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import tech.mathai.app.Service.UserAttrService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by mathai on 20-12-11.
 */
public class WXUtils {


    public static String nonceStr(){
        return UUID.randomUUID().toString().replace("-","");
    }
    public static String timeStamp(){
        Date da = new Date();
        //注意：这个地方da.getTime()得到的是一个long型的值
        return da.getTime()/1000+"";
    }
    public static String str2Xml(String ss){
        String[] split = ss.split("&");


        Element rootElement = new Element("xml");
        Document doc = new Document(rootElement);


        for(String kv:split){
            String[] split1 = kv.split("=");
            String key=split1[0];
            String value=split1[1];

            if("key".equals(key)) continue;
            Element e=new Element(key);

            if(!"sign".equals(key))
                e.addContent(new CDATA(value));
            else
                e.setText(value);

            rootElement.addContent(e);
        }




        XMLOutputter XMLout = new XMLOutputter();
        Format format = Format.getPrettyFormat();//创建XML文档输出的格式化方式(该格式化方式，用户可以自定义)
        format.setIndent("    ");//使用4个空格进行缩进, 可以兼容文本编辑器
        format.setEncoding("UTF-8");//设置编码格式
        XMLout.setFormat(format);//使XML格式化输出

        try {
            return   XMLout.outputString(doc); // 将Document写入到FileWriter文件流中 输出XML数据
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }
    public static JSONObject xml2Json(String xml) throws JDOMException, IOException {
        JSONObject result=new JSONObject();


        SAXBuilder builder=new SAXBuilder();

        Document document = builder.build(new ByteArrayInputStream(xml.getBytes()));

        Element rootElement = document.getRootElement();
        List<Element> children = rootElement.getChildren();
        for (Element elemnt:children){
            elemnt.getName();
            result.put(elemnt.getName(),elemnt.getText());
            elemnt.getText();
        }

        return result;
    }

    public static String getOrderNum(){

        SimpleDateFormat sf=new SimpleDateFormat("yyyyMMddHHmmss");

        return sf.format(new Date())+ UserAttrService.getNum(6);

    }

    public static String getIp(){
        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
            return addr.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return "192.168.1.36";

    }

    public static void main(String[] args) {

    }
}
