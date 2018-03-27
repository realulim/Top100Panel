package de.top100golfcourses.panel.aggregation;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.top100golfcourses.panel.entity.AggregatedCourse;
import de.top100golfcourses.panel.entity.BucketColor;
import de.top100golfcourses.panel.entity.RankedCourse;
import de.top100golfcourses.panel.entity.Rankings;

public class AbstractAlgorithmTest {

    private AbstractAlgorithm CUT_Linear, CUT_Quadratic;
    private AbstractAlgorithm[] algorithms;
    private List<Rankings> allRankings; // it's actually a Collection, but we use a list here to facilitate easier reasoning in assertions

    private final Random wheel = new SecureRandom();

    @Before
    public void setUp() {
        CUT_Linear = new Linear();
        CUT_Quadratic = new Quadratic();
        algorithms = new AbstractAlgorithm[] { CUT_Linear, CUT_Quadratic };
        allRankings = new ArrayList<>();
    }
    
    @Test
    public void calculate_noRankings() {
        for (AbstractAlgorithm CUT : algorithms) {
            List<AggregatedCourse> courses = CUT.calculate(allRankings);
            assertTrue(courses.isEmpty());
        }
    }

    @Test
    public void calculate_noCourseNames() {
        createRankings(3);
        addCourse(0, createCourse(BucketColor.Gold.getIndex()));
        addCourse(1, createCourse(BucketColor.Gold.getIndex()));
        for (AbstractAlgorithm CUT : algorithms) {
            List<AggregatedCourse> courses = CUT.calculate(allRankings);
            assertEquals(0, courses.size());
        }
    }

    @Test
    public void calculate_justOneCourseHasAName() {
        createRankings(2);
        addCourse(0, createCourse(BucketColor.Gold.getIndex()));
        addCourse(1, createCourse(BucketColor.Gold.getIndex(), "My Course"));
        for (AbstractAlgorithm CUT : algorithms) {
            System.out.println("Algorithm: " + CUT);
            List<AggregatedCourse> courses = CUT.calculate(allRankings);
            assertEquals(1, courses.size());
            assertEquals(6, courses.get(0).getBucket());
            assertTrue(courses.get(0).getAveragePoints() > 0.0);
            System.out.println(courses.get(0).getComments());
        }
    }

    @Test
    public void calculate() {
        int numRankings = wheel.nextInt(7) + 1;
        createRandomRankings(numRankings);
        for (Algorithm CUT : algorithms) {
            int totalVotes = 0;
            for (Rankings rankings : allRankings) {
                totalVotes += rankings.getRankedCourses().size();
            }
            System.out.println("Total Votes: " + totalVotes);
            System.out.println("Algorithm: " + CUT);

            List<AggregatedCourse> courses = CUT.calculate(allRankings);
            int totalQuorum = 0;
            AggregatedCourse last = null;
            for (AggregatedCourse course : courses) {
                totalQuorum += course.getQuorum();
                if (last != null) {
                    assertEquals(last.getPos() + 1, course.getPos());
                    assertTrue(last.getAveragePoints() >= course.getAveragePoints());
                    if (last.getAveragePoints() == course.getAveragePoints()) {
                        assertTrue(last.getTotalPoints() >= course.getTotalPoints());
                        if (last.getTotalPoints() == course.getTotalPoints()) {
                            assertTrue(last.getQuorum() >= course.getQuorum());
                        }
                    }
                    last = course;
                }
                System.out.println(course.getPos() + ": " + course.getComments());
            }
            assertEquals(totalVotes, totalQuorum);
        }
    }

    @Test
    public void calculate_unplayedCourseShouldNotInfluencePointsAverage() {
        createRankings(2);
        addCourse(0, createCourse(BucketColor.Gold.getIndex(), "Course1"));
        addCourse(1, createCourse(BucketColor.Silver.getIndex(), "Course1"));
        addCourse(0, createCourse(BucketColor.Gold.getIndex(), "Course2"));

        List<AggregatedCourse> courses = CUT_Linear.calculate(allRankings);
        assertEquals(1, courses.get(0).getQuorum());
        assertEquals(4.0, courses.get(0).getAveragePoints(), 0.0); // 4/1
        assertEquals(2, courses.get(1).getQuorum());
        assertEquals(3.5, courses.get(1).getAveragePoints(), 0.0); // (4+3)/2
    }

    private void createRankings(int rankingsCount) {
        for (int i = 0; i < rankingsCount; i++) {
            Rankings rankings = new Rankings();
            rankings.setRankedCourses(new ArrayList<>());
            allRankings.add(rankings);
        }
    }

    private RankedCourse createCourse(int bucket) {
        RankedCourse course = new RankedCourse();
        course.setBucket(bucket);
        return course;
    }

    private RankedCourse createCourse(int bucket, String name) {
        RankedCourse course = createCourse(bucket);
        course.setName(name);
        return course;
    }

    private void addCourse(int numOfRanking, RankedCourse course) {
        allRankings.get(numOfRanking).getRankedCourses().add(course);
    }

    private void createRandomRankings(int numRankings) {
        createRankings(numRankings);
        System.out.println("Rankings: " + numRankings);
        for (int i = 0; i < numRankings; i++) {
            int numCourses = wheel.nextInt(50) + 1; // 1-50 RankedCourses per Rankings
            List<RankedCourse> randomCourses = createRandomCourses(numCourses);
            allRankings.get(i).setRankedCourses(randomCourses);
        }
    }

    private List<RankedCourse> createRandomCourses(int numCourses) {
        List<RankedCourse> courses = new ArrayList<>();
        for (int i = 0; i < numCourses; i++) {
            RankedCourse course = new RankedCourse();
            course.setBucket(wheel.nextInt(BucketColor.values().length - 1) + 1);
            course.setLastPlayed(LocalDate.of(wheel.nextInt(10) + 2009, wheel.nextInt(12) + 1, wheel.nextInt(28) + 1));
            course.setName("Course " + wheel.nextInt(numCourses * 2) + 1); // create about 50% overlap on average
            courses.add(course);
        }
        return courses;
    }

}
