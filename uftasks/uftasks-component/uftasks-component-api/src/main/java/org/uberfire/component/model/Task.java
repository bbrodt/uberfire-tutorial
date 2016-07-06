package org.uberfire.component.model;

import java.util.Date;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * This class implements a TO-DO list Task.
 * 
 * We want the Task to have an arbitrarily long Rich Text "notes" attribute
 * which the user can view and update inside a Task Editor (a @WorkbenchEditor),
 * but we don't want to incur the penalty of having to marshal this potentially
 * large amount of data across the wire for every task in our TO-DO list.
 * Instead we only want to load the notes when the user opens the Task Editor.
 * This is accomplished using a separate text file that is linked to a Task
 * instance using a unique ID for the file name. The Task's "id" field will be
 * the name of the text file containing the "notes" data.
 */
@Portable
public class Task extends TreeNode<Folder, TreeNode> {
    private String name;
    private boolean done;
    private int priority;
    private Date dueDate;
    private String id;

    public Task(@MapsTo("name") String name) {
        this.name = name;
        this.done = false;
        priority = 0;
        dueDate = new Date();
        
        // Yes we should probably use a UUID here to ensure uniqueness,
        // but this is good enough for our purposes...
        this.id = Long.toString(System.currentTimeMillis());
    }
    
    public Task(Task that) {
        set(that);
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Task) {
            Task that = (Task)obj;
            if (!this.getName().equals(that.getName()))
                return false;
            if (this.isDone() != that.isDone())
                return false;
            if (this.getPriority() != that.getPriority())
                return false;
            if (!this.getDueDate().equals(that.getDueDate()))
                return false;
            return true;
        }
        return super.equals(obj);
    }

    public void set(Task that) {
        this.name = that.name;
        this.done = that.done;
        this.priority = that.priority;
        this.dueDate = that.dueDate;
        this.id = that.id;
    }
}
