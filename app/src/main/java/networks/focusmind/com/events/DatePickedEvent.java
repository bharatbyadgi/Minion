package networks.focusmind.com.events;

import org.joda.time.DateTime;

/**
 * Created by michielvanliempt on 15/12/14.
 */
public class DatePickedEvent {
    private final boolean isStartDate;
    private final DateTime date;


    public DatePickedEvent(DateTime date, boolean isStartDate) {
        this.isStartDate = isStartDate;
        this.date = date;
    }

    public DatePickedEvent() {
        date = null;
        isStartDate = false;
    }

    public DateTime getDate() {
        return date;
    }

    public boolean isStartDate() {
        return isStartDate;
    }

    public boolean isValid() {
        return date != null;
    }
}
