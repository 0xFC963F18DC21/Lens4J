package net.nergi.lens4j;

import static org.junit.jupiter.api.Assertions.*;

import java.util.function.UnaryOperator;
import org.junit.jupiter.api.Test;

class LensTest {
    // Surface-level tests.
    @Test
    void lensShouldAllowSurfaceViewing() {
        // Constants to test for.
        final int v1 = 5;
        final int v2 = 10;

        // Our box.
        final TestBox init = new TestBox(v1, v2);

        // The two lenses, one for each field.
        final Lens<TestBox, Integer> lens1 =
            new Lens<>(TestBox::getContent1, (i, tb) -> new TestBox(i, tb.getContent2()));

        final Lens<TestBox, Integer> lens2 =
            new Lens<>(TestBox::getContent2, (i, tb) -> new TestBox(tb.getContent1(), i));

        // Testing if the lenses can view the correct values.
        assertEquals(v1, lens1.view(init));
        assertEquals(v2, lens2.view(init));
    }

    @Test
    void lensShouldAllowSurfaceModification() {
        // Constants to test for.
        final int v1 = 5;
        final int v2 = 10;

        // Our test modifier function.
        final UnaryOperator<Integer> operator = i -> i + 5;

        // Our box.
        final TestBox init = new TestBox(v1, v2);

        // The two lenses, one for each field.
        final Lens<TestBox, Integer> lens1 =
            new Lens<>(TestBox::getContent1, (i, tb) -> new TestBox(i, tb.getContent2()));

        final Lens<TestBox, Integer> lens2 =
            new Lens<>(TestBox::getContent2, (i, tb) -> new TestBox(tb.getContent1(), i));

        // Some changed boxes.
        final TestBox leftMod = lens1.over(operator, init);
        final TestBox rightMod = lens2.over(operator, init);

        // Testing if the lenses can modify the values correctly.
        assertEquals(operator.apply(v1), leftMod.getContent1());
        assertEquals(operator.apply(v2), rightMod.getContent2());

        // Testing if the box remains unchanged.
        assertEquals(v1, init.getContent1());
        assertEquals(v2, init.getContent2());
    }

    @Test
    void lensShouldAllowSurfaceSetting() {
        // Constants to test for.
        final int v1 = 5;
        final int v2 = 10;
        final int n1 = 15;
        final int n2 = 20;

        // Our box.
        final TestBox init = new TestBox(v1, v2);

        // The two lenses, one for each field.
        final Lens<TestBox, Integer> lens1 =
            new Lens<>(TestBox::getContent1, (i, tb) -> new TestBox(i, tb.getContent2()));

        final Lens<TestBox, Integer> lens2 =
            new Lens<>(TestBox::getContent2, (i, tb) -> new TestBox(tb.getContent1(), i));

        // The new boxes.
        final TestBox leftSet = lens1.set(n1, init);
        final TestBox rightSet = lens2.set(n2, init);

        // Testing if the lenses can """set""" the correct values.
        assertEquals(n1, leftSet.getContent1());
        assertEquals(n2, rightSet.getContent2());

        // Testing if the box remains unchanged.
        assertEquals(v1, init.getContent1());
        assertEquals(v2, init.getContent2());
    }

    // Compositional tests.
    @Test
    void lensShouldAllowNestedViewing() {
        // Constants to test for.
        final int v1 = 5;
        final int v2 = 10;

        // Our box.
        final TestRecBox init = new TestRecBox(v1, v2);

        // Our composite lenses.
        final Lens<TestBox, Integer> lens1 =
            new Lens<>(TestBox::getContent1, (i, tb) -> new TestBox(i, tb.getContent2()));

        final Lens<TestBox, Integer> lens2 =
            new Lens<>(TestBox::getContent2, (i, tb) -> new TestBox(tb.getContent1(), i));

        final Lens<TestRecBox, TestBox> lensRec =
            new Lens<>(TestRecBox::getInnerBox, (tb, trb) -> new TestRecBox(tb));

        final Lens<TestRecBox, Integer> lensRec1 = lensRec.andThen(lens1);
        final Lens<TestRecBox, Integer> lensRec2 = lensRec.andThen(lens2);

        // Testing if the lenses can view the correct values.
        assertEquals(v1, lensRec1.view(init));
        assertEquals(v2, lensRec2.view(init));
    }

