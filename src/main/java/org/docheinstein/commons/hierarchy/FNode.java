package org.docheinstein.commons.hierarchy;

import org.docheinstein.commons.file.FileUtil;
import org.docheinstein.commons.types.StringUtil;

import java.io.File;
import java.io.IOException;

public abstract class FNode extends Node {

    /**
     * Creates a file node for the given name as mandatory with no content.
     * @param name the name of the node
     * @return the created node
     */
    public static FNode create(String name) {
        return create(name, true);
    }

    /**
     * Creates a file node for the given name, optionally mandatory.
     * @param name the name of the node
     * @param mandatory whether this node should be created (with no content) when
     *                  {@link #ensureExistence()} is called.
     * @return the created node
     */
    public static FNode create(String name, boolean mandatory) {
        return create(name, mandatory, null);
    }

    /**
     * Creates a file node for the given name as mandatory with the given content.
     * @param name the name of the node
     * @param content the content that will be written to file when
     *                 {@link #ensureExistence()} is called and it doesn't exist yet
     * @return the create node
     */
    public static FNode create(String name, String content) {
        return create(name, true, content);
    }

    /**
     * Creates a file node for the given name, optionally mandatory with the given content.
     * @param name the name of the node
     * @param mandatory whether this node should be created when
     *                  {@link #ensureExistence()} is called.
     * @param content the content that will be written to file when
     *                 {@link #ensureExistence()} is called and it doesn't exist yet.
     *                Valid only if mandatory is true.
     * @return the created node
     */
    public static FNode create(String name, boolean mandatory, String content) {
        if (mandatory)
            return new Mandatory(name, content);
        return new Optional(name);
    }

    protected FNode(String name) {
        super(name);
    }

    /**
     * Represents a {@link FNode} which may not exists and thus will
     * do a no-op when {@link #ensureExistence()} is called.
     */
    public static class Optional extends FNode {

        public Optional(String name) {
            super(name);
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
    public static class Mandatory extends FNode {

        private final String mContent;

        public Mandatory(String name) {
            this(name, null);
        }

        public Mandatory(String name, String content) {
            super(name);
            mContent = content;
        }

        @Override
        public boolean ensureExistence() {
            File f = getFile();

            if (f.exists())
                return true;

            if (!StringUtil.isValid(mContent)) {
                try {
                    return f.createNewFile();
                } catch (IOException e) {
                    return false;
                }
            }

            return FileUtil.write(f, mContent);
        }
    }
}
