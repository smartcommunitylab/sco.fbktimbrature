package it.smartcommunitylab.mobile_attendance.web;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.smartcommunitylab.mobile_attendance.model.Attendance;
import it.smartcommunitylab.mobile_attendance.service.AttendanceService;

@RestController()
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceSrv;

    private static final Logger logger = LoggerFactory.getLogger(AttendanceController.class);

    @GetMapping(value = "/echo")
    public String echo() {
        return "Up and Running!!!!!";
    }

    @PostMapping(value = "/api/attendance/{account}")
    public void store(@PathVariable String account) {
        Date timestamp = new Date();
        attendanceSrv.store(account, timestamp);
        logger.info("New attendance for login {} at {}", account, timestamp);

    }


    @GetMapping(value = "/api/attendance/{account}")
    public Page<Attendance> read(@PathVariable String account,
            @RequestParam(required = false, name = "fromTs") @DateTimeFormat(
                    pattern = "yyyy-MM-dd'T'HH:mm:ss") Date fromTimestamp,
            @RequestParam(required = false, name = "toTs") @DateTimeFormat(
                    pattern = "yyyy-MM-dd'T'HH:mm:ss") Date toTimestamp,
            Pageable pageRequest) {
        return attendanceSrv.readByRange(account, fromTimestamp, toTimestamp, pageRequest);
    }

}
