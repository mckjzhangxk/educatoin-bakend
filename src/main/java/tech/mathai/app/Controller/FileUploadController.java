package tech.mathai.app.Controller;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tech.mathai.app.utils.URLUtil;

import java.io.*;

/**
 * Created by zlsyt on 2020/10/21.
 */
@Controller
public class FileUploadController {
    @CrossOrigin("*")
    @PostMapping("/recordVideo")
    @ResponseBody
    public String recordVideo(@RequestPart("content") MultipartFile file,
                              @RequestPart("room") String room,
                              @RequestPart("timestamp") String ts) throws IOException {
//        JSONObject jsonObject=JSONObject.parseObject(body);
//
//        byte[] contents =  Base64.decodeBase64(jsonObject.getString("content"));
//
        FileOutputStream fout=new FileOutputStream("/home/mathai/myproj/edu-back/data/videorecord/"+room+'_'+ts+".webm",true);
        InputStream inputStream = file.getInputStream();


        byte[] buf=new byte[1024];
        int len=0;
        while ((len=inputStream.read(buf))>0){
            fout.write(buf,0,len);
        }

        fout.close();
        return "success";
    }

    @ResponseBody
    @PostMapping("uploadQuestionDb")
    public String SingleFileUpLoad(@RequestParam("myfile")  MultipartFile file) {

        //创建输入输出流
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            //指定上传的位置为 d:/upload/
            File upload_dir =new File( env.getProperty("upload_path"));
            if(upload_dir.exists()==false){
                upload_dir.mkdirs();
            }

            //获取文件的输入流
            inputStream = file.getInputStream();
            //获取上传时的文件名
            String fileName = file.getOriginalFilename();
            //注意是路径+文件名
            File targetFile = new File(upload_dir.getAbsolutePath()+File.separator + fileName);
            outputStream = new FileOutputStream(targetFile);
            byte[] buf=new byte[1024];
            int len=0;
            while ((len=inputStream.read(buf))>0){
                outputStream.write(buf,0,len);
            }
            outputStream.close();
            JSONObject jsonObject = extract_tar_file(targetFile);

            if(jsonObject.getInteger("result")==0){
                JSONObject result = URLUtil.Post(env.getProperty("python_inputQuestions"), "");

                if(result!=null){
                    return result.toJSONString();
                }else {
                    JSONObject ret=new JSONObject();
                    ret.put("success",false);
                    ret.put("msg","网络连接错误，请联系管理员！");
                    return ret.toJSONString();
                }

            }


            return jsonObject.toJSONString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ok";
    }

    private static JSONObject call_cmd(ProcessBuilder builder,String ...cmd){

        JSONObject ret=new JSONObject();
        ret.put("result",-1);
        ret.put("errMsg","未知错误");
        ret.put("cmd",String.join(" ",cmd));



        try {
            builder.command(cmd);
            Process process = builder.start();
            StringBuffer errBuffer=new StringBuffer();
            Thread t_stdout=new Thread()
            {
                @Override
                public void run()
                {
                    BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line = null;

                    try
                    {
                        while((line = in.readLine()) != null)
                        {
                            //System.out.println("output: " + line);
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    finally
                    {
                        try
                        {
                            in.close();
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            };
            Thread t_stderr=new Thread()
            {
                @Override
                public void run()
                {
                    BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                    String line = null;

                    try
                    {
                        while((line = err.readLine()) != null)
                        {
                            errBuffer.append(line+"\n");

                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    finally
                    {
                        try
                        {
                            err.close();
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            };
            t_stdout.start();
            t_stderr.start();
            int errCode = process.waitFor();

            t_stderr.join();
            t_stdout.join();
            ret.put("result",errCode);
            ret.put("errMsg",errBuffer.toString());

        } catch (IOException e) {
            e.printStackTrace();
            ret.put("errMsg","进程启动错误");
        } catch (InterruptedException e) {
            e.printStackTrace();
            ret.put("errMsg",e.toString());
        }


        return ret;

    }

    private static String getDirName(String filename){
        return filename.substring(0,filename.lastIndexOf(File.separator));
    }
    public static JSONObject extract_tar_file(File tarfile) {

        String tarpath=tarfile.getAbsolutePath();
        String tardir=getDirName(tarpath);

        ProcessBuilder builder = new ProcessBuilder();

        JSONObject result = call_cmd(builder, "tar", "-C", tardir, "-xvf",tarpath);


        if(result.getInteger("result")==0){
            result = call_cmd(builder, "rm", "-r" ,tarpath);
        }


        return result;
    }
    @Autowired
    private Environment env;



    public static void main(String[] args) throws IOException, InterruptedException {

//
//        JSONObject call = extract_tar_file(new File("/home/mathai/projects/db/upload/test.tar"));
//        System.out.println(call);

        FileOutputStream file1=new FileOutputStream("/home/mathai/myproj/edu-back/data/videorecord/1.webm",true);

        FileInputStream inputStream=new FileInputStream("/home/mathai/myproj/edu-back/data/videorecord/3.webm");

        byte[] buf=new byte[1024];
        int len=0;

        while ((len=inputStream.read(buf))>0){
            file1.write(buf,0,len);
        }

        file1.close();
        inputStream.close();
    }
}
