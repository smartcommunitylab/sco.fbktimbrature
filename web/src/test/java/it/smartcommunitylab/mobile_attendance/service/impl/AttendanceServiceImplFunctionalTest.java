package it.smartcommunitylab.mobile_attendance.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import it.smartcommunitylab.mobile_attendance.service.AttendanceService;

// @RunWith(SpringRunner.class)
// @SpringBootTest
public class AttendanceServiceImplFunctionalTest {

    @Autowired
    private AttendanceService attendanceSrv;

    /*
     * FIXME, with the split between read and write entity this test is broken
     */
    // @Test
    public void store_user_story() {
        assertThat(attendanceSrv.read("account", PageRequest.of(0, 5))).hasSize(0);
        assertThat(attendanceSrv.store("account", new Date())).isNotNull();
        assertThat(attendanceSrv.read("account", PageRequest.of(0, 5))).hasSize(1);
    }

}
