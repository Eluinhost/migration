package gg.uhc.migration.collection;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import java.util.NavigableMap;
import java.util.Random;

/**
 * Stores items and retrieves them randomly based on their weights
 */
public class RandomCollection<T> {

    protected final Random random = new Random();
    protected final NavigableMap<Double, T> map = Maps.newTreeMap();

    protected double total = 0;

    public void add(T entry, double weight) {
        Preconditions.checkArgument(weight > 0, "Weight must be a positive integer");

        total += weight;
        map.put(total, entry);
    }

    public T random() {
        double index = random.nextDouble() * total;
        return map.ceilingEntry(index).getValue();
    }
}
