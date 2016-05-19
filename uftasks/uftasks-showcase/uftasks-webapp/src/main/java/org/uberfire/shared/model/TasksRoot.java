package org.uberfire.shared.model;

import java.util.ArrayList;
import java.util.List;

public class TasksRoot {
    private List<Project> projects = new ArrayList<Project>();
    
    public List<Project> getProjects() {
        return projects;
    }
}
