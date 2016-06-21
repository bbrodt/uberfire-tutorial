package org.uberfire.shared.events;

import org.uberfire.component.model.Task;

public class TaskChanged {
    Task task;
    
    public TaskChanged(Task task) {
        this.task = task;
    }
    
    public Task getTask() {
        return task;
    }
}
