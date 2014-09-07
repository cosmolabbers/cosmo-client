package com.locationlabs.speedbump.datamodel;

import android.graphics.drawable.Drawable;

import com.locationlabs.speedbump.R;
import com.locationlabs.speedbump.enums.LockEvent;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by jamesl on 9/6/14.
 */
public class AlertEvent {

    LockEvent lockEvent;
    Date occurrence;

    public AlertEvent(LockEvent lockEvent, long timestamp) {
        this.lockEvent = lockEvent;
        occurrence = new Date();
        occurrence.setTime(timestamp);
    }

    public String getEventName() {
        switch(lockEvent) {
            case HARD_ACCELERATION:
                return "Hard Acceleration";

            case HARD_BRAKE:
                return "Hard Brake";

            case HARD_RIGHT:
                return "Turned too fast";

            case HARD_LEFT:
                return "Turned too fast";

            case ACCIDENT:
                return "Collision detected";

            case SPEEDING:
                return "Speeding";

            case OVER_SPEEDING:
                return "Excessive Speeding";

            case IGNITION_ON:
                return "Started driving";

            case IGNITION_OFF:
                return "Ended driving";

            default:
                return null;
        }
    }

    public String getDisplayTime() {
        Calendar calToday = Calendar.getInstance();
        Calendar cal = Calendar.getInstance();
        cal.setTime(occurrence);

        StringBuilder builder = new StringBuilder();
        /*
        if (cal.get(Calendar.DAY_OF_WEEK) == calToday.get(Calendar.DAY_OF_WEEK)) {
            builder.append("Today");
        } else {
            calToday.add(Calendar.DATE, -1);
            if (cal.get(Calendar.DAY_OF_WEEK) == calToday.get(Calendar.DAY_OF_WEEK)) {
                builder.append("Yesterday");
            } else {
                switch (cal.get(Calendar.DAY_OF_WEEK)) {
                    case Calendar.SUNDAY:
                        builder.append("Sun");
                        break;
                    case Calendar.MONDAY:
                        builder.append("Mon");
                        break;
                    case Calendar.TUESDAY:
                        builder.append("Tues");
                        break;
                    case Calendar.WEDNESDAY:
                        builder.append("Wed");
                        break;
                    case Calendar.THURSDAY:
                        builder.append("Thurs");
                        break;
                    case Calendar.FRIDAY:
                        builder.append("Fri");
                        break;
                    case Calendar.SATURDAY:
                        builder.append("Sat");
                        break;
                    default:
                        break;
                }
            }
        }

        builder.append(", ");
        */
        builder.append(cal.get(Calendar.HOUR_OF_DAY));
        builder.append(":");
        int minute = cal.get(Calendar.MINUTE);
        builder.append(minute < 10 ? "0" + minute : minute);
        builder.append(" ");
        builder.append(cal.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM");

        return builder.toString();
    }

    public int getDisplayImageId() {
        switch(lockEvent) {
            case HARD_ACCELERATION:
                return R.drawable.hard_accel_icon;

            case HARD_BRAKE:
                return R.drawable.hard_brake_icon;

            case HARD_RIGHT:
                return R.drawable.hard_turn_icon;

            case HARD_LEFT:
                return R.drawable.hard_turn_icon;

            case ACCIDENT:
                return R.drawable.collision_icon;

            case SPEEDING:
                return R.drawable.speeding_icon;

            case OVER_SPEEDING:
                return R.drawable.speeding_icon;

            case IGNITION_ON:
                return R.drawable.start_trip_icon;

            case IGNITION_OFF:
                return R.drawable.end_trip_icon;

            default:
                return 0;
        }
    }

    public int getPointValue() {
        switch(lockEvent) {
            case IGNITION_ON:
            case IGNITION_OFF:
                return 0;
            case ACCIDENT:
                return 50;
            case OVER_SPEEDING:
                return 10;
            case HARD_ACCELERATION:
            case HARD_BRAKE:
            case HARD_RIGHT:
            case HARD_LEFT:
            case SPEEDING:
                return 4;
            default:
                return 4;
        }
    }

    @Override
    public String toString() {
        return lockEvent.name() + " " + getDisplayTime();
    }

}
