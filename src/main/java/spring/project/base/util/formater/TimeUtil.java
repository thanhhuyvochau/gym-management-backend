package spring.project.base.util.formater;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class TimeUtil {

    public static Boolean checkDay(String one, String two) throws ParseException {

        Instant datOne = Instant.parse(one);
        String oneSubString = datOne.toString().substring(0, 10).replaceAll("-", " ");


        Instant dayTwo = Instant.parse(two);
        String twoSubString = dayTwo.toString().substring(0, 10).replaceAll("-", " ");


        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy MM dd");

        Date dateOne = myFormat.parse(oneSubString);
        Date dateTwo = myFormat.parse(twoSubString);
        long check = dateOne.getTime() - dateTwo.getTime();
        if (check > 0) {
            return false;
        }
        return true;

    }

    public static Instant convertDayInstant(String day) {
        String s = Instant.parse(day)
                .truncatedTo(ChronoUnit.DAYS)
                .toString();
        return Instant.parse(s);
    }

    public static Boolean checkTwoDateBigger(String one, String two, Integer plusDay) throws ParseException {

        Instant datOne = Instant.parse(one).plus(plusDay, ChronoUnit.DAYS);
        String oneSubString = datOne.toString().substring(0, 10).replaceAll("-", " ");


        Instant dayTwo = Instant.parse(two);
        String twoSubString = dayTwo.toString().substring(0, 10).replaceAll("-", " ");

        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy MM dd");

        Date dateOne = myFormat.parse(oneSubString);
        Date dateTwo = myFormat.parse(twoSubString);
        long check = dateOne.getTime() - dateTwo.getTime();
        if (check > 0) {
            return false;
        }
        return true;

    }

    public static boolean isValidBirthday(Instant birthday) {
        LocalDate targetDate = birthday.atZone(ZoneOffset.UTC).toLocalDate();
        LocalDate currentDate = Instant.now().atZone(ZoneOffset.UTC).toLocalDate();
        Period period = Period.between(targetDate, currentDate);
        return period.getYears() >= 18;
    }

    public static boolean isLessThanHourDurationOfNow(Instant instant, int duration) {
        Instant currentInstant = Instant.now();
        Duration difference = Duration.between(instant.truncatedTo(ChronoUnit.HOURS),
                currentInstant.truncatedTo(ChronoUnit.HOURS));
        long differenceInHour = difference.toHours();
        return Math.abs(differenceInHour) < duration;
    }

}
