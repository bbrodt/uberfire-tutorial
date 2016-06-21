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

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.editors.TaskEditorPresenter;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.screens.popup.NewFolderPresenter;
import org.uberfire.component.model.Folder;
import org.uberfire.component.model.Project;
import org.uberfire.component.model.Task;
import org.uberfire.component.service.UFTasksService;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.shared.events.FolderCreated;
import org.uberfire.shared.events.FolderRemoved;
import org.uberfire.shared.events.ProjectSelectedEvent;
import org.uberfire.shared.events.TaskChanged;
import org.uberfire.shared.events.TaskCreated;
import org.uberfire.shared.events.TaskDone;

import com.google.gwt.core.client.GWT;

@ApplicationScoped
@WorkbenchScreen(identifier = "TasksPresenter")
public class TasksPresenter {
    public interface View extends UberView<TasksPresenter> {

        void activateNewFolder();

        void clearTasks();

        void newFolder(Folder folder);
    }

    @Inject
    private View view;

    @Inject
    private NewFolderPresenter newFolderPresenter;

    @Inject
    private TaskEditorPresenter taskEditorPresenter;
    
    @Inject
    private Event<TaskCreated> taskCreatedEvent;

    @Inject
    private Event<TaskDone> taskDoneEvent;
    
    @Inject
    private Event<FolderCreated> folderCreatedEvent;

    @Inject
    private Event<FolderRemoved> folderRemovedEvent;

    @Inject
    Caller<UFTasksService> ufTasksService;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private User user;

    private Project currentSelectedProject;
    
    private PathPlaceRequest placeRequest;
    
    private Task task;
    
    @WorkbenchPartTitle
    public String getTitle() {
        return "Tasks";
    }

    @WorkbenchPartView
    public UberView<TasksPresenter> getView() {
        return view;
    }

    public void projectSelected(@Observes ProjectSelectedEvent projectSelectedEvent) {
        currentSelectedProject = projectSelectedEvent.getProject();
        selectFolder();
    }

    private void selectFolder() {
        view.activateNewFolder();
        updateView();
    }

    public void showNewFolder() {
        newFolderPresenter.show(this);
    }

    private List<Folder> getFolders() {
        return currentSelectedProject.getChildren();
    }

    private void updateView() {
        view.clearTasks();
        for (final Folder folder : getFolders()) {
            view.newFolder(folder);
        }
    }

    public void newFolder(String folderName) {
        folderCreatedEvent.fire(new FolderCreated(new Folder(folderName)));
        updateView();
    }

    public void removeFolder(Folder folder) {
        folderRemovedEvent.fire(new FolderRemoved(folder));
        updateView();
    }
    
    public void doneTask(Task task) {
        taskDoneEvent.fire(new TaskDone(task.getParent(), task));
        updateView();
    }

    public void createTask(Folder folder, Task task) {
        taskCreatedEvent.fire(new TaskCreated(folder, task));
        updateView();
    }

    public void taskChanged(@Observes TaskChanged taskChanged) {
        task = taskChanged.getTask();
        updateView();
    }

    public void showTaskEditor(final Task task) {
        this.task = task;
        ufTasksService.call(new RemoteCallback<String>() {
            @Override
            public void callback(final String response) {
                if (response!=null) {
                    String filename = response.replaceFirst(".*/", "");
                    Path path = PathFactory.newPath(filename, response);
                    placeRequest = new PathPlaceRequest(path);
                    placeManager.goTo(placeRequest);
                }
                else 
                    GWT.log("UFTasksService is unable to load tasks file");
            }
        }).getFilePath(user.getIdentifier(), task);
    }
    
    public PathPlaceRequest getPlaceRequest() {
        return placeRequest;
    }
    
    public Task getTask() {
        return task;
    }
    
    public void closeTaskEditor(PathPlaceRequest placeRequest) {
        placeManager.closePlace(placeRequest);
    }
}