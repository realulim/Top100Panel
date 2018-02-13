package de.top100golfcourses.panel.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class RankingsTest {
    
    public RankingsTest() {
    }
    
    @Before
    public void setUp() {
    }

    @Test
    public void reorderRankedCourses() {
        List<RankedCourse> courses = new ArrayList<>();
        for (int i = 1; i < 6; i++) {
            RankedCourse course = new RankedCourse();
            course.setPos(i);
            courses.add(course);
        }
        Rankings CUT = new Rankings();
        CUT.setRankedCourses(courses);

        CUT.deleteRankedCourseAt(3);
        checkIntegrity(courses);

        RankedCourse course = new RankedCourse();
        course.setPos(4);
        CUT.insertRankedCourseAt(3, course);
        checkIntegrity(courses);

        // move row from index 2 (pos 3) to index 4 (pos 5)
        int startPos = 3, endPos = 5;
        CUT.deleteRankedCourseAt(startPos - 1);
        course = new RankedCourse();
        course.setPos(endPos);
        CUT.insertRankedCourseAt(endPos - 1, course);
        checkIntegrity(courses);

        // move row from index 3 (pos 4) to index 0 (pos 1)
        startPos = 4; endPos = 1;
        CUT.deleteRankedCourseAt(startPos - 1);
        course = new RankedCourse();
        course.setPos(endPos);
        CUT.insertRankedCourseAt(endPos - 1, course);
        checkIntegrity(courses);
    }

    private void checkIntegrity(List<RankedCourse> courses) {
        for (int i = 0; i < courses.size(); i++) {
            System.out.println(i + ": " + courses.get(i).toString());
            assertEquals(i+1, courses.get(i).getPos());
        }
        System.out.println("==========");
    }

    @Test
    public void changeBucket_noBucketLeader() {
        List<RankedCourse> courses = new ArrayList<>();
        Rankings CUT = new Rankings();
        CUT.setRankedCourses(courses);

        checkIntegrity(courses);
        assertNull(CUT.getFirstCourseIn(BucketColor.Gold));
    }

    @Test
    public void changeBucket() {
        List<RankedCourse> courses = new ArrayList<>();
        int bucket = 0;
        for (int i = 1; i < 15; i += 3) {
            bucket++;
            for (int j = 0; j < 3; j++) {
                RankedCourse course = new RankedCourse();
                course.setPos(i + j);
                course.setBucket(bucket);
                courses.add(course);
            }
        }
        Rankings CUT = new Rankings();
        CUT.setRankedCourses(courses);

        checkIntegrity(courses);

        List<RankedCourse> bucketLeaders = new ArrayList<>();
        for (BucketColor color : Arrays.asList(new BucketColor[] { BucketColor.Gold, BucketColor.Silver, BucketColor.Bronze, BucketColor.Field, BucketColor.DQ})) {
            bucketLeaders.add(CUT.getFirstCourseIn(color));
        }
        for (RankedCourse bucketLeader : bucketLeaders) {
            RankedCourse newCourse = new RankedCourse();
            newCourse.setPos(bucketLeader.getPos());
            assertEquals(Math.max(1, bucketLeader.getBucket() - 1), insertCourseBefore(CUT, bucketLeader, newCourse).getBucket());
            assertEquals(bucketLeader.getPos() - 1, newCourse.getPos());
        }

        bucketLeaders.clear();
        for (BucketColor color : Arrays.asList(new BucketColor[] { BucketColor.Gold, BucketColor.Silver, BucketColor.Bronze, BucketColor.Field, BucketColor.DQ})) {
            bucketLeaders.add(CUT.getFirstCourseIn(color));
        }
        for (RankedCourse bucketLeader : bucketLeaders) {
            RankedCourse newCourse = new RankedCourse();
            newCourse.setPos(bucketLeader.getPos() + 1);
            assertEquals(bucketLeader.getBucket(), insertCourseAfter(CUT, bucketLeader, newCourse).getBucket());
            assertEquals(bucketLeader.getPos() + 1, newCourse.getPos());
        }
    }

    private RankedCourse insertCourseBefore(Rankings rankings, RankedCourse ref, RankedCourse toInsert) {
        rankings.insertRankedCourseAt(Math.max(0, ref.getPos() - 1), toInsert);
        checkIntegrity(rankings.getRankedCourses());
        return toInsert;
    }

    private RankedCourse insertCourseAfter(Rankings rankings, RankedCourse ref, RankedCourse toInsert) {
        rankings.insertRankedCourseAt(ref.getPos(), toInsert);
        checkIntegrity(rankings.getRankedCourses());
        return toInsert;
    }

}
