package org.docheinstein.commons.adt;

/**
 * Wraps a generic object.
 * <p>
 * Can be useful if final reference is needed to an object but it
 * can't actually be final.
 * @param <T> the object to wrap
 */
public class Wrapper<T> {
    private T mT;

    /**
     * Creates an empty wrapper.
     */
    public Wrapper() {}

    /**
     * Creates a wrapper for the given object.
     * @param t the object to wrap
     */
    public Wrapper(T t) {
        set(t);
    }

    /**
     * Sets the underlying object.
     * @param t the object to wrap
     */
    public void set(T t) {
        mT = t;
    }

    /**
     * Returns the underlying object.
     * @return the wrapped object
     */
    public T get() {
        return mT;
    }
}
