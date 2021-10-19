package com.sinthoras.visualprospecting.task;

import java.util.ArrayList;
import java.util.List;

public class TaskManager {

    private final List<ITask> tasks = new ArrayList<>();

    public void addTask(ITask task) {
        tasks.add(task);
    }

    public void onTick() {
        tasks.removeIf(ITask::process);
    }
}
