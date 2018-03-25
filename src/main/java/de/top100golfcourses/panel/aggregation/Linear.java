package de.top100golfcourses.panel.aggregation;

import de.top100golfcourses.panel.entity.BucketColor;
import de.top100golfcourses.panel.entity.RankedCourse;

/**
 * An algorithm that linearly increases the points from bucket to bucket.
 */
public class Linear extends AbstractAlgorithm {

    @Override
    public int score(RankedCourse course) {
        String color = course.getBucketColor();
        if (color.equals(BucketColor.Field.name())) {
            return 1;
        }
        else if (color.equals(BucketColor.Bronze.name())) {
            return 2;
        }
        else if (color.equals(BucketColor.Silver.name())) {
            return 3;
        }
        else if (color.equals(BucketColor.Gold.name())) {
            return 4;
        }
        else return 0;
    }

}
