package networks.focusmind.com.fragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.widget.TimePicker;

import org.greenrobot.eventbus.EventBus;
import org.joda.time.DateTime;

import networks.focusmind.com.events.TimePickedEvent;

/**
 * Created by michielvanliempt on 21/12/14.
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    public static final String HOUR = "hour";
    public static final String MINUTE = "minute";
    public static final String IS_START_DATE = "is-from-date";
    private boolean isStartDate;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        DateTime now = DateTime.now();

        int hour = now.getHourOfDay();
        if (arguments.containsKey(HOUR)) {
            hour = arguments.getInt(HOUR);
        }
        int minute = now.getMonthOfYear();
        if (arguments.containsKey(MINUTE)) {
            minute = arguments.getInt(MINUTE);
        }
        isStartDate = arguments.getBoolean(IS_START_DATE, true);

        return new TimePickerDialog(getActivity(), this, hour, minute, true);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
         EventBus.getDefault().post(new TimePickedEvent(hourOfDay, minute, isStartDate));
    }

    public static void showForDate(FragmentManager fragmentManager, DateTime dateTime, boolean isStart) {
        showForTime(fragmentManager, dateTime.getHourOfDay(), dateTime.getMinuteOfHour(), isStart);
    }

    public static void showForTime(FragmentManager fragmentManager, int hours, int minutes, boolean isStart) {
        Bundle args = new Bundle();
        args.putBoolean(TimePickerFragment.IS_START_DATE, isStart);
        args.putInt(TimePickerFragment.HOUR, hours);
        args.putInt(TimePickerFragment.MINUTE, minutes);

        TimePickerFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.setArguments(args);
        timePickerFragment.show(fragmentManager, "time-picker");
    }

    @Override
    public void dismiss() {
        super.dismiss();
        EventBus.getDefault().post(new TimePickedEvent());
    }
}
