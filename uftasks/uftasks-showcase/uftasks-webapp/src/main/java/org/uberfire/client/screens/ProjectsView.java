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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.LinkedGroup;
import org.gwtbootstrap3.client.ui.LinkedGroupItem;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.component.model.Project;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;

@Dependent
@Templated
public class ProjectsView extends Composite implements ProjectsPresenter.View {

    private ProjectsPresenter presenter;
    protected static final String EMPTY = "";

    @Inject
    @DataField("new-project")
    Button newProject;

    @Inject
    @DataField("projects")
    LinkedGroup projectsGroup;

    public class ProjectItem extends LinkedGroupItem {
        Project project;

        public ProjectItem(Project project) {
            this.project = project;
            setText(project.getName());
        }

        public Project getProject() {
            return project;
        }
    }

    @Override
    public void init(ProjectsPresenter presenter) {
        this.presenter = presenter;
    }
    
    @Override
    public void clearProjects() {
        projectsGroup.clear();
    }

    @Override
    public void enableProjectCreation( boolean enabled ) {
        newProject.setEnabled( enabled );
    }

    @Override
    public void addProject(final Project project, final boolean active) {
        final LinkedGroupItem projectItem = createProjectItem(project, active);
        projectsGroup.add(projectItem);
    }

    private ProjectItem createProjectItem(final Project project, final boolean active) {
        final ProjectItem projectItem = new ProjectItem(project);
        projectItem.setActive(active);
        projectItem.addClickHandler((event) -> presenter.selectProject(projectItem.getProject()));
        return projectItem;
    }

    @EventHandler("new-project")
    public void newProject(ClickEvent event) {
        presenter.newProject();
    }
}