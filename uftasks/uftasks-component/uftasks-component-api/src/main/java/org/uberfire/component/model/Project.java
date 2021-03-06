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

package org.uberfire.component.model;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class Project extends TreeNode<TasksRoot, Folder> {

    private final String name;
    private boolean selected;

    public Project(@MapsTo("name") String name) {
        this.name = name;
        this.selected = false;
    }

    public String getName() {
        return name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    
    public int countDoneTasks() {
        int doneTasks = 0;
        for (Folder folder : getChildren()) {
            for (Task task : folder.getChildren()) {
                if (task.isDone()) {
                    ++doneTasks;
                }
            }
        }
        return doneTasks;
    }
    
    public int countTotalTasks() {
        int totalTasks = 0;
        for (Folder folder : getChildren()) {
            totalTasks += folder.getChildren().size();
        }
        return totalTasks;
    }
}