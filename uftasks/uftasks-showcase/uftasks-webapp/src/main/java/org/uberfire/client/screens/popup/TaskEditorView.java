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

package org.uberfire.client.screens.popup;

import java.util.Date;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RichTextArea;
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

    private Modal modal;
    
    RichTextArea notesTextArea;
   
    ListBox priorityListBox;
    
    DatePicker dueDatePicker;
    
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

        this.modal = new Modal();
        final ModalBody body = new ModalBody();
        body.add( this );
        modal.add( body );
        
        // add the other widgets
        notesTextArea = new RichTextArea(); 
        notesTextArea.setHeight("200");
        notesTextArea.setWidth("100%");
        notesTextArea.setHTML("<b>Hello World!</b><br/>Uberfire is cool");
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
    public void show() {
        modal.show();
    }

    @Override
    public void hide() {
        modal.hide();
    }

    @EventHandler("ok-button")
    public void onOk( ClickEvent event ) {
        presenter.close();
    }

    @EventHandler("cancel-button")
    public void onCancel( ClickEvent event ) {
        presenter.close();
    }
}