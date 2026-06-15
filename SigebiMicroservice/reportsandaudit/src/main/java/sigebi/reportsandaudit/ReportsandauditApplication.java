package sigebi.reportsandaudit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ReportsandauditApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReportsandauditApplication.class, args);
	}

}
