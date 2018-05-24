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
import it.smartcommunitylab.mobile_attendance.model.AttendanceRead;
import it.smartcommunitylab.mobile_attendance.persistence.AttendanceEntity;
import it.smartcommunitylab.mobile_attendance.persistence.AttendanceReadEntity;
import it.smartcommunitylab.mobile_attendance.persistence.AttendanceReadRepository;
import it.smartcommunitylab.mobile_attendance.persistence.AttendanceRepository;
import it.smartcommunitylab.mobile_attendance.service.AttendanceService;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepo;

    @Autowired
    private AttendanceReadRepository attendanceReadRepo;

    @Override
    public Attendance store(String account, Date timestamp) {
        AttendanceEntity attendance = new AttendanceEntity();
        attendance.setAccount(account);
        attendance.setTimestamp(timestamp);
        return attendanceRepo.save(attendance);
    }

    @Override
    public Page<AttendanceRead> read(String account, Pageable pageRequest) {
        Page<AttendanceReadEntity> pageEntities =
                attendanceReadRepo.findByAccount(account, setDefaultSortIfNothing(pageRequest));
        return convertToAttendanceRead(pageEntities);
    }

    @Override
    public Page<AttendanceRead> readByRange(String account, Date fromTimestamp, Date toTimestamp,
            Pageable pageRequest) {
        Pageable defaultPageRequest = setDefaultSortIfNothing(pageRequest);
        Page<AttendanceReadEntity> pageEntities = null;
        if (fromTimestamp != null && toTimestamp != null) {
            pageEntities = attendanceReadRepo.findByAccountAndTimestampBetween(account,
                    fromTimestamp, toTimestamp, defaultPageRequest);
        } else if (fromTimestamp != null) {
            pageEntities = attendanceReadRepo.findByAccountAndTimestampGreaterThanEqual(account,
                    fromTimestamp, defaultPageRequest);
        } else if (toTimestamp != null) {
            pageEntities = attendanceReadRepo.findByAccountAndTimestampLessThanEqual(account,
                    toTimestamp, defaultPageRequest);
        } else {
            return read(account, defaultPageRequest);
        }
        return convertToAttendanceRead(pageEntities);
    }

    private Page<Attendance> convertToAttendance(Page<AttendanceEntity> pageEntities) {
        List<Attendance> attendances = new ArrayList<>(pageEntities.getContent());
        return new PageImpl<Attendance>(attendances, pageEntities.getPageable(),
                pageEntities.getTotalElements());
    }

    private Page<AttendanceRead> convertToAttendanceRead(Page<AttendanceReadEntity> pageEntities) {
        List<AttendanceRead> attendances = new ArrayList<>(pageEntities.getContent());
        return new PageImpl<AttendanceRead>(attendances, pageEntities.getPageable(),
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
