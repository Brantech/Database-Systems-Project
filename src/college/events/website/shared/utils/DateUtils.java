package college.events.website.shared.utils;

import com.google.gwt.i18n.client.DateTimeFormat;
import java.util.Date;

public class DateUtils {
    enum Month {
        JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE, JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER
    }

    enum Day {
        SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY
    }

    public static final long DAY_MILLIS = 1000 * 60 * 60 * 24;
    public static final long MONTH_MILLIS = DAY_MILLIS * 30;

    public static int getDaysInMonth(Month month) {
        if(month.equals(Month.JANUARY)) {
            return 31;
        } else if(month.equals(Month.FEBRUARY)) {
            if(Integer.parseInt(DateTimeFormat.getFormat("yyyy").format(new Date())) % 4 == 0) {
                return 29;
            } else {
                return 28;
            }
        } else if(month.equals(Month.MARCH)) {
            return 31;
        } else if(month.equals(Month.APRIL)) {
            return 30;
        } else if(month.equals(Month.MAY)) {
            return 31;
        } else if(month.equals(Month.JUNE)) {
            return 30;
        } else if(month.equals(Month.JULY)) {
            return 31;
        } else if(month.equals(Month.AUGUST)) {
            return 31;
        } else if(month.equals(Month.SEPTEMBER)) {
            return 30;
        } else if(month.equals(Month.OCTOBER)) {
            return 31;
        } else if(month.equals(Month.NOVEMBER)) {
            return 30;
        } else if(month.equals(Month.DECEMBER)) {
            return 31;
        } else {
            return 0;
        }
    }

    public static Month monthBefore(Month month) {
        if(month == null) {
            return null;
        }

        if(month.ordinal() == 0) {
            return Month.values()[11];
        }

        return Month.values()[month.ordinal() - 1];
    }

    public static Month monthAfter(Month month) {
        if(month == null) {
            return null;
        }

        return Month.values()[(month.ordinal() + 1) % 12];
    }

    public static int getDay(Date date) {
        if(date == null) {
            return 0;
        }

        return Integer.parseInt(DateTimeFormat.getFormat("d").format(date));
    }

    public static Day getDayOfWeek(Date date) {
        return Day.values()[date.getDay()];
    }

    public static Month getMonth(Date date) {
        if(date == null) {
            return null;
        }

        return Month.values()[Integer.parseInt(DateTimeFormat.getFormat("M").format(date))];
    }

    public static int getYear(Date date) {
        if(date == null) {
            return 0;
        }

        return Integer.parseInt(DateTimeFormat.getFormat("yyyy").format(date));
    }
}
