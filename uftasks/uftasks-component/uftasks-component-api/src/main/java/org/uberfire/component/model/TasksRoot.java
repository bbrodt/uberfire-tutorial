package org.uberfire.component.model;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class TasksRoot extends TreeNode<TreeNode,Project>{

    public Task getTask(String id) {
        for (Project p : getChildren()) {
            for (Folder f : p.getChildren()) {
                for (Task t : f.getChildren()) {
                    if (t.getId().equals(id)) {
                        return t;
                    }
                }
            }
        }
        return null;
    }
}
