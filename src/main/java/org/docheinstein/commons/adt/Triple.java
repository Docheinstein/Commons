package org.docheinstein.commons.adt;


import java.io.Serializable;

public class Triple<A, B, C> implements Serializable {

    private A mFirst;
    private B mSecond;
    private C mThird;

    public A getFirst() {
        return mFirst;
    }
    public B getSecond() { return mSecond; }
    public C getThird() { return mThird; }

    public Triple(A first, B second, C third) {
        mFirst = first;
        mSecond = second;
        mThird = third;
    }

    @Override
    public String toString() {
        return "(" + mFirst + ", " + mSecond + ", " + mThird + ")";
    }
}

