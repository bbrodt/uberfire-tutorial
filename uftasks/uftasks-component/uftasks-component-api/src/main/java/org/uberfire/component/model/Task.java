package org.uberfire.component.model;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class Task extends TreeNode<Folder, TreeNode> {
    private String name;
    private boolean done;

    public Task(@MapsTo("name") String name) {
        this.name = name;
        this.done = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
