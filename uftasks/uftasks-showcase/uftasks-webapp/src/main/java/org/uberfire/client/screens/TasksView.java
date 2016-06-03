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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.Badge;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.InlineCheckBox;
import org.gwtbootstrap3.client.ui.InputGroup;
import org.gwtbootstrap3.client.ui.ListGroup;
import org.gwtbootstrap3.client.ui.ListGroupItem;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.ListGroupItemType;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.component.model.Folder;
import org.uberfire.component.model.Task;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

@Dependent
@Templated
public class TasksView extends Composite implements TasksPresenter.View {

    private TasksPresenter presenter;

    @Inject
    @DataField("new-folder")
    Button newFolderButton;

    @Inject
    @DataField("tasks")
    FlowPanel taskPanel;

    public class TaskItem extends ListGroupItem {
        private Task task;

        public TaskItem(Task task) {
            this.task = task;
        }

        public Task getTask() {
            return task;
        }
    }

    @Override
    public void init(final TasksPresenter presenter) {
        this.presenter = presenter;
        this.newFolderButton.setVisible(false);
    }

    @Override
    public void activateNewFolder() {
        newFolderButton.setVisible(true);
    }

    @Override
    public void clearTasks() {
        taskPanel.clear();
    }

    @Override
    public void newFolder(Folder folder) {

        ListGroup folderGroup = GWT.create(ListGroup.class);
        List<Task> taskList = folder.getChildren();
        folderGroup.add(generateFolderTitle(folder.getName(), taskList.size()));
        for (Task task : taskList) {
            folderGroup.add(generateTask(task));
        }
        folderGroup.add(generateNewTask(folder));

        taskPanel.add(folderGroup);
    }

    private ListGroupItem generateNewTask(Folder folder) {
        ListGroupItem newTask = GWT.create(ListGroupItem.class);

        InputGroup inputGroup = GWT.create(InputGroup.class);
        inputGroup.add(createTextBox(folder));

        newTask.add(inputGroup);

        return newTask;
    }

    private TextBox createTextBox(final Folder folder) {
        final TextBox taskText = GWT.create(TextBox.class);
        taskText.setWidth("400");
        taskText.setPlaceholder("New task...");
        taskText.addKeyDownHandler(event -> {
            if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                Task task = new Task(taskText.getText());
                presenter.createTask(folder, task);
            }
        });

        return taskText;
    }

    private ListGroupItem generateFolderTitle(String name, Integer numberOfTasks) {
        ListGroupItem folderTitle = GWT.create(ListGroupItem.class);
        folderTitle.setText(name);
        folderTitle.setType(ListGroupItemType.INFO);

        Badge number = GWT.create(Badge.class);
        number.setText(String.valueOf(numberOfTasks));

        folderTitle.add(number);

        return folderTitle;
    }

    private ListGroupItem generateTask(Task task) {
        TaskItem taskItem = new TaskItem(task);
        taskItem.add(createTaskCheckbox(task));
        taskItem.add(createTaskNotesButton(task));

        return taskItem;
    }

    private InlineCheckBox createTaskCheckbox(Task task) {
        InlineCheckBox checkBox = GWT.create(InlineCheckBox.class);
        checkBox.setText(task.getName());
        checkBox.addClickHandler(event -> presenter.doneTask(task));
        checkBox.setValue(task.isDone());

        return checkBox;
    }

    private Button createTaskNotesButton(Task task) {
        Button button = GWT.create(Button.class);
//        button.setText("Notes...");
        button.setIcon(IconType.EDIT);
        button.setMarginLeft(20.0);
        button.addClickHandler(event -> presenter.showTaskEditor(task));
        return button;
    }
    
    @EventHandler("new-folder")
    public void newFolderClick(ClickEvent event) {
        presenter.showNewFolder();
    }
}