package spring.project.base.util.other;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class GymPlanUtil {
    public static Instant calculateToDateOfPlan(Instant instant, long daysToAdd) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        LocalDateTime resultDateTime = localDateTime.plusDays(daysToAdd);
        return resultDateTime.toInstant(ZoneOffset.UTC);
    }
}
