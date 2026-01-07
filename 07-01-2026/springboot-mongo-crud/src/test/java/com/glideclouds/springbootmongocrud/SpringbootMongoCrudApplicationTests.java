package com.glideclouds.springbootmongocrud;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
	"MONGODB_URI=mongodb://localhost:27017/test_db",
	"SERVER_PORT=8082"
})
class SpringbootMongoCrudApplicationTests {

	@Test
	void contextLoads() {
	}
}
