package de.pisi.testutils.beans;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

public class BeanWithOnlyOnePropertyTest {
    @Test
    public void test() throws IntrospectionException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        new BeanTester().test(BeanWithOnlyOneProperty.class);
    }
}
