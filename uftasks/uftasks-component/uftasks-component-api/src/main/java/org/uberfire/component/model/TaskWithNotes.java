package org.uberfire.component.model;

/**
 * This class extends the TO-DO list Task by adding a "notes" attribute. This
 * class is not intended to be marshalled by the UFTasksService, it only serves
 * to pass the Task and notes data between the client-side Presenter and View
 * classes.
 */
public class TaskWithNotes extends Task {

    private String notes = "";
     
    public TaskWithNotes(Task that) {
        super(that);
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public String getNotes() {
        return notes;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TaskWithNotes) {
            TaskWithNotes other = (TaskWithNotes)obj;
            if (!this.getNotes().equals(other.getNotes()))
                return false;
        }
        return super.equals(obj);
    }
    
    public Task asTask() {
        return new Task(this);
    }
}
