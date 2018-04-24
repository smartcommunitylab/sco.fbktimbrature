package it.smartcommunitylab.mobile_attendance.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import it.smartcommunitylab.mobile_attendance.model.Attendance;
import it.smartcommunitylab.mobile_attendance.persistence.AttendanceEntity;
import it.smartcommunitylab.mobile_attendance.persistence.AttendanceRepository;
import it.smartcommunitylab.mobile_attendance.service.AttendanceService;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepo;

    @Override
    public Attendance store(String account, Date timestamp) {
        AttendanceEntity attendance = new AttendanceEntity();
        attendance.setAccount(account);
        attendance.setTimestamp(timestamp);
        return attendanceRepo.save(attendance);
    }

    @Override
    public Page<Attendance> read(String account, Pageable pageRequest) {
        Page<AttendanceEntity> pageEntities =
                attendanceRepo.findByAccount(account, setDefaultSortIfNothing(pageRequest));
        return convertToAttendance(pageEntities);
    }

    @Override
    public Page<Attendance> readByRange(String account, Date fromTimestamp, Date toTimestamp,
            Pageable pageRequest) {
        Pageable defaultPageRequest = setDefaultSortIfNothing(pageRequest);
        Page<AttendanceEntity> pageEntities = null;
        if (fromTimestamp != null && toTimestamp != null) {
            pageEntities = attendanceRepo.findByAccountAndTimestampBetween(account, fromTimestamp,
                    toTimestamp, defaultPageRequest);
        } else if (fromTimestamp != null) {
            pageEntities = attendanceRepo.findByAccountAndTimestampGreaterThanEqual(account,
                    fromTimestamp, defaultPageRequest);
        } else if (toTimestamp != null) {
            pageEntities = attendanceRepo.findByAccountAndTimestampLessThanEqual(account,
                    toTimestamp, defaultPageRequest);
        } else {
            return read(account, defaultPageRequest);
        }
        return convertToAttendance(pageEntities);
    }

    private Page<Attendance> convertToAttendance(Page<AttendanceEntity> pageEntities) {
        List<Attendance> attendances = new ArrayList<>(pageEntities.getContent());
        return new PageImpl<Attendance>(attendances, pageEntities.getPageable(),
                pageEntities.getTotalElements());
    }

    private Pageable setDefaultSortIfNothing(Pageable pageRequest) {
        if (pageRequest != null) {
            if (pageRequest.getSort().equals(Sort.unsorted())) {
                return PageRequest.of(pageRequest.getPageNumber(), pageRequest.getPageSize(),
                        new Sort(Direction.DESC, "timestamp"));
            }
        }
        return pageRequest;
    }
}
