package utils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

public class DateUtils {

    private DateUtils() {}

    public static LocalDate dateToLocalDate(Date date) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd") ;
        return LocalDate.parse(dateFormatter.format(date)) ;
    }
}