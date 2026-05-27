package com.jobprep.jobprep_platform;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "app.scheduling.enabled=false")
class JobprepPlatformApplicationTests {

	@Test
	void contextLoads() {
	}

}
