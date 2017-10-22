package itproject.neon_client;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

import junit.framework.TestSuite;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

import itproject.neon_client.activities.NeonARActivity;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@MediumTest
public class ARTests
{
    private Random random = new Random();

    @Rule
    public ActivityTestRule<NeonARActivity> testRule = new ActivityTestRule<>(NeonARActivity.class);

    @Test
    public void lowPassFilterNullTest() throws Exception
    {
        float[] testInput = new float[3];
        float delta = 0.00001f;

        // test for 20 different random arrays
        for (int i = 0; i < 20; i++)
        {
            testInput[0] = random.nextFloat();
            testInput[1] = random.nextFloat();
            testInput[2] = random.nextFloat();

            assertArrayEquals(testInput, testRule.getActivity().lowPassFilter(testInput, null),
                                                                                            delta);
        }
    }

    @Test
    public void lowPassFilterValuesTest() throws Exception
    {
        float[] testInput = new float[3];
        float[] testOutput = new float[3];
        float delta = 2f;


        for (int i = 0; i < 20; i++)
        {
            testInput[0] = random.nextFloat() * 10;
            testInput[1] = random.nextFloat() * 10;
            testInput[2] = random.nextFloat() * 10;

            testOutput[0] = testInput[0] + 0.25f * (testInput[0] - testOutput[0]);
            testOutput[1] = testInput[1] + 0.25f * (testInput[1] - testOutput[1]);
            testOutput[2] = testInput[2] + 0.25f * (testInput[2] - testOutput[2]);

            assertArrayEquals(testInput, testRule.getActivity().lowPassFilter(testInput, testOutput),
                    delta);
        }
    }
}
