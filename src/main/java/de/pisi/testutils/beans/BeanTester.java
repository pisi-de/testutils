package de.pisi.testutils.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
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

    public <T> void test(Class<T> clazz) throws IntrospectionException, InstantiationException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        T newInstance = clazz.newInstance();
        for (PropertyDescriptor pd : getAllRWProperties(clazz)) {
            String propertyUnderTest = pd.getName();
            setAllProperties(clazz, newInstance);
            Map<String, Object> allOtherPropertiesBefore = getAllPropertiesExcept(clazz, newInstance, pd);
            Object expected = getUniqueValue(pd.getPropertyType());
            setProperty(clazz, newInstance, pd, expected);
            Object actual = getProperty(clazz, newInstance, pd);
            Assert.assertEquals("While testing property '" + propertyUnderTest + "': getter must return the data provided by the setter.", expected, actual);
            Map<String, Object> allOtherPropertiesAfter = getAllPropertiesExcept(clazz, newInstance, pd);
            if (!allOtherPropertiesAfter.isEmpty()) {
                // only check if there is anything else
                compareAllProperties(allOtherPropertiesBefore, allOtherPropertiesAfter, propertyUnderTest);
            }
        }
    }

    private void compareAllProperties(Map<String, Object> expected, Map<String, Object> actuals, String propertyUnderTest) {
        Assert.assertThat(propertyUnderTest, actuals.entrySet(), IsIterableContainingInOrder.contains(expected.entrySet().toArray()));
    }

    private Object getUniqueValue(Class<?> propertyType) {
        final Object value;
        if (propertyType.isAssignableFrom(String.class)) {
            value = "String" + getNextCounter();
        } else if (propertyType.isAssignableFrom(Integer.class)) {
            value = getNextCounter();
        } else if (propertyType.isAssignableFrom(int.class)) {
            value = getNextCounter();
        } else if (propertyType.isAssignableFrom(Long.class)) {
            value = Integer.valueOf(getNextCounter()).longValue();
        } else if (propertyType.isAssignableFrom(long.class)) {
            value = Integer.valueOf(getNextCounter()).longValue();
        } else if (propertyType.isAssignableFrom(Float.class)) {
            value = new Integer(getNextCounter()).floatValue();
        } else if (propertyType.isAssignableFrom(float.class)) {
            value = new Integer(getNextCounter()).floatValue();
        } else if (propertyType.isAssignableFrom(Double.class)) {
            value = new Integer(getNextCounter()).doubleValue();
        } else if (propertyType.isAssignableFrom(double.class)) {
            value = new Integer(getNextCounter()).doubleValue();
        } else if (propertyType.isAssignableFrom(HashSet.class)) {
            HashSet<Object> h = new HashSet<Object>();
            h.add(new Object());
            value = h;
        } else if (propertyType.isAssignableFrom(LinkedList.class)) {
            LinkedList<Object> l = new LinkedList<Object>();
            l.add(new Object());
            value = l;
        } else if (propertyType.isAssignableFrom(HashMap.class)) {
            HashMap<Object, Object> l = new HashMap<Object, Object>();
            l.put(new Object(), new Object());
            value = l;
        } else {
            // value = java.lang.reflect.Proxy.newProxyInstance(propertyType.getClassLoader(), new Class[] { propertyType }, new InvocationHandler() {
            //
            // @Override
            // public Object invoke(Object arg0, Method method, Object[] args) throws Throwable {
            // // TODO Auto-generated method stub
            // if (method.getName().equals("equals")){
            // return method.invoke(arg0, args);
            // }
            //
            // throw new RuntimeException("unsupported method type: " + method.getName());
            // }
            // });
            throw new RuntimeException("unsupported property type: " + propertyType);
        }
        return value;
    }

    private <T> void setAllProperties(Class<T> clazz, T instance) throws IntrospectionException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        for (PropertyDescriptor pd : getAllRWProperties(clazz)) {
            setProperty(clazz, instance, pd, getUniqueValue(pd.getPropertyType()));
        }
    }

    private <T> void setProperty(Class<T> clazz, T instance, PropertyDescriptor pd, Object value) throws IntrospectionException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        pd.getWriteMethod().invoke(instance, new Object[] { value });
    }

    private <T> Map<String, Object> getAllProperties(Class<T> clazz, T instance) throws IntrospectionException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        return getAllPropertiesExcept(clazz, instance, null);
    }

    private <T> Map<String, Object> getAllPropertiesExcept(Class<T> clazz, T instance, PropertyDescriptor except) throws IntrospectionException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Map<String, Object> res = new TreeMap<String, Object>();
        for (PropertyDescriptor pd : getAllRWProperties(clazz)) {
            if (null != except && except.getName().equals(pd.getName())) {
                continue;
            }
            String name = pd.getName();
            res.put(name, getProperty(clazz, instance, pd));
        }
        return res;
    }

    private <T> Object getProperty(Class<T> clazz, T instance, PropertyDescriptor pd) throws IntrospectionException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        return pd.getReadMethod().invoke(instance);
    }

    private Collection<PropertyDescriptor> getAllRWProperties(Class<?> clazz) throws IntrospectionException {
        BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        Collection<PropertyDescriptor> res = new ArrayList<PropertyDescriptor>();
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            if (isPropertyRW(propertyDescriptor))
                res.add(propertyDescriptor);
        }
        return res;
    }

    private boolean isPropertyRW(PropertyDescriptor propertyDescriptor) {
        return (null != propertyDescriptor.getReadMethod() && null != propertyDescriptor.getWriteMethod());
    }
}
