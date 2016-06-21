package org.uberfire.component.backend.server;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.marshalling.client.Marshalling;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.component.model.Task;
import org.uberfire.component.model.TasksRoot;
import org.uberfire.component.service.UFTasksService;

@Service
@ApplicationScoped
public class UFTasksServiceImpl implements UFTasksService {
    private final static String FILENAME = "tasks.json";
    private final static String DEFAULT_URI = "default://uftasks";

    @Inject
    protected VFSService vfsServices;

    @Override
    public TasksRoot load(String userId) {
        String uri = DEFAULT_URI + "/" + userId + "/" + FILENAME;
        Path path = PathFactory.newPath(FILENAME, uri);

        String content = vfsServices.readAllString(path);
        TasksRoot tasksRoot = Marshalling.fromJSON(content, TasksRoot.class);
        return tasksRoot;
    }

    @Override
    public String save(TasksRoot tasksRoot, String userId) {
        String content = Marshalling.toJSON(tasksRoot);
        String uri = DEFAULT_URI + "/" + userId + "/" + FILENAME;
        Path path = PathFactory.newPath(FILENAME, uri);

        path = vfsServices.write(path, content);
        if (path!=null)
            return path.getFileName();
        return null;
    }

    @Override
    public String getFilePath(String userId, Task task) {
        return DEFAULT_URI + "/" + userId + "/" + task.getId() + ".task";
    }
}
