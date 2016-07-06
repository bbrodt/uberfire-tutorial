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

import java.util.Date;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.Button;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.component.model.TaskWithNotes;
import org.uberfire.ext.editor.commons.client.EditorTitle;
import org.uberfire.ext.editor.commons.client.resources.i18n.CommonConstants;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.DatePicker;

@Dependent
@Templated
public class TaskEditorView extends Composite
        implements TaskEditorPresenter.View {
    
    public static class DueDateValueChangeHandler implements ValueChangeHandler<Date> {
        private final Label text;

        public DueDateValueChangeHandler(Label text) {
            this.text = text;
        }

        public void onValueChange(ValueChangeEvent<Date> event) {
            Date date = event.getValue();
            String dateString = DateTimeFormat.getFormat(PredefinedFormat.DATE_MEDIUM).format(date);
            text.setText(dateString);
        }
    }

    private TaskEditorPresenter presenter;

    private TaskWithNotes taskWithNotes;
    
    protected EditorTitle title = new EditorTitle();

    TextBox nameTextBox;
    
    CheckBox doneCheckBox;
    
    RichTextArea notesTextArea;
   
    ListBox priorityListBox;
    
    DatePicker dueDatePicker;
    
    @Inject
    @DataField("task-name")
    Anchor taskNameAnchor;
    
    @Inject
    @DataField("task-done")
    Anchor taskDoneAnchor;
    
    @Inject
    @DataField("task-notes")
    Anchor taskNotesAnchor;
    
    @Inject
    @DataField("task-priority")
    Anchor taskPriorityAnchor;
    
    @Inject
    @DataField("task-due-date")
    Anchor taskDueDateAnchor;

    @Inject
    @DataField("task-due-date-label")
    Label dueDateLabel;
    
    @Inject
    @DataField("ok-button")
    Button okButton;

    @Inject
    @DataField("cancel-button")
    Button cancelButton;
            
    @Override
    public void init( TaskEditorPresenter presenter ) {
        this.presenter = presenter;
        
        nameTextBox = new TextBox();
        nameTextBox.setWidth("90%");
        taskNameAnchor.add(nameTextBox);
        
        doneCheckBox = new CheckBox();
        doneCheckBox.setText("Done");
        taskDoneAnchor.add(doneCheckBox);
        
        notesTextArea = new RichTextArea(); 
        notesTextArea.setHeight("80%");
        notesTextArea.setWidth("90%");
        taskNotesAnchor.add(notesTextArea);
        
        priorityListBox = new ListBox();
        priorityListBox.setMultipleSelect(false);
        priorityListBox.addItem("Medium", "2");
        priorityListBox.addItem("High", "3");
        priorityListBox.addItem("Low", "1");
        priorityListBox.addItem("Screw it, I don't time for this crap!", "0");
        taskPriorityAnchor.add(priorityListBox);
        
        dueDatePicker = new DatePicker();
        dueDatePicker.setYearArrowsVisible(true);
        dueDatePicker.setYearAndMonthDropdownVisible(true);
        dueDatePicker.setVisibleYearCount(51);
        dueDatePicker.addValueChangeHandler(new DueDateValueChangeHandler(dueDateLabel));
        dueDatePicker.setValue(new Date(), true);
        taskDueDateAnchor.add(dueDatePicker);
    }

    @Override
    public EditorTitle getTitleWidget() {
        return title;
    }

    @EventHandler("ok-button")
    public void onOk( ClickEvent event ) {
        presenter.save();
        presenter.close();
    }

    @EventHandler("cancel-button")
    public void onCancel( ClickEvent event ) {
        if (confirmClose())
            presenter.close();
    }

    private boolean confirmClose() {
        if (isDirty()) {
            if (!Window.confirm(CommonConstants.INSTANCE.DiscardUnsavedData()))
                return false;
        }
        return true;
    }

    @Override
    public void setContent(TaskWithNotes content) {
        taskWithNotes = content;
        nameTextBox.setText(taskWithNotes.getName());
        doneCheckBox.setValue(taskWithNotes.isDone());
        notesTextArea.setHTML(taskWithNotes.getNotes());
        for (int index=0; index<priorityListBox.getItemCount(); ++index) {
            int v = Integer.parseInt(priorityListBox.getValue(index));
            if (v == taskWithNotes.getPriority()) {
                priorityListBox.setSelectedIndex(index);
                break;
            }
        }
        dueDatePicker.setValue(taskWithNotes.getDueDate(), true);
        dueDatePicker.setCurrentMonth(taskWithNotes.getDueDate());
    }

    @Override
    public TaskWithNotes getContent() {
        TaskWithNotes content = new TaskWithNotes(taskWithNotes);
        content.setName(nameTextBox.getText());
        content.setDone(doneCheckBox.getValue());
        content.setNotes(notesTextArea.getHTML());
        content.setPriority(Integer.parseInt(priorityListBox.getSelectedValue()));
        content.setDueDate(dueDatePicker.getValue());
        return content;
    }

    @Override
    public boolean isDirty() {
        // check if anything has changed
        return !getContent().equals(taskWithNotes);
    }
}