package de.top100golfcourses.panel.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.top100golfcourses.panel.aggregation.Algorithm;

/**
 * A list of AggregatedCourses for a particular area. Example: Top 50 Alabama
 * The TopList is assumed to be created from a bunch of Rankings by different 
 * users (a user is a rater or any other source of ranking data).
 */
public class TopList {

    public static String USERNAME = "Calculated";

    private final String name;
    private final Collection<Rankings> rankings;
    private final Algorithm algorithm;
    private final List<AggregatedCourse> aggregatedCourses;

    public TopList(String name, Collection<Rankings> rankings, Algorithm algorithm) {
        this.name = name;
        this.rankings = rankings;
        this.algorithm = algorithm;
        this.aggregatedCourses = algorithm.calculate(rankings);
    }

    public String getName() {
        return name;
    }

    public Collection<Rankings> getRankings() {
        return rankings;
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public List<AggregatedCourse> getAggregatedCourses() {
        return aggregatedCourses;
    }

    /**
     * @return this TopList as Rankings for displaying it in the RankingGrid
     */
    public Rankings toRankings() {
        Rankings aggregatedRankings = new Rankings();
        List<RankedCourse> rankedCourses = new ArrayList<>();
        rankedCourses.addAll(aggregatedCourses);
        aggregatedRankings.setUser(TopList.USERNAME);
        aggregatedRankings.setName(this.name);
        aggregatedRankings.setRankedCourses(rankedCourses);
        return aggregatedRankings;
    }

}
