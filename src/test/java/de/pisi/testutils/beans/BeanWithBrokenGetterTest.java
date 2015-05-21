package de.pisi.testutils.beans;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;

import org.junit.Assert;
import org.junit.ComparisonFailure;
import org.junit.Test;

public class BeanWithBrokenGetterTest {
    @Test
    public void test() throws IntrospectionException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        try {
            new BeanTester().test(BeanWithBrokenGetter.class);
            Assert.fail("Exception expected");
        } catch (ComparisonFailure cf) {
            Assert.assertEquals(
                    "While testing property 's1': getter must return the data provided by the setter. expected:<[String3]> but was:<[some hard coded test value for BeanWithBrokenGetter]>",
                    cf.getMessage());
        }
    }
}
