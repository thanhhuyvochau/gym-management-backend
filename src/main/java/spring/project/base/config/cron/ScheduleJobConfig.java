package spring.project.base.config.cron;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.transaction.Transactional;

@Configuration
@EnableScheduling
@Transactional
public class ScheduleJobConfig {

}
