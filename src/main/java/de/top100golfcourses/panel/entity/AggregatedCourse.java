package de.top100golfcourses.panel.entity;

import java.util.Objects;

/**
 * A virtual course that reflects the aggregation of several RankedCourses.
 */
public class AggregatedCourse extends RankedCourse implements Comparable {

    private Integer totalPoints = 0;
    private Double averagePoints = 0.00;
    private Integer quorum = 0;

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

    public int getQuorum() {
        return quorum;
    }

    public void setQuorum(int quorum) {
        this.quorum = quorum;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof AggregatedCourse) {
            AggregatedCourse other = (AggregatedCourse)o;
            int compared = averagePoints.compareTo(other.getAveragePoints());
            if (compared == 0) compared = totalPoints.compareTo(other.getTotalPoints());
            if (compared == 0) compared = quorum.compareTo(other.getQuorum());
            return -compared;
        }
        else return 0;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.totalPoints);
        hash = 29 * hash + Objects.hashCode(this.averagePoints);
        hash = 29 * hash + this.quorum;
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
        if (this.quorum != other.quorum) {
            return false;
        }
        if (!Objects.equals(this.totalPoints, other.totalPoints)) {
            return false;
        }
        return Objects.equals(this.averagePoints, other.averagePoints);
    }

    @Override
    public String toString() {
        return "\nAggregatedCourse{" + "totalPoints=" + totalPoints + ", averagePoints=" + averagePoints + ", quorum=" + quorum + '}';
    }

}
