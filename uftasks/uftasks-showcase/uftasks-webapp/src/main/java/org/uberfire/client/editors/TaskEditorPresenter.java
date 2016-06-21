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

package org.uberfire.client.editors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.screens.TasksPresenter;
import org.uberfire.component.model.TaskWithNotes;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.shared.events.TaskChanged;
import org.uberfire.workbench.model.menu.Menus;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.IsWidget;

@Dependent
@WorkbenchEditor(identifier = "TaskEditor", supportedTypes = { TaskResourceType.class })
public class TaskEditorPresenter {

    public interface View extends UberView<TaskEditorPresenter> {
        IsWidget getTitleWidget();
        void setContent(final TaskWithNotes content);
        TaskWithNotes getContent();
        boolean isDirty();
    }

    @Inject
    private View view;

    @Inject
    private Event<TaskChanged> taskChangedEvent;

    @Inject
    private Caller<VFSService> vfsServices;
    
    @Inject
    private TasksPresenter tasksPresenter;
    
    // the task being edited
    private TaskWithNotes taskWithNotes;

    private PathPlaceRequest placeRequest;
    
    @PostConstruct
    public void setup() {
        view.init( this );
    }

    @OnStartup
    public void onStartup( final ObservablePath path,
                           final PlaceRequest place ) {
        taskWithNotes = new TaskWithNotes(tasksPresenter.getTask());
        placeRequest = tasksPresenter.getPlaceRequest();
        loadTaskNotes(path);
    }
    
    private void loadTaskNotes(ObservablePath path) {
        vfsServices.call(
            new RemoteCallback<String>() {
                @Override
                public void callback(String response) {
                    if ( response != null ) {
                        taskWithNotes.setNotes(response);
                    }
                    view.setContent(taskWithNotes);
                }
            }, new ErrorCallback<Object>() {
    
                @Override
                public boolean error(Object message, Throwable throwable) {
                    view.setContent(taskWithNotes);
                    return false;
                }
            }
        ).readAllString( path );
    }
    
    private void saveTaskNotes(ObservablePath path, String notes) {
        vfsServices.call(
                new RemoteCallback<Path>() {
                    @Override
                    public void callback(Path response) {
                        if ( response != null ) {
                            GWT.log("saveTaskNotes SUCCEEDED: "+response);
                        }
                    }
                }, new ErrorCallback<Object>() {
        
                    @Override
                    public boolean error(Object message, Throwable throwable) {
                        GWT.log("saveTaskNotes FAILED: "+message);
                        return false;
                    }
                }
            ).write(path, notes);
    }
    
    @WorkbenchPartTitle
    public String getTitleText() {
        return "Task Editor";
    }

    @WorkbenchPartTitleDecoration
    public IsWidget getTitle() {
        return view.getTitleWidget();
    }

    @WorkbenchPartView
    public IsWidget asWidget() {
        return view;
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return null;
    }

    public void save() {
        if (view.isDirty()) {
            TaskWithNotes changedTask = view.getContent();
            if (!taskWithNotes.getNotes().equals(changedTask.getNotes()))
                saveTaskNotes(placeRequest.getPath(), changedTask.getNotes());
            if (!changedTask.asTask().equals(taskWithNotes.asTask()))
                taskChangedEvent.fire(new TaskChanged(changedTask));
        }
    }
    
    public void close() {
        tasksPresenter.closeTaskEditor(placeRequest);
    }
}