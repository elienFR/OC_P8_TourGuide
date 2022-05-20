package com.elienferre.trippricerms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Locale;

@SpringBootApplication
public class TripPricerMsApplication {

	public static void main(String[] args) {
		Locale.setDefault(Locale.FRENCH);
		SpringApplication.run(TripPricerMsApplication.class, args);
	}

}
