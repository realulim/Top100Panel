package de.top100golfcourses.panel.aggregation;

import java.util.Collection;
import java.util.List;

import de.top100golfcourses.panel.entity.AggregatedCourse;
import de.top100golfcourses.panel.entity.Rankings;

public interface Algorithm {

    public List<AggregatedCourse> calculate(Collection<Rankings> rankings);

}
