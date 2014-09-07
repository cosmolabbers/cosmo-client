package com.locationlabs.speedbump.enums;

/**
 *
 */
public enum LockEvent {

    // sent as camelcase
    HARD_ACCELERATION,
    HARD_BRAKE,
    HARD_RIGHT,
    HARD_LEFT,
    ACCIDENT,
    SPEEDING,
    OVER_SPEEDING,
    IGNITION_ON,
    IGNITION_OFF;


    public static LockEvent getLockEventFromString(String string) {
        if (string.equals("IgnitionOn")) {
            return IGNITION_ON;
        } else if (string.equals("IgnitionOff")) {
            return IGNITION_OFF;
        } else if (string.equals("Accident")) {
            return ACCIDENT;
        } else if (string.equals("HardRight")) {
            return HARD_RIGHT;
        } else if (string.equals("HardLeft")) {
            return HARD_LEFT;
        } else if (string.equals("HardAcceleration")) {
            return HARD_ACCELERATION;
        } else if (string.equals("HardBrake")) {
            return HARD_BRAKE;
        } else if (string.equals("Speeding")) {
            return SPEEDING;
        } else if (string.equals("OverSpeeding")) {
            return OVER_SPEEDING;
        } else {
            return null;
        }
    }

}
