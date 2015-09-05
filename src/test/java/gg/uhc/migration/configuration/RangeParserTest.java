package gg.uhc.migration.configuration;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(PowerMockRunner.class)
public class RangeParserTest {

    protected RangeParser parser;

    @Before
    public void onStartUp() {
        parser = new RangeParser();
    }

    @Test
    public void test_read_all_valid() {
        Range range = parser.readRange("(-Inf..+Inf)");

        assertThat(range.hasLowerBound()).isFalse();
        assertThat(range.hasUpperBound()).isFalse();

        range = parser.readRange("(-Inf..-10)");

        assertThat(range.hasLowerBound()).isFalse();

        assertThat(range.hasUpperBound()).isTrue();
        assertThat(range.upperEndpoint()).isEqualTo(-10D);
        assertThat(range.upperBoundType()).isEqualTo(BoundType.OPEN);

        range = parser.readRange("(-Inf..-10]");

        assertThat(range.hasLowerBound()).isFalse();

        assertThat(range.hasUpperBound()).isTrue();
        assertThat(range.upperBoundType()).isEqualTo(BoundType.CLOSED);
        assertThat(range.upperEndpoint()).isEqualTo(-10D);

        range = parser.readRange("(-Inf..0]");

        assertThat(range.hasLowerBound()).isFalse();

        assertThat(range.hasUpperBound()).isTrue();
        assertThat(range.upperBoundType()).isEqualTo(BoundType.CLOSED);
        assertThat(range.upperEndpoint()).isEqualTo(0D);

        range = parser.readRange("(-Inf..100)");

        assertThat(range.hasLowerBound()).isFalse();

        assertThat(range.hasUpperBound()).isTrue();
        assertThat(range.upperBoundType()).isEqualTo(BoundType.OPEN);
        assertThat(range.upperEndpoint()).isEqualTo(100D);

        range = parser.readRange("(-100..+Inf)");

        assertThat(range.hasLowerBound()).isTrue();
        assertThat(range.lowerEndpoint()).isEqualTo(-100D);
        assertThat(range.lowerBoundType()).isEqualTo(BoundType.OPEN);

        assertThat(range.hasUpperBound()).isFalse();

        range = parser.readRange("[-0..+Inf)");

        assertThat(range.hasLowerBound()).isTrue();
        assertThat(range.lowerEndpoint()).isEqualTo(-0D);
        assertThat(range.lowerBoundType()).isEqualTo(BoundType.CLOSED);

        assertThat(range.hasUpperBound()).isFalse();

        range = parser.readRange("[300..+Inf)");

        assertThat(range.hasLowerBound()).isTrue();
        assertThat(range.lowerEndpoint()).isEqualTo(300D);
        assertThat(range.lowerBoundType()).isEqualTo(BoundType.CLOSED);

        assertThat(range.hasUpperBound()).isFalse();

        range = parser.readRange("[-100..-30)");

        assertThat(range.hasLowerBound()).isTrue();
        assertThat(range.lowerEndpoint()).isEqualTo(-100D);
        assertThat(range.lowerBoundType()).isEqualTo(BoundType.CLOSED);

        assertThat(range.hasUpperBound()).isTrue();
        assertThat(range.upperEndpoint()).isEqualTo(-30D);
        assertThat(range.upperBoundType()).isEqualTo(BoundType.OPEN);

        range = parser.readRange("(-20..200]");

        assertThat(range.hasLowerBound()).isTrue();
        assertThat(range.lowerEndpoint()).isEqualTo(-20D);
        assertThat(range.lowerBoundType()).isEqualTo(BoundType.OPEN);

        assertThat(range.hasUpperBound()).isTrue();
        assertThat(range.upperEndpoint()).isEqualTo(200D);
        assertThat(range.upperBoundType()).isEqualTo(BoundType.CLOSED);

        range = parser.readRange("[0..0]");

        assertThat(range.hasLowerBound()).isTrue();
        assertThat(range.lowerEndpoint()).isEqualTo(0D);
        assertThat(range.lowerBoundType()).isEqualTo(BoundType.CLOSED);

        assertThat(range.hasUpperBound()).isTrue();
        assertThat(range.upperEndpoint()).isEqualTo(0D);
        assertThat(range.upperBoundType()).isEqualTo(BoundType.CLOSED);

        range = parser.readRange("[-20.23..223.92]");

        assertThat(range.hasLowerBound()).isTrue();
        assertThat(range.lowerEndpoint()).isEqualTo(-20.23D);
        assertThat(range.lowerBoundType()).isEqualTo(BoundType.CLOSED);

        assertThat(range.hasUpperBound()).isTrue();
        assertThat(range.upperEndpoint()).isEqualTo(223.92D);
        assertThat(range.upperBoundType()).isEqualTo(BoundType.CLOSED);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_invalid_bound_singleton() {
        parser.readRange("(0..0)");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_invalid_bound_inf_lower() {
        parser.readRange("[-Inf..0)");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_invalid_bound_inf_upper() {
        parser.readRange("(0..+Inf]");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_invalid_inf_upper() {
        parser.readRange("(0..-Inf)");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_invalid_inf_lower() {
        parser.readRange("(+Inf..0)");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_invalid_number_upper() {
        parser.readRange("(0..X)");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_invalid_number_lower() {
        parser.readRange("(X..0)");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_no_dots() {
        parser.readRange("(00)");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_invalid_bound_char_left() {
        parser.readRange("/0..0)");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_invalid_bound_char_right() {
        parser.readRange("(0..0/");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_invalid_bound_size_left() {
        parser.readRange("(..0)");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_invalid_bound_size_right() {
        parser.readRange("(0..)");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_invalid_bound_crossing() {
        parser.readRange("(1..0)");
    }
}
