package it.smartcommunitylab.mobile_attendance.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import it.smartcommunitylab.aac.AACProfileService;
import it.smartcommunitylab.aac.AACService;
import it.smartcommunitylab.mobile_attendance.service.AttendanceService;

@RunWith(SpringRunner.class)
@WebMvcTest(AttendanceController.class)
@ActiveProfiles(profiles = {"dev", "no-sec"})
@EnableSpringDataWebSupport // permits correct instantiation of pageable
public class AttendanceControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AACProfileService aacProfileService;

    @MockBean
    private AACService aacService;

    @MockBean
    private AttendanceService attendanceSrv;


    @Test
    public void post_an_attendance() throws Exception {
        mvc.perform(post("/api/attendance").header("Authorization", "Bearer MY_VALID_TOKEN"))
                .andExpect(status().isOk());
    }

    @Test
    public void read_attendances() throws Exception {
        mvc.perform(get("/api/attendance").header("Authorization", "Bearer MY_VALID_TOKEN"))
                .andExpect(status().isOk());
    }

}