    @Test
    void lensShouldAllowNestedModification() {
        // Constants to test for.
        final int v1 = 5;
        final int v2 = 10;

        // Our test modifier function.
        final UnaryOperator<Integer> operator = i -> i + 5;

        // Our box.
        final TestRecBox init = new TestRecBox(v1, v2);

        // Our composite lenses.
        final Lens<TestBox, Integer> lens1 =
            new Lens<>(TestBox::getContent1, (i, tb) -> new TestBox(i, tb.getContent2()));

        final Lens<TestBox, Integer> lens2 =
            new Lens<>(TestBox::getContent2, (i, tb) -> new TestBox(tb.getContent1(), i));

        final Lens<TestRecBox, TestBox> lensRec =
            new Lens<>(TestRecBox::getInnerBox, (tb, trb) -> new TestRecBox(tb));

        final Lens<TestRecBox, Integer> lensRec1 = lensRec.andThen(lens1);
        final Lens<TestRecBox, Integer> lensRec2 = lensRec.andThen(lens2);

        // Some changed boxes.
        final TestRecBox leftMod = lensRec1.over(operator, init);
        final TestRecBox rightMod = lensRec2.over(operator, init);

        // Testing if the lenses can modify the values correctly.
        assertEquals(operator.apply(v1), leftMod.getInnerBox().getContent1());
        assertEquals(operator.apply(v2), rightMod.getInnerBox().getContent2());

        // Testing if the box remains unchanged.
        assertEquals(v1, init.getInnerBox().getContent1());
        assertEquals(v2, init.getInnerBox().getContent2());

    }

    @Test
    void lensShouldAllowNestedSetting() {
        // Constants to test for.
        final int v1 = 5;
        final int v2 = 10;
        final int n1 = 15;
        final int n2 = 20;

        // Our box.
        final TestRecBox init = new TestRecBox(v1, v2);

        // Our composite lenses.
        final Lens<TestBox, Integer> lens1 =
            new Lens<>(TestBox::getContent1, (i, tb) -> new TestBox(i, tb.getContent2()));

        final Lens<TestBox, Integer> lens2 =
            new Lens<>(TestBox::getContent2, (i, tb) -> new TestBox(tb.getContent1(), i));

        final Lens<TestRecBox, TestBox> lensRec =
            new Lens<>(TestRecBox::getInnerBox, (tb, trb) -> new TestRecBox(tb));

        final Lens<TestRecBox, Integer> lensRec1 = lensRec.andThen(lens1);
        final Lens<TestRecBox, Integer> lensRec2 = lensRec.andThen(lens2);

        // The new boxes.
        final TestRecBox leftSet = lensRec1.set(n1, init);
        final TestRecBox rightSet = lensRec2.set(n2, init);

        // Testing if the lenses can """set""" the correct values.
        assertEquals(n1, leftSet.getInnerBox().getContent1());
        assertEquals(n2, rightSet.getInnerBox().getContent2());

        // Testing if the box remains unchanged.
        assertEquals(v1, init.getInnerBox().getContent1());
        assertEquals(v2, init.getInnerBox().getContent2());
    }

    // Our simple container types.
    // We are testing for immutability, so we remove final to see if we messed up.
    @SuppressWarnings("FieldMayBeFinal")
    private static final class TestBox {
        private int content1;
        private int content2;

        public TestBox(int content1, int content2) {
            this.content1 = content1;
            this.content2 = content2;
        }

        public int getContent1() {
            return content1;
        }

        public int getContent2() {
            return content2;
        }
    }

    // See above.
    @SuppressWarnings("FieldMayBeFinal")
    private static final class TestRecBox {
        private TestBox innerBox;

        public TestRecBox(int c1, int c2) {
            this(new TestBox(c1, c2));
        }

        public TestRecBox(TestBox innerBox) {
            this.innerBox = innerBox;
        }

        public TestBox getInnerBox() {
            return innerBox;
        }
    }
}
