package tech.mathai.app.Controller;

import com.alibaba.fastjson.JSONObject;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import org.jdom.JDOMException;
import tech.mathai.app.utils.WXUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by mathai on 20-12-3.
 */

@WebServlet(name="wxnotify",urlPatterns = "/wxnotify")
public class WXNotifyServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
        ServletInputStream inputStream = req.getInputStream();
        ByteOutputStream out=new ByteOutputStream();


        byte[] buf=new byte[1024];
        int len=0;

        while ((len=inputStream.read(buf))!=-1){
            out.write(buf,0,len);
        }

        String body=new String(out.toByteArray());

        try {
            JSONObject jsonObject = WXUtils.xml2Json(body);

            System.out.println(jsonObject);
        } catch (JDOMException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }
}
