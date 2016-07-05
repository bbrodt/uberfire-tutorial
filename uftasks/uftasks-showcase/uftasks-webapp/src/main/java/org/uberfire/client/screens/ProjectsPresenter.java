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
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.screens.popup.NewProjectPresenter;
import org.uberfire.security.annotations.ResourceCheck;
import org.uberfire.component.model.Folder;
import org.uberfire.component.model.Project;
import org.uberfire.component.model.Task;
import org.uberfire.component.model.TasksRoot;
import org.uberfire.component.service.UFTasksService;
import org.uberfire.shared.events.FolderCreatedEvent;
import org.uberfire.shared.events.FolderRemovedEvent;
import org.uberfire.shared.events.ProjectSelectedEvent;
import org.uberfire.shared.events.TaskChangedEvent;
import org.uberfire.shared.events.TaskCreatedEvent;
import org.uberfire.shared.events.TaskDoneEvent;

import com.google.gwt.core.client.GWT;
import static org.uberfire.shared.authz.ProjectConstants.*;
import static org.uberfire.shared.authz.UFTasksControllerHelper.*;

@ApplicationScoped
@WorkbenchScreen(identifier = "ProjectsPresenter")
public class ProjectsPresenter {

    public interface View extends UberView<ProjectsPresenter> {

        void clearProjects();

        void enableProjectCreation(boolean enabled );

        void addProject(Project project, boolean selected);
    }

    @Inject
    private View view;

    @Inject
    private NewProjectPresenter newProjectPresenter;

    @Inject
    private Event<ProjectSelectedEvent> projectSelectedEvent;

    @Inject
    private User user;

    @Inject
    Caller<UFTasksService> ufTasksService;
    
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
        view.enableProjectCreation( false );

        // The Project security API can be used to check project creation permission
        projects().create().granted( () -> {
            view.enableProjectCreation( true );
        } );
        loadTasksRoot();
    }

    private void loadTasksRoot() {
        ufTasksService.call(new RemoteCallback<TasksRoot>() {
            @Override
            public void callback(final TasksRoot response) {
                if (response!=null)
                    tasksRoot = response;
                else 
                    GWT.log("UFTasksService is unable to load tasks file");
                updateView();
            }
        }).load(user.getIdentifier());
    }
    
    private void saveTasksRoot() {
        ufTasksService.call(new RemoteCallback<String>() {
            @Override
            public void callback(final String response) {
                GWT.log("Write Response: " + response);
            }
        }).save(tasksRoot, user.getIdentifier());
    }
    
    public void taskCreated(@Observes TaskCreatedEvent taskCreated) {
        if (activeProject!=null) {
            Folder folder = taskCreated.getFolder();
            Task task = taskCreated.getTask();
            folder.addChild(task);
            saveTasksRoot();
            updateView();
        }
    }

    public void taskDone(@Observes TaskDoneEvent taskDone) {
        Task task = taskDone.getTask();
        task.setDone(true);
        saveTasksRoot();
        updateView();
    }

    public void taskChanged(@Observes TaskChangedEvent taskChanged) {
        Task changedTask = taskChanged.getTask();
        Task task = tasksRoot.getTask(changedTask.getId());
        if (task!=null) {
            task.set(changedTask);
        }
        saveTasksRoot();
        updateView();
    }

    public void folderCreated(@Observes FolderCreatedEvent folderCreated) {
        if (activeProject!=null) {
            activeProject.addChild(folderCreated.getFolder());
            saveTasksRoot();
            updateView();
        }
    }

    public void folderRemoved(@Observes FolderRemovedEvent folderRemoved) {
        if (activeProject!=null) {
            activeProject.removeChild(folderRemoved.getFolder());
            saveTasksRoot();
            updateView();
        }
    }
    
    public void onCreateGranted() {
        //Project creation allowed
    }

    public void onCreateDenied() {
       //Project creation NOT allowed
    }

    // Creation of projects is restricted
    @ResourceCheck(type=PROJECT, action=CREATE, onGranted="onCreateGranted", onDenied="onCreateDenied")
    public void newProject() {
        newProjectPresenter.show(this);
    }

    // Creation of projects is restricted
    @ResourceCheck(type=PROJECT, action=CREATE, onGranted="onCreateGranted", onDenied="onCreateDenied")
    public void createNewProject(String projectName) {
        tasksRoot.getChildren().add(new Project(projectName));
        saveTasksRoot();
        updateView();
    }

    protected void updateView() {
        view.clearProjects();
        for (Project project : tasksRoot.getChildren()) {
            view.addProject(project, project.isSelected());
        }
    }

    public void selectProject(Project project) {
        setActiveProject(project);
        projectSelectedEvent.fire(new ProjectSelectedEvent(project));
    }

    private void setActiveProject(Project project) {
        activeProject = project;
        for (Project p : tasksRoot.getChildren()) {
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