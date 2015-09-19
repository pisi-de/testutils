package de.pisi.testutils.beans;

public class BeanWithEnumProperty {
    public static enum Enumm {
        A, B, C
    }

    private Enumm enumm;

    public Enumm getEnumm() {
        return enumm;
    }

    public void setEnumm(final Enumm newEnumm) {
        enumm = newEnumm;
    }
}
