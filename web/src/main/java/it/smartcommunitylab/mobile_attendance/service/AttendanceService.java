package it.smartcommunitylab.mobile_attendance.service;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import it.smartcommunitylab.mobile_attendance.model.Attendance;

public interface AttendanceService {

    Attendance store(String account, Date timestamp);

    Page<Attendance> read(String account, Pageable pageRequest);

    Page<Attendance> readByRange(String account, Date fromTimestamp, Date toTimestamp,
            Pageable pageRequest);
}
