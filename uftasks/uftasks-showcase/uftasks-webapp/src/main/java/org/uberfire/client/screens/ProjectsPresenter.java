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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

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

@ApplicationScoped
@WorkbenchScreen(identifier = "ProjectsPresenter")
public class ProjectsPresenter {

    public interface View extends UberView<ProjectsPresenter> {

        void clearProjects();

        void addProject(Project project, boolean selected);
    }

    @Inject
    private View view;

    @Inject
    private NewProjectPresenter newProjectPresenter;

    @Inject
    private Event<ProjectSelectedEvent> projectSelectedEvent;

    @Produces
    @Named("tasksRoot")
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
    public TasksRoot tasksRoot() {
        return tasksRoot;
    }
    
    public void taskCreated(@Observes TaskCreated taskCreated) {
        if (activeProject!=null) {
            Folder folder = taskCreated.getFolder();
            Task task = taskCreated.getTask();
            folder.addChild(task);
            updateView();
        }
    }

    public void taskDone(@Observes TaskDone taskDone) {
        Task task = taskDone.getTask();
        task.setDone(true);
        updateView();
    }

    public void folderCreated(@Observes FolderCreated folderCreated) {
        if (activeProject!=null) {
            activeProject.addChild(folderCreated.getFolder());
            updateView();
        }
    }

    public void folderRemoved(@Observes FolderRemoved folderRemoved) {
        if (activeProject!=null) {
            activeProject.removeChild(folderRemoved.getFolder());
            updateView();
        }
    }
    
    public void newProject() {
        newProjectPresenter.show(this);
    }

    public void createNewProject(String projectName) {
        tasksRoot.getProjects().add(new Project(projectName));
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