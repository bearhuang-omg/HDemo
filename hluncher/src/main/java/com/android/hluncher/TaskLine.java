package com.android.hluncher;

import com.android.hluncher.Task.TaskOrder;

public class TaskLine {

    private Task root;
    private Task current;
    private Checker checker;
    private TaskOrder taskOrder;

    private TaskLine() {
        checker = new Checker();
        taskOrder = new TaskOrder() {
            @Override
            public void beforeTask(Task task) {

            }

            @Override
            public void afterTask(Task task) {
                root = task.next().iterator().next();
                if (root == null) {
                    return;
                }
                TaskPool.execute(root);
            }
        };
    }

    public TaskLine next(Task task) {
        if (task == null) {
            return null;
        }
        task.setTaskOrder(taskOrder);
        if (root == null) {
            root = task;
        }
        if (current == null) {
            current = task;
        } else {
            current.addNext(task);
            current = task;
        }
        return this;
    }

    public void execute() throws RuntimeException {
        if (root == null) {
            return;
        }
        if (checker.checkRound(root)) {
            TaskPool.execute(root);
        } else {
            throw new RuntimeException("出现了任务环！");
        }
    }

    public Task getRoot() {
        return this.root;
    }

    public static TaskLine create() {
        return new TaskLine();
    }
}
