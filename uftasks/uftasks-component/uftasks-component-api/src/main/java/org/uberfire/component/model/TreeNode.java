package org.uberfire.component.model;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class TreeNode<PARENT extends TreeNode, CHILD extends TreeNode> {
    private PARENT parent;
    private List<CHILD> children;

    public TreeNode() {
        parent = null;
    }

    public String getName() {
        return "noname";
    }

    public PARENT getParent() {
        return (PARENT) parent;
    }

    public void setParent(PARENT parent) {
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
