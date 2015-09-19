package de.pisi.testutils.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Assert;

public class BeanTester {

    private int globalCounter = 1;

    private int getNextCounter() {
        globalCounter++;
        return globalCounter;
    }

    public <T> void test(final Class<T> clazz) throws IntrospectionException, InstantiationException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        for (final PropertyDescriptor pd : getAllRWProperties(clazz)) {
            final T newInstance = clazz.newInstance();
            final String propertyUnderTest = pd.getName();
            setAllProperties(clazz, newInstance);
            final Map<String, Object> allOtherPropertiesBefore = getAllPropertiesExcept(clazz, newInstance, pd);
            final Object expected = getUniqueValue(pd.getPropertyType());
            setProperty(clazz, newInstance, pd, expected);
            final Object actual = getProperty(clazz, newInstance, pd);
            Assert.assertEquals("While testing property '" + propertyUnderTest + "': getter must return the data provided by the setter.", expected, actual);
            Map<String, Object> allOtherPropertiesAfter = getAllPropertiesExcept(clazz, newInstance, pd);
            if (!allOtherPropertiesAfter.isEmpty()) {
                // only check if there is anything else
                compareAllProperties(allOtherPropertiesBefore, allOtherPropertiesAfter, propertyUnderTest);
            }
        }
    }

    private void compareAllProperties(final Map<String, Object> expected, final Map<String, Object> actuals, final String propertyUnderTest) {
        Assert.assertThat(propertyUnderTest, actuals.entrySet(), IsIterableContainingInOrder.contains(expected.entrySet().toArray()));
    }

    private Object getUniqueValuePrimitive(final Class<?> propertyType) {
        final Object value;
        if (propertyType.isAssignableFrom(int.class)) {
            value = getNextCounter();
        } else if (propertyType.isAssignableFrom(long.class)) {
            value = Integer.valueOf(getNextCounter()).longValue();
        } else if (propertyType.isAssignableFrom(float.class)) {
            value = new Integer(getNextCounter()).floatValue();
        } else if (propertyType.isAssignableFrom(double.class)) {
            value = new Integer(getNextCounter()).doubleValue();
        } else {
            throw new RuntimeException("unsupported primitive property type: " + propertyType);
        }
        return value;
    }

    private Object getUniqueValueNumber(final Class<?> propertyType) {
        final Object value;
        if (propertyType.isAssignableFrom(Integer.class)) {
            value = getNextCounter();
        } else if (propertyType.isAssignableFrom(Long.class)) {
            value = Integer.valueOf(getNextCounter()).longValue();
        } else if (propertyType.isAssignableFrom(Float.class)) {
            value = new Integer(getNextCounter()).floatValue();
        } else if (propertyType.isAssignableFrom(Double.class)) {
            value = new Integer(getNextCounter()).doubleValue();
        } else {
            throw new RuntimeException("unsupported Number property type: " + propertyType);
        }
        return value;
    }

    // private Object getUniqueValueCollection(final Class<?> propertyType) {
    // final Object value;
    //
    // if (propertyType.isAssignableFrom(HashSet.class)) {
    // HashSet<Object> h = new HashSet<Object>();
    // h.add(new Object());
    // value = h;
    // } else if (propertyType.isAssignableFrom(LinkedList.class)) {
    // LinkedList<Object> l = new LinkedList<Object>();
    // l.add(new Object());
    // value = l;
    // } else if (propertyType.isAssignableFrom(HashMap.class)) {
    // HashMap<Object, Object> l = new HashMap<Object, Object>();
    // l.put(new Object(), new Object());
    // value = l;
    // } else {
    // throw new RuntimeException("unsupported collection property type: " + propertyType);
    // }
    // return value;
    // }

    private Object getUniqueValueEnum(final Class<?> propertyType) {
        final List<?> enums = Arrays.asList(propertyType.getEnumConstants());
        return enums.get(getNextCounter() % enums.size());
    }

    private Object getUniqueValueInterface(final Class<?> propertyType) {
        return Proxy.newProxyInstance(propertyType.getClassLoader(), new Class[] { propertyType }, new InvocationHandler() {
            final String s = getUniqueString();

            @Override
            public Object invoke(final Object arg0, final Method method, final Object[] args) throws Throwable {

                if ("equals".equals(method.getName())) {
                    boolean res = (s.equals(args[0].toString()));
                    return Boolean.valueOf(res);
                }
                if ("toString".equals(method.getName())) {
                    return s;
                }

                throw new RuntimeException("unsupported method: " + method.getName());
            }
        });
    }

    private Object getUniqueValue(final Class<?> propertyType) {

        if (propertyType.isEnum()) {
            return getUniqueValueEnum(propertyType);
        }
        if (propertyType.isPrimitive()) {
            return getUniqueValuePrimitive(propertyType);
        }
        if (Number.class.isAssignableFrom(propertyType)) {
            return getUniqueValueNumber(propertyType);
        }
        if (propertyType.isInterface()) {
            return getUniqueValueInterface(propertyType);
        }
        // // other collection types that are not defined per interface
        // if (Collection.class.isAssignableFrom(propertyType) || Map.class.isAssignableFrom(propertyType)) {
        // return getUniqueValueCollection(propertyType);
        // }
        final Object value;
        if (propertyType.isAssignableFrom(String.class)) {
            value = getUniqueString();
        } else {
            throw new RuntimeException("unsupported property type: " + propertyType);
        }
        return value;
    }

    private String getUniqueString() {
        return "String" + getNextCounter();
    }

    private <T> void setAllProperties(final Class<T> clazz, final T instance) throws IntrospectionException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        for (final PropertyDescriptor pd : getAllRWProperties(clazz)) {
            setProperty(clazz, instance, pd, getUniqueValue(pd.getPropertyType()));
        }
    }

    private <T> void setProperty(final Class<T> clazz, final T instance, final PropertyDescriptor pd, final Object value) throws IntrospectionException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        pd.getWriteMethod().invoke(instance, new Object[] { value });
    }

    // private <T> Map<String, Object> getAllProperties(final Class<T> clazz, final T instance) throws IntrospectionException, IllegalAccessException,
    // IllegalArgumentException, InvocationTargetException {
    // return getAllPropertiesExcept(clazz, instance, null);
    // }

    private <T> Map<String, Object> getAllPropertiesExcept(final Class<T> clazz, final T instance, final PropertyDescriptor except)
            throws IntrospectionException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        final Map<String, Object> res = new TreeMap<String, Object>();
        for (final PropertyDescriptor pd : getAllRWProperties(clazz)) {
            if (null != except && except.getName().equals(pd.getName())) {
                continue;
            }
            final String name = pd.getName();
            res.put(name, getProperty(clazz, instance, pd));
        }
        return res;
    }

    private <T> Object getProperty(final Class<T> clazz, T instance, final PropertyDescriptor pd) throws IntrospectionException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        return pd.getReadMethod().invoke(instance);
    }

    private Collection<PropertyDescriptor> getAllRWProperties(final Class<?> clazz) throws IntrospectionException {
        final BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
        final PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        final Collection<PropertyDescriptor> res = new ArrayList<PropertyDescriptor>();
        for (final PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            if (isPropertyRW(propertyDescriptor))
                res.add(propertyDescriptor);
        }
        return res;
    }

    private boolean isPropertyRW(final PropertyDescriptor propertyDescriptor) {
        return (null != propertyDescriptor.getReadMethod() && null != propertyDescriptor.getWriteMethod());
    }
}
