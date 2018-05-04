package it.smartcommunitylab.mobile_attendance.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import it.smartcommunitylab.mobile_attendance.model.Rating;
import it.smartcommunitylab.mobile_attendance.service.RatingService;

@Service
public class LogRatingServiceImpl implements RatingService {

    private static final Logger logger = LoggerFactory.getLogger("ratingLog");

    @Override
    public Rating saveRating(Rating rating) {
        logger.info("{},{},{}", rating.getTimestamp().getTime(), rating.getAccount(),
                rating.getValue());
        return rating;
    }

}
