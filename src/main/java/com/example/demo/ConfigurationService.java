package com.example.demo;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
@Configuration
@Getter
public class ConfigurationService {

	@Value("${spring.profiles.active:dev}")
	private String profileName;
}