package it.smartcommunitylab.mobile_attendance.model;

import java.util.Date;

public interface AttendanceRead extends Attendance {
    String getMode();

    Date getInazRegistrationTimestamp();
}
