package it.smartcommunitylab.mobile_attendance.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.junit4.SpringRunner;

import it.smartcommunitylab.mobile_attendance.model.Attendance;
import it.smartcommunitylab.mobile_attendance.persistence.AttendanceEntity;
import it.smartcommunitylab.mobile_attendance.persistence.AttendanceRepository;
import it.smartcommunitylab.mobile_attendance.service.AttendanceService;



@RunWith(SpringRunner.class)
@SpringBootTest
public class AttendanceServiceImplTest {

    @Autowired
    private AttendanceService attendanceSrv;

    @Autowired
    private AttendanceRepository attendanceRepo;

    @Test
    public void store_attendance() {


        when(attendanceRepo.save(any(AttendanceEntity.class))).then(new Answer<AttendanceEntity>() {
            @Override
            public AttendanceEntity answer(InvocationOnMock arg0) throws Throwable {
                AttendanceEntity savedAttendance = new AttendanceEntity();
                savedAttendance.setId(1L);
                savedAttendance.setAccount("account");
                savedAttendance.setTimestamp(convert("2018-04-18T11:20"));
                return savedAttendance;
            }
        });

        Attendance result = attendanceSrv.store("account", convert("2018-04-18T11:20"));
        assertThat(result).isNotNull().hasFieldOrProperty("id")
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("account", "account")
                .hasFieldOrPropertyWithValue("timestamp", convert("2018-04-18T11:20"));
    }

    @Test
    public void read_a_page_attendance_of_an_account_with_default_sorting() {

        when(attendanceRepo.findByAccount("account",
                PageRequest.of(0, 5, new Sort(Direction.DESC, "timestamp"))))
                        .then(new Answer<Page<AttendanceEntity>>() {
                            @Override
                            public Page<AttendanceEntity> answer(InvocationOnMock arg0)
                                    throws Throwable {
                                List<AttendanceEntity> entities = new ArrayList<>();
                                AttendanceEntity entity = new AttendanceEntity();
                                entity.setId(2L);
                                entity.setAccount("account");
                                entity.setTimestamp(convert("2018-04-17T00:10"));
                                entities.add(entity);
                                entity = new AttendanceEntity();
                                entity.setId(1L);
                                entity.setAccount("account");
                                entity.setTimestamp(convert("2018-04-17T00:00"));
                                entities.add(entity);
                                return new PageImpl<>(entities);
                            }
                        });

        Page<Attendance> page = attendanceSrv.read("account", PageRequest.of(0, 5));
        assertThat(page).hasSize(2).first().hasFieldOrPropertyWithValue("timestamp",
                convert("2018-04-17T00:10"));
    }

    @Test
    public void read_a_page_attendance_of_an_account_with_explicit_sorting() {

        when(attendanceRepo.findByAccount("account",
                PageRequest.of(0, 5, new Sort(Direction.DESC, "timestamp"))))
                        .then(new Answer<Page<AttendanceEntity>>() {
                            @Override
                            public Page<AttendanceEntity> answer(InvocationOnMock arg0)
                                    throws Throwable {
                                List<AttendanceEntity> entities = new ArrayList<>();
                                AttendanceEntity entity = new AttendanceEntity();
                                entity.setId(2L);
                                entity.setAccount("account");
                                entity.setTimestamp(convert("2018-04-17T00:01"));
                                entities.add(entity);
                                entity = new AttendanceEntity();
                                entity.setId(1L);
                                entity.setAccount("account");
                                entity.setTimestamp(convert("2018-04-17T00:00"));
                                entities.add(entity);
                                return new PageImpl<>(entities);
                            }
                        });

        when(attendanceRepo.findByAccount("account",
                PageRequest.of(0, 5, new Sort(Direction.ASC, "timestamp"))))
                        .then(new Answer<Page<AttendanceEntity>>() {
                            @Override
                            public Page<AttendanceEntity> answer(InvocationOnMock arg0)
                                    throws Throwable {
                                List<AttendanceEntity> entities = new ArrayList<>();
                                AttendanceEntity entity = new AttendanceEntity();
                                entity.setId(1L);
                                entity.setAccount("account");
                                entity.setTimestamp(convert("2018-04-17T00:00"));
                                entities.add(entity);
                                entity = new AttendanceEntity();
                                entity.setId(2L);
                                entity.setAccount("account");
                                entity.setTimestamp(convert("2018-04-17T00:01"));
                                entities.add(entity);
                                return new PageImpl<>(entities);
                            }
                        });


        Page<Attendance> page = attendanceSrv.read("account",
                PageRequest.of(0, 5, new Sort(Direction.ASC, "timestamp")));
        assertThat(page).hasSize(2).first().hasFieldOrPropertyWithValue("timestamp",
                convert("2018-04-17T00:00"));

    }


