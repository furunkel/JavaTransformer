package javaaug.transformations;

import javaaug.TransformationTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ShuffleVariableNamesTest extends TransformationTest {
    @Test
    public void testSpectrum() throws Exception {
        testSpectrum("test", Arrays.asList(new ShuffleVariableNames.Builder()), (before, after) -> {
            System.out.println(before.toString());
            System.out.println(after.toString());
        });
    }

    @Test
    public void testSpectrum2() throws Exception {
        testSpectrum("test", Arrays.asList(new SwapIfElseBranches.Builder(), new LowerNegation.Builder(), new ShuffleVariableNames.Builder()), (before, after) -> {
            System.out.println(before.toString());
            System.out.println(after.toString());
        });
    }
}
