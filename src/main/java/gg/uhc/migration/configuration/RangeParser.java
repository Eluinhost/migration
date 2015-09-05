package gg.uhc.migration.configuration;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.BoundType;
import com.google.common.collect.Range;

public class RangeParser {

    class RangeBound {

        protected final double bound;
        protected final BoundType boundType;

        public RangeBound(double bound, BoundType type) {
            this.bound = bound;
            this.boundType = type;
        }
    }

    /**
     * Reads a range from a given string
     *
     * @param rangeString the string to read from
     * @return the parsed range
     * @throws IllegalArgumentException on invalid range string
     */
    public Range<Double> readRange(String rangeString) {
        // notation is formatted in style of [x1..x2)
        // split it into left and right bounds
        String[] bounds = rangeString.split("\\.\\.");

        // must be 2 parts
        if (bounds.length != 2) throw new IllegalArgumentException("Missing center .. in region definition");

        Optional<RangeBound> left = readLeftBound(bounds[0]);
        Optional<RangeBound> right = readRightBound(bounds[1]);

        boolean unboundedLeft = !left.isPresent();
        boolean unboundedRight = !right.isPresent();

        if (unboundedLeft && unboundedRight) {
            return Range.all();
        }

        if (unboundedLeft) {
            return Range.upTo(right.get().bound, right.get().boundType);
        }

        if (unboundedRight) {
            return Range.downTo(left.get().bound, left.get().boundType);
        }

        return Range.range(left.get().bound, left.get().boundType, right.get().bound, right.get().boundType);
    }

    protected Optional<BoundType> readLeftBoundType(char type) {
        switch (type) {
            case '[':
                return Optional.of(BoundType.CLOSED);
            case '(':
                return Optional.of(BoundType.OPEN);
            default:
                return Optional.absent();
        }
    }

    protected Optional<BoundType> readRightBoundType(char type) {
        switch (type) {
            case ']':
                return Optional.of(BoundType.CLOSED);
            case ')':
                return Optional.of(BoundType.OPEN);
            default:
                return Optional.absent();
        }
    }

    protected Optional<RangeBound> readLeftBound(String bound) {
        return readBound(bound, true);
    }

    protected Optional<RangeBound> readRightBound(String bound) {
        return readBound(bound, false);
    }

    protected Optional<RangeBound> readBound(String bound, boolean left) {
        // check for open lower bound
        if (left && bound.equalsIgnoreCase("(-Inf")) {
            return Optional.absent();
        }

        // check for open higher bound
        if (!left && bound.equalsIgnoreCase("+Inf)")) {
            return Optional.absent();
        }

        Preconditions.checkArgument(bound.length() > 1, "Must be at least 2 characters long");

        // grab the bound type character
        Optional<BoundType> type = left ? readLeftBoundType(bound.charAt(0)) : readRightBoundType(bound.charAt(bound.length() - 1));
        Preconditions.checkArgument(type.isPresent(), "Invalid bound range type character");

        double range;
        try {
            range = Double.parseDouble(left ? bound.substring(1) : bound.substring(0, bound.length() - 1));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid range number", e);
        }

        return Optional.of(new RangeBound(range, type.get()));
    }
}