    @Test
    public void read_a_page_attendance_of_an_account_in_a_complete_range() {

        when(attendanceRepo.findByAccountAndTimestampBetween("account", convert("2018-04-16T11:20"),
                convert("2018-04-17T11:20"),
                PageRequest.of(0, 5, Sort.by(Direction.DESC, "timestamp"))))
                        .then(new Answer<Page<AttendanceEntity>>() {
                            @Override
                            public Page<AttendanceEntity> answer(InvocationOnMock arg0)
                                    throws Throwable {
                                List<AttendanceEntity> entities = new ArrayList<>();
                                AttendanceEntity entity = new AttendanceEntity();
                                entity.setId(1L);
                                entity.setAccount("account");
                                entity.setTimestamp(convert("2018-04-17T00:00"));
                                entities.add(entity);
                                return new PageImpl<>(entities);
                            }
                        });

        Page<Attendance> page = attendanceSrv.readByRange("account", convert("2018-04-16T11:20"),
                convert("2018-04-17T11:20"), PageRequest.of(0, 5));
        assertThat(page).hasSize(1).first().hasFieldOrPropertyWithValue("timestamp",
                convert("2018-04-17T00:00"));
    }

    @Test
    public void read_a_page_attendance_of_an_account_from_start_to_timestamp() {
        when(attendanceRepo.findByAccountAndTimestampBetween("account", convert("2018-04-16T11:20"),
                convert("2018-04-18T11:20"),
                PageRequest.of(0, 5, Sort.by(Direction.DESC, "timestamp"))))
                        .then(new Answer<Page<AttendanceEntity>>() {
                            @Override
                            public Page<AttendanceEntity> answer(InvocationOnMock arg0)
                                    throws Throwable {
                                List<AttendanceEntity> entities = new ArrayList<>();
                                AttendanceEntity entity = new AttendanceEntity();
                                entity.setId(1L);
                                entity.setAccount("account");
                                entity.setTimestamp(convert("2018-04-17T10:00"));
                                entities.add(entity);
                                return new PageImpl<>(entities);
                            }
                        });

        when(attendanceRepo.findByAccountAndTimestampLessThanEqual("account",
                convert("2018-04-18T11:20"),
                PageRequest.of(0, 5, Sort.by(Direction.DESC, "timestamp"))))
                        .then(new Answer<Page<AttendanceEntity>>() {
                            @Override
                            public Page<AttendanceEntity> answer(InvocationOnMock arg0)
                                    throws Throwable {
                                List<AttendanceEntity> entities = new ArrayList<>();
                                AttendanceEntity entity = new AttendanceEntity();
                                entity.setId(1L);
                                entity.setAccount("account");
                                entity.setTimestamp(convert("2018-04-17T08:00"));
                                entities.add(entity);
                                return new PageImpl<>(entities);
                            }
                        });

        Page<Attendance> page = attendanceSrv.readByRange("account", null,
                convert("2018-04-18T11:20"), PageRequest.of(0, 5));
        assertThat(page).hasSize(1).first().hasFieldOrPropertyWithValue("timestamp",
                convert("2018-04-17T08:00"));
    }

    @Test
    public void read_a_page_attendance_of_an_account_from_timestamp_to_end() {
        when(attendanceRepo.findByAccountAndTimestampGreaterThanEqual("account",
                convert("2018-04-11T11:20"),
                PageRequest.of(0, 5, Sort.by(Direction.DESC, "timestamp"))))
                        .then(new Answer<Page<AttendanceEntity>>() {
                            @Override
                            public Page<AttendanceEntity> answer(InvocationOnMock arg0)
                                    throws Throwable {
                                List<AttendanceEntity> entities = new ArrayList<>();
                                AttendanceEntity entity = new AttendanceEntity();
                                entity.setId(1L);
                                entity.setAccount("account");
                                entity.setTimestamp(convert("2018-04-17T00:00"));
                                entities.add(entity);
                                return new PageImpl<>(entities);
                            }
                        });

        Page<Attendance> page = attendanceSrv.readByRange("account", convert("2018-04-11T11:20"),
                null, PageRequest.of(0, 5));
        assertThat(page).hasSize(1).first().hasFieldOrPropertyWithValue("timestamp",
                convert("2018-04-17T00:00"));
    }

    private Date convert(String dateString) {
        return Date
                .from(LocalDateTime.parse(dateString).atZone(ZoneId.systemDefault()).toInstant());
    }

    @org.springframework.boot.test.context.TestConfiguration
    public static class TestConfiguration {

        @Bean
        public AttendanceRepository attendanceRepo() {
            return mock(AttendanceRepository.class);
        }
    }
}
