package de.pisi.testutils.beans;

public class BeanWithInterfaceProperty {
    public static interface Intf {

    }

    private Intf intf;

    public Intf getIntf() {
        return intf;
    }

    public void setIntf(final Intf newIntf) {
        intf = newIntf;
    }

}
