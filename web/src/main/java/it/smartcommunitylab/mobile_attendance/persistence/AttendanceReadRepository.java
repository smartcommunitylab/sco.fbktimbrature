package it.smartcommunitylab.mobile_attendance.persistence;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttendanceReadRepository
        extends PagingAndSortingRepository<AttendanceReadEntity, Long> {

    Page<AttendanceReadEntity> findByAccount(String account, Pageable pageRequest);

    Page<AttendanceReadEntity> findByAccountAndTimestampBetween(String account, Date fromTimestamp,
            Date toTimestamp, Pageable pageRequest);

    Page<AttendanceReadEntity> findByAccountAndTimestampGreaterThanEqual(String account,
            Date fromTimestamp, Pageable pageRequest);

    Page<AttendanceReadEntity> findByAccountAndTimestampLessThanEqual(String account,
            Date toTimestamp, Pageable pageRequest);

}
