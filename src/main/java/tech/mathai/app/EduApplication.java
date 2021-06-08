package tech.mathai.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan(basePackages = "tech.mathai.app.Controller")
public class EduApplication {


	public static void main(String[] args) {
		SpringApplication.run(EduApplication.class, args);
	}

}
