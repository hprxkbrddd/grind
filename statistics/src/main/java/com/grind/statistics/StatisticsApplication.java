package com.grind.statistics;

import com.grind.statistics.repository.ClickhouseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.grind.statistics.repository.ClickhouseQueries.ANALYTICS_DB;
import static com.grind.statistics.repository.ClickhouseQueries.DDL_CREATE_DATABASE;
import static com.grind.statistics.repository.ClickhouseQueries.DDL_CREATE_TABLES;
import static com.grind.statistics.repository.ClickhouseQueries.DDL_CREATE_VIEWS;

@SpringBootApplication
public class StatisticsApplication {

	private static final Logger log = LoggerFactory.getLogger(StatisticsApplication.class);
	private static final Pattern CREATE_OBJECT_PATTERN = Pattern.compile(
			"CREATE\\s+(?:OR\\s+REPLACE\\s+)?(?:(?:MATERIALIZED\\s+)?VIEW|TABLE|DATABASE)\\s+"
					+ "(?:IF\\s+NOT\\s+EXISTS\\s+)?(?<name>[\\w.]+)",
			Pattern.CASE_INSENSITIVE
	);

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(StatisticsApplication.class, args);
		ClickhouseRepository repository = ctx.getBean(ClickhouseRepository.class);
	}

}
