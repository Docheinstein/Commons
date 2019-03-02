package org.docheinstein.commons.adt;


import java.io.Serializable;

/**
 * Embeds three generic elements
 * @param <A> the first element
 * @param <B> the second element
 * @param <C> the thirds element
 */
public class Triple<A, B, C> implements Serializable {

    private A mFirst;
    private B mSecond;
    private C mThird;

    /**
     * Returns the first element.
     * @return the first element
     */
    public A getFirst() {
        return mFirst;
    }

    /**
     * Returns the second element.
     * @return the second element
     */
    public B getSecond() { return mSecond; }

    /**
     * Returns the thirds element.
     * @return the thirds element
     */
    public C getThird() { return mThird; }

    /**
     * Creates a new triple wrapper for the given elements
     * @param first the first element
     * @param second the second element
     * @param third the third element
     */
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

