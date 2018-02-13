package de.top100golfcourses.panel.da;

import java.util.List;

import de.top100golfcourses.panel.entity.Rankings;

public interface Persistence {

    public static String DB = "top100.db";

    void save(Rankings rankings);

    List<Rankings> findByUser(String user);

    List<Rankings> findAll();

    List<Rankings> findAllSortByUser(String user);

    void delete(Rankings rankings);

}
