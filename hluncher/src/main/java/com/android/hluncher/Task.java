package com.android.hluncher;

import java.util.HashSet;
import java.util.Set;

public abstract class Task implements Runnable {

    private Set<Task> next = new HashSet<>();
    private Set<Task> pre = new HashSet<>();
    private Set<TaskOrder> taskOrder = new HashSet<>();
    public String tag;

    public Task(String tag) {
        this.tag = tag;
    }

    public Task() {
    }

    public void addNext(Task task) {
        if (task == null) {
            return;
        }
        next.add(task);
        task.pre.add(this);
    }

    public Set<Task> next() {
        return this.next;
    }

    public Set<Task> pre() {
        return this.pre;
    }

    public void addPre(Task task) {
        if (task == null) {
            return;
        }
        pre.add(task);
        task.next.add(this);
    }

    public void setTaskOrder(TaskOrder taskOrder) {
        this.taskOrder.add(taskOrder);
    }

    @Override
    public void run() {
        if (taskOrder != null) {
            for (TaskOrder order : taskOrder) {
                order.beforeTask(this);
            }
        }
        if (isExecute()) {
            runTask();
        }
        if (taskOrder != null) {
            for (TaskOrder order : taskOrder) {
                order.afterTask(this);
            }
        }
        taskOrder.clear();
    }

    public abstract void runTask();

    public boolean isExecute() {
        return true;
    }

    public Priority getPriority() {
        return Priority.Normal;
    }

    public enum Priority {
        Immediately,//放入缓存线程池执行
        Normal,//放入线程池执行
        Idle,//cpu空闲时执行
        Main//主线程执行
    }

    public interface TaskOrder {

        public void beforeTask(Task task);

        public void afterTask(Task task);
    }
}
