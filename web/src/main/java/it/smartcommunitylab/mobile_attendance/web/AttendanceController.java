package it.smartcommunitylab.mobile_attendance.web;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import it.smartcommunitylab.mobile_attendance.model.Attendance;
import it.smartcommunitylab.mobile_attendance.model.AttendanceRead;
import it.smartcommunitylab.mobile_attendance.service.AttendanceService;

@RestController
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceSrv;

    private static final Logger logger = LoggerFactory.getLogger(AttendanceController.class);

    @GetMapping(value = "/echo")
    public String echo() {
        return "Up and Running!!!!!";
    }

    @PostMapping(value = "/api/attendance")
    @ApiOperation("Attendance action")
    public Attendance store() {
        Date timestamp = new Date();
        String account = SecurityContextHolder.getContext().getAuthentication().getName();
        Attendance stored = attendanceSrv.store(account, timestamp);
        logger.info("New attendance for login {} at {}", account, timestamp);
        return stored;

    }


    @GetMapping(value = "/api/attendance")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "integer", paramType = "query",
                    value = "Page to return (zero-based)"),
            @ApiImplicitParam(name = "size", dataType = "integer", paramType = "query",
                    value = "Number of Records per page."),})
    @ApiOperation("Read attendances")
    public Page<AttendanceRead> read(
            @RequestParam(required = false, name = "fromTs") @DateTimeFormat(
                    pattern = "yyyy-MM-dd'T'HH:mm:ss") Date fromTimestamp,
            @RequestParam(required = false, name = "toTs") @DateTimeFormat(
                    pattern = "yyyy-MM-dd'T'HH:mm:ss") Date toTimestamp,
            Pageable pageRequest) {
        String account = SecurityContextHolder.getContext().getAuthentication().getName();
        // FIX the account could not be the mail when I read the view in db
        account = account.replace("@fbk.eu", "");
        return attendanceSrv.readByRange(account, fromTimestamp, toTimestamp, pageRequest);
    }

}
