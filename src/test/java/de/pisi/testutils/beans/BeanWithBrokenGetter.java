package de.pisi.testutils.beans;

public class BeanWithBrokenGetter {
    private String s1;

    public String getS1() {
        return "some hard coded test value for BeanWithBrokenGetter";
    }

    public void setS1(String s1) {
        this.s1 = s1;
    }

}
