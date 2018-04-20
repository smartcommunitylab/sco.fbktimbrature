package it.smartcommunitylab.mobile_attendance.persistence;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttendanceRepository extends PagingAndSortingRepository<AttendanceEntity, Long> {

    Page<AttendanceEntity> findByAccount(String account, Pageable pageRequest);

    Page<AttendanceEntity> findByAccountAndTimestampBetween(String account, Date fromTimestamp,
            Date toTimestamp, Pageable pageRequest);

    Page<AttendanceEntity> findByAccountAndTimestampGreaterThanEqual(String account,
            Date fromTimestamp, Pageable pageRequest);

    Page<AttendanceEntity> findByAccountAndTimestampLessThanEqual(String account, Date toTimestamp,
            Pageable pageRequest);
}
