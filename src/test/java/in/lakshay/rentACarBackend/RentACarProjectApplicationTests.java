package in.lakshay.rentACarBackend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

// boots the full context against a throwaway MySQL container:
// schema creation (ddl-auto) and data.sql seeding both run for real,
// no local MySQL required
// skipped automatically when no usable Docker environment is detected
@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class RentACarProjectApplicationTests {

	@Container
	@ServiceConnection
	static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0");

	@Test
	void contextLoads() {
	}

}
