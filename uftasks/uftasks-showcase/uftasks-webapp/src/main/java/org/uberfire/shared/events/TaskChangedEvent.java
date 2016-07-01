package org.uberfire.shared.events;

import org.uberfire.component.model.Task;

public class TaskChangedEvent {
    Task task;
    
    public TaskChangedEvent(Task task) {
        this.task = task;
    }
    
    public Task getTask() {
        return task;
    }
}
