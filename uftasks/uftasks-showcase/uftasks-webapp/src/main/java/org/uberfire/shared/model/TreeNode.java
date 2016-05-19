package org.uberfire.shared.model;

import java.util.ArrayList;
import java.util.List;

public abstract class TreeNode<PARENT extends TreeNode, CHILD extends TreeNode> {
    private PARENT parent;
    private List<CHILD> children;

    public TreeNode() {
        parent = null;
    }

    abstract public String getName();

    public PARENT getParent() {
        return (PARENT) parent;
    }

    protected void setParent(PARENT parent) {
        this.parent = parent;
    }

    public List<CHILD> getChildren() {
        if (children == null)
            children = new ArrayList<CHILD>();
        return children;
    }

    public void addChild(CHILD child) {
        getChildren().add(child);
        child.setParent(this);
    }

    public void removeChild(TreeNode child) {
        getChildren().remove(child);
        child.setParent(null);
    }
}
