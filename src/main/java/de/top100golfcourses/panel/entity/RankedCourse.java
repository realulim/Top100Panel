package de.top100golfcourses.panel.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class RankedCourse implements Serializable {

    private int pos;
    private int bucket = 1;
    private String name;
    
    @JsonDeserialize(using = LocalDateDeserializer.class)  
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate lastPlayed;

    private String comments;

    public RankedCourse() { }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public void incrementPos() {
        this.pos++;
    }

    public void decrementPos() {
        this.pos--;
    }

    public int getBucket() {
        return bucket;
    }

    public String getBucketColor() {
        return BucketColor.byIndex(bucket).name();
    }

    public void setBucket(int bucket) {
        this.bucket = bucket;
    }

    public void setBucketColor(String color) {
        this.bucket = BucketColor.byName(color).getIndex();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getLastPlayed() {
        return lastPlayed;
    }

    public void setLastPlayed(LocalDate lastPlayed) {
        this.lastPlayed = lastPlayed;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + this.pos;
        hash = 47 * hash + this.bucket;
        hash = 47 * hash + Objects.hashCode(this.name);
        hash = 47 * hash + Objects.hashCode(this.lastPlayed);
        hash = 47 * hash + Objects.hashCode(this.comments);
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
        final RankedCourse other = (RankedCourse) obj;
        if (this.pos != other.pos) {
            return false;
        }
        if (this.bucket != other.bucket) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.comments, other.comments)) {
            return false;
        }
        return Objects.equals(this.lastPlayed, other.lastPlayed);
    }

    @Override
    public String toString() {
        return "RankedCourse{" + "pos=" + pos + ", bucket=" + bucket + ", name=" + name + ", lastPlayed=" + lastPlayed + ", comments=" + comments + '}';
    }

}
