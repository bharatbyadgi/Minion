package networks.focusmind.com.events;

/**
 * Created by michielvanliempt on 21/12/14.
 */
public class TimePickedEvent {
    private final int minute;
    private final int hour;
    private final boolean isStartDate;

    public TimePickedEvent(int hourOfDay, int minute, boolean isStartDate) {
        this.hour = hourOfDay;
        this.minute = minute;
        this.isStartDate = isStartDate;
    }

    public TimePickedEvent() {
        minute = -1;
        hour = -1;
        isStartDate = true;
    }

    public int getMinute() {
        return minute;
    }

    public int getHour() {
        return hour;
    }

    public boolean isStartDate() {
        return isStartDate;
    }

    public boolean isValid() {
        return hour >= 0 && minute >= 0;
    }
}
