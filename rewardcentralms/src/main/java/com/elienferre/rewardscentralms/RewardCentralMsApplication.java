package com.elienferre.rewardscentralms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Locale;

@SpringBootApplication
public class RewardCentralMsApplication {

	public static void main(String[] args) {
		Locale.setDefault(Locale.FRENCH);
		SpringApplication.run(RewardCentralMsApplication.class, args);
	}

}
