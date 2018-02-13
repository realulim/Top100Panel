package de.top100golfcourses.panel.entity;

import java.io.Serializable;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.logging.Logger;

import org.dizitart.no2.objects.Id;

public class Rankings implements Serializable {

    @Id
    private String id;

    private String user;
    private String name;
    private List<RankedCourse> rankedCourses;

    public Rankings() { }

    public String getId() {
        return id;
    }

    private void setId() {
        this.id = this.user + ":" + this.name;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
        setId();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        setId();
    }

    public List<RankedCourse> getRankedCourses() {
        return rankedCourses;
    }

    public void insertRankedCourseAt(int index, RankedCourse course) {
        if (index > 0) {
            course.setBucket(rankedCourses.get(index - 1).getBucket());
        }
        rankedCourses.add(index, course);
        if (rankedCourses.size() > index) {
            for (ListIterator<RankedCourse> iter = rankedCourses.listIterator(index + 1); iter.hasNext(); ) {
                iter.next().incrementPos();
            }
        }
    }

    public void deleteRankedCourseAt(int index) {
        rankedCourses.remove(index);
        if (rankedCourses.size() > index) {
            for (ListIterator<RankedCourse> iter = rankedCourses.listIterator(index); iter.hasNext(); ) {
                iter.next().decrementPos();
            }
        }
    }

    public void setRankedCourses(List<RankedCourse> rankedCourses) {
        this.rankedCourses = rankedCourses;
    }

    public RankedCourse getFirstCourseIn(BucketColor color) {
        for (RankedCourse course : rankedCourses) {
            if (course.getBucket() == color.getIndex()) return course;
        }
        return null;
    }

    public int getFirstPosForBucket(int bucket) {
        if (this.rankedCourses.isEmpty()) return 1;
        for (RankedCourse course : this.rankedCourses) {
            if (course.getBucket() == bucket) {
                return course.getPos();
            }
        }
        for (RankedCourse course : this.rankedCourses) {
            if (course.getBucket() > bucket) {
                return course.getPos();
            }
        }
        return this.rankedCourses.size() + 1;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.id);
        hash = 67 * hash + Objects.hashCode(this.user);
        hash = 67 * hash + Objects.hashCode(this.name);
        hash = 67 * hash + Objects.hashCode(this.rankedCourses);
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
        final Rankings other = (Rankings) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.user, other.user)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return Objects.equals(this.rankedCourses, other.rankedCourses);
    }

    @Override
    public String toString() {
        return "Rankings{" + "id=" + id + ", user=" + user + ", name=" + name + ", rankedCourses=" + rankedCourses + '}';
    }

}
