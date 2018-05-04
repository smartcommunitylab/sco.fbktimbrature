package it.smartcommunitylab.mobile_attendance.web;


import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import it.smartcommunitylab.aac.AACProfileService;
import it.smartcommunitylab.aac.AACService;
import it.smartcommunitylab.aac.model.AccountProfile;
import it.smartcommunitylab.mobile_attendance.service.AttendanceService;
import it.smartcommunitylab.mobile_attendance.service.RatingService;

@RunWith(SpringRunner.class)
@WebMvcTest({AttendanceController.class, RatingController.class})
@ActiveProfiles(profiles = {"sec"})
@EnableSpringDataWebSupport // permits correct instantiation of pageable
@TestPropertySource(properties = {"aac.attendanceScopes.read=ATTENDANCE.SCOPE"})
public class AuthenticationInterceptorTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AACProfileService aacProfileService;

    @MockBean
    private AACService aacService;

    @MockBean
    private AttendanceService attendanceSrv;

    @MockBean
    private RatingService ratingSrv;


    @Test
    public void token_is_OK() throws Exception {
        given(aacService.isTokenApplicable("MY_VALID_TOKEN", "ATTENDANCE.SCOPE")).willReturn(true);
        given(aacProfileService.findAccountProfile("MY_VALID_TOKEN"))
                .will(new Answer<AccountProfile>() {
                    @Override
                    public AccountProfile answer(InvocationOnMock arg0) throws Throwable {
                        AccountProfile profile = new AccountProfile();
                        profile.addAttribute("google", "email", "me@fbk.eu");
                        return profile;
                    }
                });

        mvc.perform(get("/api/attendance").header("Authorization", "Bearer MY_VALID_TOKEN"))
                .andExpect(status().isOk());
    }

    @Test
    public void missing_token() throws Exception {
        mvc.perform(get("/api/attendance")).andExpect(status().isUnauthorized());
    }


    @Test
    public void not_fbk_account() throws Exception {
        given(aacProfileService.findAccountProfile("MY_VALID_TOKEN"))
                .will(new Answer<AccountProfile>() {
                    @Override
                    public AccountProfile answer(InvocationOnMock arg0) throws Throwable {
                        AccountProfile profile = new AccountProfile();
                        profile.addAttribute("google", "email", "me@google.com");
                        return profile;
                    }
                });

        mvc.perform(get("/api/attendance").header("Authorization", "Bearer MY_VALID_TOKEN"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void missing_email_attribute() throws Exception {
        given(aacProfileService.findAccountProfile("MY_VALID_TOKEN"))
                .will(new Answer<AccountProfile>() {
                    @Override
                    public AccountProfile answer(InvocationOnMock arg0) throws Throwable {
                        AccountProfile profile = new AccountProfile();
                        return profile;
                    }
                });

        mvc.perform(get("/api/attendance").header("Authorization", "Bearer MY_VALID_TOKEN"))
                .andExpect(status().isForbidden());
    }


    @Test
    public void scope_is_not_correct() throws Exception {
        given(aacService.isTokenApplicable("MY_VALID_TOKEN", "ATTENDANCE.SCOPE")).willReturn(false);
        given(aacProfileService.findAccountProfile("MY_VALID_TOKEN"))
                .will(new Answer<AccountProfile>() {
                    @Override
                    public AccountProfile answer(InvocationOnMock arg0) throws Throwable {
                        AccountProfile profile = new AccountProfile();
                        return profile;
                    }
                });

        mvc.perform(get("/api/attendance").header("Authorization", "Bearer MY_VALID_TOKEN"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void rating_request() throws Exception {
        given(aacProfileService.findAccountProfile("MY_VALID_TOKEN"))
                .will(new Answer<AccountProfile>() {
                    @Override
                    public AccountProfile answer(InvocationOnMock arg0) throws Throwable {
                        AccountProfile profile = new AccountProfile();
                        profile.addAttribute("google", "email", "me@fbk.eu");
                        return profile;
                    }
                });

        mvc.perform(post("/api/rating").contentType(MediaType.APPLICATION_JSON_UTF8)
                .content("{\"value\":11}").header("Authorization", "Bearer MY_VALID_TOKEN"))
                .andExpect(status().isOk());
    }
}
