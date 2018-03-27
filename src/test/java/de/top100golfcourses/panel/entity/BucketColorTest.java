package de.top100golfcourses.panel.entity;

import static org.junit.Assert.*;

import org.junit.Test;

public class BucketColorTest {
    
    public BucketColorTest() {
    }
    
    @Test
    public void roundtrips() {
        for (BucketColor CUT : BucketColor.values()) {
            assertEquals(CUT, BucketColor.byIndex(CUT.getIndex()));
            assertEquals(CUT, BucketColor.byName(CUT.name()));
        }
    }

}
