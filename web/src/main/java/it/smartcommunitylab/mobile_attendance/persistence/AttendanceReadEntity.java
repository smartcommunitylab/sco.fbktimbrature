package it.smartcommunitylab.mobile_attendance.persistence;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import it.smartcommunitylab.mobile_attendance.model.AttendanceRead;

@Entity
@Table(name = "vw_global_attendance_records")
public class AttendanceReadEntity implements AttendanceRead {

    @Id
    private Long id;

    @Column(name = "Email")
    private String account;

    @Column(name = "Date")
    private Date timestamp;

    @Column(name = "tipo_timbratura")
    private String mode;

    @Column(name = "datainvio")
    private Date inazRegistrationTimestamp;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Date getInazRegistrationTimestamp() {
        return inazRegistrationTimestamp;
    }

    public void setInazRegistrationTimestamp(Date inazRegistrationTimestamp) {
        this.inazRegistrationTimestamp = inazRegistrationTimestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
