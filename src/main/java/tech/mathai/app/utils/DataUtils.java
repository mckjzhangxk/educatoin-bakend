package tech.mathai.app.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by mathai on 20-12-14.
 */
public class DataUtils {

    public static String getCurrentTime(){
        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return sf.format(new Date());
    }

    public static String addTime(String time,int mouth) throws ParseException {
        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
        Date d = sf.parse(time);

        Calendar cl=Calendar.getInstance();
        cl.setTime(d);

        cl.add(Calendar.MONTH,mouth);

        return sf.format(cl.getTime());
    }
    public static void main(String[] args) throws ParseException {
        System.out.println(getCurrentTime());
        System.out.println(getCurrentTime().compareTo(addTime(getCurrentTime(), 0)));



    }
}
