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

package org.uberfire.shared.events;

import org.uberfire.component.model.Folder;
import org.uberfire.component.model.Task;

public class TaskDoneEvent {

    private final Task task;
    private final Folder folder;

    public TaskDoneEvent(Folder folder, Task task) {
        this.folder = folder;
       this.task = task;
    }

    public Task getTask() {
        return task;
    }
    
    public Folder getFolder() {
        return folder;
    }
}