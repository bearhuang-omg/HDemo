package com.android.hluncher;

import com.android.hluncher.Task.TaskOrder;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class Checker {

    private Set<Task> checked = new HashSet<>();
    private Queue<Task> seeds = new ArrayDeque<>();

    public boolean checkRound(Task root) {
        return checkRound(root, null);
    }

    public boolean checkRound(Task root, TaskOrder order) {
        checked.clear();
        seeds.clear();
        seeds.offer(root);
        while (!seeds.isEmpty()) {
            Task task = seeds.poll();
            if (checked.contains(task)) {
                return false;
            }
            if (order != null) {
                task.setTaskOrder(order);
            }
            checked.add(task);
            Set<Task> children = task.next();
            if (children != null && children.size() > 0) {
                for (Task child : children) {
                    child.addPre(task);
                    seeds.offer(child);
                }
            }
        }
        checked.clear();
        seeds.clear();
        return true;
    }

}
