package tech.mathai.app.utils;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by Administrator on 2020/7/16.
 */
public class PropertiesUtils {
    static Properties getAppProperity() throws IOException {
        if(m_properities==null)
            m_properities= PropertiesLoaderUtils.loadProperties(new EncodedResource(new ClassPathResource("application.properties"), "UTF-8"));;
        return m_properities;
    }

    public static String get(String field) throws IOException {
        return getAppProperity().getProperty(field);
    }

    static Properties m_properities=null;
}
