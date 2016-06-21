package org.uberfire.component.model;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class TasksRoot {
    private List<Project> projects = new ArrayList<Project>();
    
    public TasksRoot() {
    }
    
    public List<Project> getProjects() {
        return projects;
    }
    
    public Task getTask(String id) {
        for (Project p : projects) {
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
