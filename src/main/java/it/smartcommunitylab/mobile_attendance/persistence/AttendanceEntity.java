package it.smartcommunitylab.mobile_attendance.persistence;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import it.smartcommunitylab.mobile_attendance.model.Attendance;

@Entity
@Table(name = "mobile_attendance_records")
public class AttendanceEntity implements Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "Login")
    private String account;

    @Column(name = "Date")
    private Date timestamp;


    public Long getId() {
        return id;
    }

    public String getAccount() {
        return account;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
