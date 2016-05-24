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
}
