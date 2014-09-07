package com.locationlabs.speedbump.datamodel;

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
                return "Right Turn too fast";

            case HARD_LEFT:
                return "Left Turn too fast";

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
        builder.append(cal.get(Calendar.HOUR_OF_DAY));
        builder.append(":");
        int minute = cal.get(Calendar.MINUTE);
        builder.append(minute < 10 ? "0" + minute : minute);
        builder.append(" ");
        builder.append(cal.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM");

        return builder.toString();
    }

}
