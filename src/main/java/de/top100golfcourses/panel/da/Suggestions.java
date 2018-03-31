package de.top100golfcourses.panel.da;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Suggestions {

    private final List<String> allCourses;

    public Suggestions(Path path) {
        allCourses = new ArrayList<>();
        try {
            Files.lines(path).forEach(allCourses::add);
        }
        catch (IOException ex) {
            // graceful degradation: suggestion list is empty, hence no suggestions ever
            Logger.getAnonymousLogger().log(Level.SEVERE, path.toString(), ex);
        }
    }

    public boolean isSuggestion(String value) {
        return (value != null && allCourses.contains(value));
    }

    public List<String> getCourseNameSuggestions(String fragment, int limit) {
        if (fragment == null || fragment.length() < 3) {
            return new ArrayList<>();
        }
        else {
            return allCourses.stream().filter(course -> containsIgnoreCase(course, fragment)).limit(limit).collect(Collectors.toList());
        }
    }

    public static boolean containsIgnoreCase(String str, String searchStr) {
        if (str == null || searchStr == null) {
            return false;
        }

        final int length = searchStr.length();
        if (length == 0) {
            return true;
        }

        for (int i = str.length() - length; i >= 0; i--) {
            if (str.regionMatches(true, i, searchStr, 0, length)) {
                return true;
            }
        }
        return false;
    }

}
