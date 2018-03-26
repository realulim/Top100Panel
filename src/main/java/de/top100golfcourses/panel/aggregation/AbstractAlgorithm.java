package de.top100golfcourses.panel.aggregation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import de.top100golfcourses.panel.entity.AggregatedCourse;
import de.top100golfcourses.panel.entity.RankedCourse;
import de.top100golfcourses.panel.entity.Rankings;

public abstract class AbstractAlgorithm implements Algorithm {

    public abstract int score(RankedCourse course);
    private int numberOfRankings = 1;

    @Override
    public List<AggregatedCourse> calculate(Collection<Rankings> allRankings) {
        numberOfRankings = allRankings.size();
        List<AggregatedCourse> courses = new ArrayList<>();
        for (Rankings rankings : allRankings) {
            for (RankedCourse course : rankings.getRankedCourses()) {
                aggregateCourse(courses, course);
            }
        }
        List<AggregatedCourse> averagedCourses = calculatePointAverages(courses);
        List<AggregatedCourse> sortedCourses = sort(averagedCourses);
        return sortedCourses;
    }

    protected void aggregateCourse(List<AggregatedCourse> aggregatedCourses, RankedCourse courseToAggregate) {
        String courseName = courseToAggregate.getName();
        if (courseName == null) return;
        for (AggregatedCourse course : aggregatedCourses) {
            if (course.getName().equalsIgnoreCase(courseName)) {
                course.addPointsToTotal(score(courseToAggregate));
                course.setQuorum(course.getQuorum() + 1);
                return;
            }
        }
        AggregatedCourse course = new AggregatedCourse();
        course.setTotalPoints(score(courseToAggregate));
        course.setQuorum(1);
        course.setName(courseName);
        course.setBucket(6);
        aggregatedCourses.add(course);
    }

    protected List<AggregatedCourse> calculatePointAverages(List<AggregatedCourse> courses) {
        for (AggregatedCourse course : courses) {
            double averagePoints = course.getTotalPoints() / (double)numberOfRankings;
            Double truncatedDouble = BigDecimal.valueOf(averagePoints).setScale(2, RoundingMode.HALF_UP).doubleValue();
            course.setAveragePoints(truncatedDouble);
            course.setComments("Average: " + course.getAveragePoints() + ", Total: " + course.getTotalPoints() + ", Quorum: " + course.getQuorum());
        }
        return courses;
    }

    protected List<AggregatedCourse> sort(List<AggregatedCourse> coursesToSort) {
        Collections.sort(coursesToSort); // default sorting is by average points, then total points
        int i = 0;
        for (AggregatedCourse course : coursesToSort) {
            course.setPos(++i);
        }
        return coursesToSort;
    }

}
