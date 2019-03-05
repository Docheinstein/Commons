package org.docheinstein.commons.hierarchy;

import java.io.File;

/**
 * Represents a generic file (file or directory) which has a name
 * and thus a path which must be built through {@link #setChildOf(Node)}
 * by subclasses.
 */
public abstract class Node {

    /** Final name of the node, i.e. the last path component. */
    protected final String mName;

    /** Full path of the file, initially unset,
     * and the build via {@link #setChildOf(Node)} */
    protected String mPath;

    /**
     * Creates a new node for the given name.
     * @param name the name of the name
     */
    protected Node(String name) {
        mName = name;
    }

    /**
     * Returns the name of the node, i.e. the last path component.
     * @return the name of the node
     */
    public String getName() {
        return mName;
    }

    /**
     * Returns the full path of the node.
     * <p>
     * This methods yield significant result only if the node has
     * been normalized via {@link #setChildOf(Node)}.
     * @return the full path of the node
     */
    public String getPath() {
        return mPath;
    }

    /**
     * Returns a new {@link File} for this node.
     * @return a new file for this node
     */
    public File getFile() {
        return new File(getPath());
    }

    @Override
    public String toString() {
        return getPath();
    }

    /**
     * Ensure that this node exists in a way that is decided
     * by the implementation of this class.
     * @return true if the node already exists or has been created successfully,
     *         false otherwise
     */
    public abstract boolean ensureExistence();

    /**
     * Set this node as child of the given node and thus concat the
     * path of the parent to this node's name.
     * @param parent the parent node of this node
     */
    protected void setChildOf(Node parent) {
        mPath = parent.getPath() + mName;
    }
}
