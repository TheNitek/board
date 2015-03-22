package de.naeveke.board.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ComponentScan(basePackages = "de.naeveke.board",
        excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, value = Configuration.class))
@PropertySource(ignoreResourceNotFound = true, value = {"classpath:board.properties", "classpath:board-test.properties", "file:${java:comp/env/appConfigPath}/board.properties"})
@Import({DatabaseConfig.class, WebSocketConfig.class})
public class BoardConfig {

}
