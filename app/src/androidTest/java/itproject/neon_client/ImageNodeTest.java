package itproject.neon_client;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.util.Random;

import eu.kudan.kudan.ARImageNode;
import itproject.neon_client.ar.ARSimpleImageNode;

import static org.junit.Assert.*;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class ImageNodeTest
{
    private ARSimpleImageNode arImageNode = new ARSimpleImageNode("arrow.png");
    private Random random = new Random();

    @Test
    public void updateRotationTest()
    {
        float delta = 0.01f;
        for (int i = 0; i < 20; i++)
        {
            arImageNode.updateOrientationValue(random.nextFloat() * 10);
            float orientationMatrixYaw = 0.0f;
            float currentAngleRadians = 0.0f;

            try {
                Field field = arImageNode.getClass().getDeclaredField("orientationMatrixYaw");
                field.setAccessible(true);
                orientationMatrixYaw = (Float) field.get(arImageNode);

                field = arImageNode.getClass().getDeclaredField("currentAngleRadians");
                field.setAccessible(true);
                currentAngleRadians = (Float) field.get(arImageNode);
            } catch (NoSuchFieldException e) {
                System.out.println("Got NoSuchFieldException: " + e.getMessage());
            } catch (IllegalAccessException e) {
                System.out.println("Got IllegalAccessException: " + e.getMessage());
            }

            assertEquals(orientationMatrixYaw, orientationMatrixYaw - currentAngleRadians, delta);
        }
    }
}
