package org.docheinstein.commons.hierarchy;

import org.docheinstein.commons.adt.Pair;

import java.util.Stack;

/**
 * Is exactly as a {@link DNode} but should be used to represents
 * the root node of the hierarchy.
 * <p>
 * Moreover call the {@link #ensureExistence()} method on this object
 * creates all the underlying hierarchy recursively.
 */
public class Hierarchy extends DNode {

    /**
     * Creates a new hierarchy for the given root path and nodes.
     * <p>
     * The hierarchy is already normalized.
     * @param root the root path
     * @param children the children of the root
     * @return the created directory node
     */
    public static Hierarchy create(String root, Node... children) {
        Hierarchy h = new Hierarchy(root, children);
        h.normalize();
        return h;
    }

    /**
     * Creates an non normalized hierarchy for the given root path and nodes.
     * @param root the root path
     * @param children the children of the root
     */
    private Hierarchy(String root, Node... children) {
        super(root, children);
        mPath = root;
    }

    @Override
    public boolean ensureExistence() {
        return createHierarchy();
    }

    /**
     * Returns a (fancy) tree representation of the hierarchy as string.
     * @return the tree representation of this hierarchy
     */
    public String toTree() {
        StringBuilder sb = new StringBuilder();

        // (node, indentation)
        Stack<Pair<Node, Integer>> nodes = new Stack<>();
        nodes.push(new Pair<>(this, 0));

        Pair<Node, Integer> currentNodeItem;

        while (!nodes.empty()) {
            // Retrieve a node
            currentNodeItem = nodes.pop();
            Node currentNode = currentNodeItem.getKey();
            int currentIndent = currentNodeItem.getValue();

            // Indent
            for (int i = 0; i < currentIndent; i++)
                sb.append("| ");

            // Print the name
            sb.append(currentNode.getName());
            sb.append("\n");

            // Push all the child
            if (currentNode instanceof DNode) {
                for (Node child : ((DNode) currentNode).getChildren())
                    nodes.push(new Pair<>(child, currentIndent + 1));
            }
        }

        return sb.toString();
    }
}
