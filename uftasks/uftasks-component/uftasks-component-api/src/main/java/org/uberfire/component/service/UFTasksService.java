package org.uberfire.component.service;


import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.component.model.Task;
import org.uberfire.component.model.TasksRoot;

@Remote
public interface UFTasksService {
    TasksRoot load(String userId);
    String save(TasksRoot tasksRoot, String userId);
    String getFilePath(String userId, Task task);
}
