package gg.uhc.migration;

import com.google.common.collect.Range;

public class Area {

    protected final Range<Double> xRange;
    protected final Range<Double> zRange;
    protected final String announce;
    protected final int weight;

    public Area(Range<Double> xRange, Range<Double> zRange, String announce, int weight) {
        this.xRange = xRange;
        this.zRange = zRange;
        this.weight = weight;
        this.announce = announce;
    }

    public int getWeight() {
        return weight;
    }

    public String getAnnounce() {
        return announce;
    }

    public boolean inside(double x, double z) {
        return xRange.contains(x) && zRange.contains(z);
    }
}
