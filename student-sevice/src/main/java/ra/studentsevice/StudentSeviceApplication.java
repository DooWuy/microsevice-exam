package ra.studentsevice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class StudentSeviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentSeviceApplication.class, args);
    }

}
