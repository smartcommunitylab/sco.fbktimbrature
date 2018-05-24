package it.smartcommunitylab.mobile_attendance.service;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import it.smartcommunitylab.mobile_attendance.model.Attendance;
import it.smartcommunitylab.mobile_attendance.model.AttendanceRead;

public interface AttendanceService {

    Attendance store(String account, Date timestamp);

    Page<AttendanceRead> read(String account, Pageable pageRequest);

    Page<AttendanceRead> readByRange(String account, Date fromTimestamp, Date toTimestamp,
            Pageable pageRequest);
}
