package it.smartcommunitylab.mobile_attendance.bean;

import java.util.Date;

import it.smartcommunitylab.mobile_attendance.model.Rating;

public class RatingBean implements Rating {
    private String account;
    private Date timestamp;
    private int value;


    public RatingBean(String account, Date timestamp, int value) {
        this.account = account;
        this.timestamp = timestamp;
        this.value = value;
    }

    @Override
    public String getAccount() {
        return account;
    }

    @Override
    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public int getValue() {
        return value;
    }


}
