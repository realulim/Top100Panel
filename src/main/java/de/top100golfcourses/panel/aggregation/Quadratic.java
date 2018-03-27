package de.top100golfcourses.panel.aggregation;

import de.top100golfcourses.panel.entity.BucketColor;
import de.top100golfcourses.panel.entity.RankedCourse;

/**
 * An algorithm that quadratically increases the points from bucket to bucket.
 */
public class Quadratic extends AbstractAlgorithm {

    @Override
    public int score(RankedCourse course) {
        return getPoints(BucketColor.byName(course.getBucketColor()));
    }

    @Override
    public int getPoints(BucketColor color) {
        switch(color) {
            case Gold:
                return 8;
            case Silver:
                return 4;
            case Bronze:
                return 2;
            case Field:
                return 1;
            default:
                return 0;
        }
    }

}
