package com.android.hluncher;

import com.android.hluncher.Task.TaskOrder;
import java.util.Set;

public class TaskTree {

    private Task root;
    private Checker checker;
    private TaskOrder taskOrder;

    private TaskTree() {
        root = new Task() {
            @Override
            public void runTask() {

            }
        };
        checker = new Checker();
        taskOrder = new TaskOrder() {
            @Override
            public void beforeTask(Task task) {

            }

            @Override
            public void afterTask(Task task) {
                Set<Task> next = task.next();
                for (Task t : next) {
                    t.pre().remove(task);
                    if (t.pre().size() == 0) {
                        TaskPool.execute(t);
                    }
                }
            }
        };
    }

    public TaskTree addTaskLine(TaskLine taskLine) {
        root.addNext(taskLine.getRoot());
        return this;
    }

    public TaskTree addTask(Task task) {
        root.addNext(task);
        return this;
    }

    public void execute() throws RuntimeException {
        if (checker.checkRound(root, taskOrder)) {
            TaskPool.execute(root);
        } else {
            throw new RuntimeException("出现了任务环！");
        }
    }

    public static TaskTree create() {
        return new TaskTree();
    }

}
