package de.top100golfcourses.panel.entity;

public enum BucketColor {

    Gold(1),  Silver(2), Bronze(3), Field(4), DQ(5), Black(6);

    private final int index;

    BucketColor(int ordinalNumber) {
        this.index = ordinalNumber;
    }

    public int getIndex() {
        return index;
    }

    public static BucketColor byIndex(int index) {
        if (BucketColor.values().length >= index) {
            return BucketColor.values()[index - 1];
        }
        else return BucketColor.Black;
    }

    public static BucketColor byName(String name) {
        for (BucketColor color : BucketColor.values()) {
            if (color.name().equals(name)) return color;
        }
        return BucketColor.Black;
    }

}
