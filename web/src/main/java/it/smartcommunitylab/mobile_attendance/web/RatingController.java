package it.smartcommunitylab.mobile_attendance.web;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import it.smartcommunitylab.mobile_attendance.bean.RatingBean;
import it.smartcommunitylab.mobile_attendance.model.Rating;
import it.smartcommunitylab.mobile_attendance.service.RatingService;

@RestController
public class RatingController {

    @Autowired
    private RatingService ratingService;


    @PostMapping(value = "/api/rating")
    @ApiOperation("Store rating")
    public Rating writeRating(@RequestBody RatingWebObj rating) {
        String account = SecurityContextHolder.getContext().getAuthentication().getName();
        return ratingService.saveRating(new RatingBean(account, new Date(), rating.getValue()));
    }

}


class RatingWebObj {
    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
