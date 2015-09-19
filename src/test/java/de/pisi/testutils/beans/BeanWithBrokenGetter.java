package de.pisi.testutils.beans;

public class BeanWithBrokenGetter {
    private String s1 = "dummy default";

    public String getS1() {
        return "some hard coded test value for BeanWithBrokenGetter";
    }

    public void setS1(String newS1) {
        this.s1 = newS1;
    }

}
