package sigebi.cms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ApplicationCMS {

	public static void main(String[] args) {
		SpringApplication.run(ApplicationCMS.class, args);
	}

}
