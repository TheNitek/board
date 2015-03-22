package de.naeveke.board.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ComponentScan(basePackages = "de.naeveke.board")
@PropertySource(ignoreResourceNotFound = true, value = {"classpath:board.properties", "classpath:board-test.properties", "file:${java:comp/env/appConfigPath}/board.properties"})
public class BoardConfig {

}
