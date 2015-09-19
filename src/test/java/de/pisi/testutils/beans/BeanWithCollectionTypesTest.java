package de.pisi.testutils.beans;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

public class BeanWithCollectionTypesTest {
    @Test
    public void test() throws IntrospectionException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        new BeanTester().test(BeanWithCollectionTypes.class);
    }
}
