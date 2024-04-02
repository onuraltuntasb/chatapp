package com.chatapp.chatapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


//active profile for default test

@ActiveProfiles("dev")
@SpringBootTest
class ChatappApplicationTests {

	@Test
	void contextLoads() {
	}

}
