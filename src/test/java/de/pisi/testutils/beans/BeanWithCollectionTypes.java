package de.pisi.testutils.beans;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class BeanWithCollectionTypes {
    private List<Object> list;
    private Map<Object, Object> map;
    private Queue<Object> queue;
    private Set<Object> set;

    public List<Object> getList() {
        return list;
    }

    public Map<Object, Object> getMap() {
        return map;
    }

    public Queue<Object> getQueue() {
        return queue;
    }

    public Set<Object> getSet() {
        return set;
    }

    public void setList(List<Object> newList) {
        list = newList;
    }

    public void setMap(Map<Object, Object> map) {
        this.map = map;
    }

    public void setQueue(Queue<Object> queue) {
        this.queue = queue;
    }

    public void setSet(Set<Object> newSet) {
        set = newSet;
    }

}
