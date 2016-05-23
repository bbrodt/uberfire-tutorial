/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.client.screens;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.screens.popup.NewProjectPresenter;
import org.uberfire.shared.events.FolderCreated;
import org.uberfire.shared.events.FolderRemoved;
import org.uberfire.shared.events.ProjectSelectedEvent;
import org.uberfire.shared.events.TaskCreated;
import org.uberfire.shared.events.TaskDone;
import org.uberfire.shared.model.Folder;
import org.uberfire.shared.model.Project;
import org.uberfire.shared.model.Task;
import org.uberfire.shared.model.TasksRoot;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

@ApplicationScoped
@WorkbenchScreen(identifier = "ProjectsPresenter")
public class ProjectsPresenter {

    public interface View extends UberView<ProjectsPresenter> {

        void clearProjects();

        void addProject(Project project, boolean selected);
    }
    private final static String DEFAULT_URI = "default://uftasks";

    @Inject
    private View view;

    @Inject
    private NewProjectPresenter newProjectPresenter;

    @Inject
    private Event<ProjectSelectedEvent> projectSelectedEvent;

    @Inject
    private User user;

    @Inject
    protected Caller<VFSService> vfsServices;

    private TasksRoot tasksRoot = new TasksRoot();

    private Project activeProject = null;
    
    @WorkbenchPartTitle
    public String getTitle() {
        return "Projects";
    }

    @WorkbenchPartView
    public UberView<ProjectsPresenter> getView() {
        return view;
    }

    @Produces
    @Named("tasksRoot")
    public TasksRoot tasksRoot() {
        return tasksRoot;
    }

    @PostConstruct
    public void init() {
        loadTasksRoot();
    }

    private void loadTasksRoot() {
        String filename = "tasks.json";
        String uri = DEFAULT_URI + "/" + user.getIdentifier() + "/" + filename;
        Path path = PathFactory.newPath(filename, uri);

        vfsServices.call(new RemoteCallback<String>() {
            @Override
            public void callback(final String response) {
                TasksRoot newRoot = new TasksRoot();
                JSONObject tasksRootJson = JSONParser.parseStrict(response).isObject();
                JSONArray projectsJson = tasksRootJson.get("projects").isArray();
                for (int pi=0; pi<projectsJson.size(); ++pi) {
                    JSONObject pj = projectsJson.get(pi).isObject();
                    Project p = new Project(pj.get("name").isString().stringValue());
                    JSONArray foldersJson = pj.get("folders").isArray();
                    for (int fi=0; fi<foldersJson.size(); ++fi) {
                        JSONObject fj = foldersJson.get(fi).isObject();
                        Folder f = new Folder(fj.get("name").isString().stringValue());
                        JSONArray tasksJson = fj.get("tasks").isArray();
                        for (int ti=0; ti<tasksJson.size(); ++ti) {
                            JSONObject tj = tasksJson.get(ti).isObject();
                            Task t = new Task(tj.get("name").isString().stringValue());
                            t.setDone(tj.get("isDone").isBoolean().booleanValue());
                            f.getChildren().add(t);
                        }
                        p.getChildren().add(f);
                    }
                    newRoot.getProjects().add(p);
                }
                tasksRoot = newRoot;
                updateView();
            }
        }).readAllString(path);
    }
    
    private void saveTasksRoot() {
        JSONObject tasksRootJson = new JSONObject();
        JSONArray projectsJson = new JSONArray();
        int pi = 0;
        for (Project p : tasksRoot.getProjects()) {
            JSONObject pj = new JSONObject();
            pj.put("name", new JSONString(p.getName()));
            
            JSONArray foldersJson = new JSONArray();
            int fi = 0;
            for (Folder f : p.getChildren()) {
                JSONObject fj = new JSONObject();
                fj.put("name", new JSONString(f.getName()));
                foldersJson.set(fi++, fj);
                
                JSONArray tasksJson = new JSONArray();
                int ti = 0;
                for (Task t : f.getChildren()) {
                    JSONObject tj = new JSONObject();
                    tj.put("name", new JSONString(t.getName()));
                    tj.put("isDone", JSONBoolean.getInstance(t.isDone()));
                    tasksJson.set(ti++, tj);
                }
                fj.put("tasks", tasksJson);
            }
            pj.put("folders", foldersJson);
            projectsJson.set(pi++, pj);
        }
        tasksRootJson.put("projects", projectsJson);
        
        final String content = tasksRootJson.toString();
        String filename = "tasks.json";
        String uri = DEFAULT_URI + "/" + user.getIdentifier() + "/" + filename;
        Path path = PathFactory.newPath(filename, uri);

        vfsServices.call(new RemoteCallback<Path>() {
            @Override
            public void callback(final Path response) {
                GWT.log("Write Response: " + response);
            }
        }).write(path, content);
    }
    
    public void taskCreated(@Observes TaskCreated taskCreated) {
        if (activeProject!=null) {
            Folder folder = taskCreated.getFolder();
            Task task = taskCreated.getTask();
            folder.addChild(task);
            saveTasksRoot();
            updateView();
        }
    }

    public void taskDone(@Observes TaskDone taskDone) {
        Task task = taskDone.getTask();
        task.setDone(true);
        saveTasksRoot();
        updateView();
    }

    public void folderCreated(@Observes FolderCreated folderCreated) {
        if (activeProject!=null) {
            activeProject.addChild(folderCreated.getFolder());
            saveTasksRoot();
            updateView();
        }
    }

    public void folderRemoved(@Observes FolderRemoved folderRemoved) {
        if (activeProject!=null) {
            activeProject.removeChild(folderRemoved.getFolder());
            saveTasksRoot();
            updateView();
        }
    }
    
    public void newProject() {
        newProjectPresenter.show(this);
    }

    public void createNewProject(String projectName) {
        tasksRoot.getProjects().add(new Project(projectName));
        saveTasksRoot();
        updateView();
    }

    private void updateView() {
        view.clearProjects();
        for (Project project : tasksRoot.getProjects()) {
            view.addProject(project, project.isSelected());
        }
    }

    public void selectProject(Project project) {
        setActiveProject(project);
        projectSelectedEvent.fire(new ProjectSelectedEvent(project));
    }

    private void setActiveProject(Project project) {
        activeProject = project;
        for (Project p : tasksRoot.getProjects()) {
            if (p == project) {
                p.setSelected(true);
            }
            else {
                p.setSelected(false);
            }
        }
        updateView();
    }
}