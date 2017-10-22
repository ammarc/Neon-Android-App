package itproject.neon_client;

import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

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
@SmallTest
public class ARTest
{
    private Random random = new Random();

    @Rule
    public ActivityTestRule<NeonARActivity> arActivityTestRule = new
                                        ActivityTestRule<>(NeonARActivity.class);

    @Test
    public void lowPassFilterValuesTest()
    {
        float[] testInput = new float[3];
        float[] testOutput = new float[3];

        // we need to keep this delta high to accommodate for the variance of values
        // we get when we are dealing with lots of noise in the sensor data
        float delta = 3f;


        for (int i = 0; i < 20; i++)
        {
            testInput[0] = random.nextFloat() * 10;
            testInput[1] = random.nextFloat() * 10;
            testInput[2] = random.nextFloat() * 10;

            testOutput[0] = testInput[0] + 0.25f * (testInput[0] - testOutput[0]);
            testOutput[1] = testInput[1] + 0.25f * (testInput[1] - testOutput[1]);
            testOutput[2] = testInput[2] + 0.25f * (testInput[2] - testOutput[2]);

            assertArrayEquals(testInput, arActivityTestRule.getActivity().
                            lowPassFilter(testInput, testOutput), delta);
        }
    }
}
