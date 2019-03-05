package org.docheinstein.commons.hierarchy;


import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a {@link Node} that refers to a directory.
 */
public abstract class DNode extends Node {

    /**
     * Creates a directory node for the given name and children as mandatory.
     * @param name the name of the node
     * @param children the children of the node
     * @return the created node
     */
    public static DNode create(String name, Node... children) {
        return create(name, true, children);
    }

    /**
     * Creates a directory node for the given name and children.
     * @param name the name of the node
     * @param mandatory whether this node should be created when
     *                  {@link #ensureExistence()} is called.
     * @param children the children of the node
     * @return the created node
     */
    public static DNode create(String name, boolean mandatory, Node... children) {
        if (mandatory)
            return new Mandatory(name, children);
        return new Optional(name, children);
    }

    /** List of children nodes (files or directories) */
    protected List<Node> mChildren = new ArrayList<>();

    /**
     * Creates a new directory node for the given name and children.
     * @param name the name of this node
     * @param children the children of this node
     */
    protected DNode(String name, Node... children) {
        super(name);
        for (Node n : children)
            addChild(n);
    }

    /**
     * Adds a child to the children of this node.
     * @param node the node to add as child
     */
    public void addChild(Node node) {
        mChildren.add(node);
    }

    /**
     * Removes a child from the children of this node.
     * @param node the child to remove
     */
    public void removeChild(Node node) {
        mChildren.remove(node);
    }

    /**
     * Returns an unmodifiable list of children of this node.
     * @return the children of this node
     */
    public List<Node> getChildren() {
        return Collections.unmodifiableList(mChildren);
    }

    /**
     * Normalizes recursively this node path and the paths
     * of every child.
     * <p>
     * After the call to this method, {@link #getPath()} will
     * give valid result (i.e. the right full path of the node)
     */
    protected void normalize() {
        // Ensure that name is a directory name that ends with '/'
        if (mPath.charAt(mPath.length() - 1) != File.separatorChar)
            mPath += File.separator;

        // Make child path relative to this node
        for (Node child : mChildren) {
            child.setChildOf(this);
            if (child instanceof DNode) {
                ((DNode) child).normalize();
            }
        }
    }

    /**
     * Ensures that this node exists and does the same for every child
     * recursively by calling {@link #ensureExistence()} on each of those.
     * <p>
     * Note that the creation of the hierarchy doesn't imply that every node
     * is created, this actually depends on the implementation of the
     * {@link #ensureExistence()} method.
     * @return whether the creation has been done successfully for every child
     */
    protected boolean createHierarchy() {
        for (Node n : mChildren) {
            if (!n.ensureExistence())
                return false;

            if (n instanceof DNode) {
                DNode dn = (DNode) n;
                if (!dn.createHierarchy())
                    return false;
            }
        }
        return true;
    }

    /**
     * Represents a {@link DNode} which may not exists and thus will
     * do a no-op when {@link #ensureExistence()} is called.
     */
    public static class Optional extends DNode {

        public Optional(String name, Node... children) {
            super(name, children);
        }

        @Override
        public boolean ensureExistence() {
            return true;
        }
    }


    /**
     * Represents a {@link DNode} that should exists and thus
     * will be created when {@link #ensureExistence()} is called.
     */
    public static class Mandatory extends DNode {

        public Mandatory(String name, Node... children) {
            super(name, children);
        }

        @Override
        public boolean ensureExistence() {
            File f = getFile();
            return f.exists() || f.mkdirs();
        }
    }

}
