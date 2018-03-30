package de.top100golfcourses.panel.entity;

import java.util.Objects;

/**
 * A virtual course that reflects the aggregation of several RankedCourses.
 */
public class AggregatedCourse extends RankedCourse implements Comparable {

    private Integer totalPoints = 0; // total number of points accumulated over all votes
    private Double averagePoints = 0.00; // average number of points per vote
    private Integer votes = 0; // how many votes were cast for this course

    public int getTotalPoints() {
        return totalPoints;
    }

    public void addPointsToTotal(int pointsToAdd) {
        this.totalPoints += pointsToAdd;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public double getAveragePoints() {
        return averagePoints;
    }

    public void setAveragePoints(double averagePoints) {
        this.averagePoints = averagePoints;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof AggregatedCourse) {
            AggregatedCourse other = (AggregatedCourse)o;
            int compared = averagePoints.compareTo(other.getAveragePoints());
            if (compared == 0) compared = totalPoints.compareTo(other.getTotalPoints());
            if (compared == 0) compared = votes.compareTo(other.getVotes());
            return -compared;
        }
        else return 0;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.totalPoints);
        hash = 29 * hash + Objects.hashCode(this.averagePoints);
        hash = 29 * hash + this.votes;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AggregatedCourse other = (AggregatedCourse) obj;
        if (!Objects.equals(this.totalPoints, other.totalPoints)) {
            return false;
        }
        if (!Objects.equals(this.averagePoints, other.averagePoints)) {
            return false;
        }
        return Objects.equals(this.votes, other.votes);
    }


    @Override
    public String toString() {
        return "\nAggregatedCourse{" + "totalPoints=" + totalPoints + ", averagePoints=" + averagePoints + ", votes=" + votes + '}';
    }

}
