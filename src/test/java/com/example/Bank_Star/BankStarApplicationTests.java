package com.example.Bank_Star;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = BankStarApplication.class)
class BankStarApplicationTests {

	@Autowired
	private ApplicationContext context;

	@Test
	void testApplicationContextLoads() {
		assertThat(context).isNotNull();
	}
}