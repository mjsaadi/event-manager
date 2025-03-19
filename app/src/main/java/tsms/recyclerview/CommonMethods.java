package tsms.recyclerview;

/**
 * Created by shams on 10/21/2017.
 */

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CommonMethods {
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static DateFormat timeFormat = new SimpleDateFormat("hh:mm:ss");

    public static String getCurrentTime(Date date) {

        Date today = Calendar.getInstance().getTime();
        return timeFormat.format(date);
    }

    public static String getCurrentDate(Date date) {

        Date today = Calendar.getInstance().getTime();
        return dateFormat.format(date);
    }

}
