package de.pisi.testutils.beans;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class BeanWithCollectionTypes {
    private Set<Object> set;
    private List<Object> list;
    private Map<Object, Object> map;

    public Map<Object, Object> getMap() {
        return map;
    }

    public void setMap(Map<Object, Object> map) {
        this.map = map;
    }

    public List<Object> getList() {
        return list;
    }

    public void setList(List<Object> newList) {
        list = newList;
    }

    public Set<Object> getSet() {
        return set;
    }

    public void setSet(Set<Object> newSet) {
        set = newSet;
    }

}
