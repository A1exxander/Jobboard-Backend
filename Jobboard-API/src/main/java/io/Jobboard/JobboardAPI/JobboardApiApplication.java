package io.Jobboard.JobboardAPI;
import io.Jobboard.User.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.sql.SQLException;


@SpringBootApplication
public class JobboardApiApplication {

	public static void main(String[] args) throws SQLException, InterruptedException {
		SpringApplication.run(JobboardApiApplication.class, args);
	}

}
