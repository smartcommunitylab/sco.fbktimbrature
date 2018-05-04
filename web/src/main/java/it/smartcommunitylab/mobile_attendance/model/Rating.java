package it.smartcommunitylab.mobile_attendance.model;

import java.util.Date;

public interface Rating {

    String getAccount();

    Date getTimestamp();

    int getValue();
}
