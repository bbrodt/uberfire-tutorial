package org.uberfire.client.screens.popup;

import java.util.Date;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.component.model.Task;
import org.uberfire.shared.events.TaskChangedEvent;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.DatePicker;

@Dependent
@Templated
public class NewTaskView extends Composite implements NewTaskPresenter.View {

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

    private NewTaskPresenter presenter;

    private Modal modal;

    private Task task;

    TextBox nameTextBox;
    
    CheckBox doneCheckBox;

    ListBox priorityListBox;
    
    DatePicker dueDatePicker;
    
    @Inject
    @DataField("task-name")
    Anchor taskNameAnchor;
    
    @Inject
    @DataField("task-done")
    Anchor taskDoneAnchor;
    
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

    @Inject
    private Event<TaskChangedEvent> taskChangedEvent;

    @Override
    public void init( NewTaskPresenter presenter ) {
        this.presenter = presenter;

        this.modal = new Modal();
        final ModalBody body = new ModalBody();
        body.add( this );
        modal.add( body );
        
        // add the other widgets
        this.presenter = presenter;
        
        nameTextBox = new TextBox();
        nameTextBox.setWidth("90%");
        taskNameAnchor.add(nameTextBox);
        
        doneCheckBox = new CheckBox();
        doneCheckBox.setText("Done");
        taskDoneAnchor.add(doneCheckBox);
        
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
    public void show(Task task) {
        this.task = task;
        nameTextBox.setText(task.getName());
        doneCheckBox.setValue(task.isDone());
        for (int index=0; index<priorityListBox.getItemCount(); ++index) {
            int v = Integer.parseInt(priorityListBox.getValue(index));
            if (v == task.getPriority()) {
                priorityListBox.setSelectedIndex(index);
                break;
            }
        }
        dueDatePicker.setValue(task.getDueDate(), true);
        dueDatePicker.setCurrentMonth(task.getDueDate());
        modal.show();
    }

    @Override
    public void hide() {
        modal.hide();
    }

    @EventHandler("ok-button")
    public void onOk(ClickEvent event) {
        task.setName(nameTextBox.getText());
        task.setDone(doneCheckBox.getValue());
        task.setPriority(Integer.parseInt(priorityListBox.getSelectedValue()));
        task.setDueDate(dueDatePicker.getValue());
        taskChangedEvent.fire(new TaskChangedEvent(task));
        presenter.close();
    }

    @EventHandler("cancel-button")
    public void onCancel(ClickEvent event) {
        presenter.close();
    }
}
