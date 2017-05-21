package com.sohu.bp.elite.task;

import java.util.List;

/**
 * @author zhangzhihao
 *         2016/8/24
 */
public interface TaskEventQueue {

    void push(TaskEvent event);

    TaskEvent poll();

    List<TaskEvent> poll(int size);

    long size();
}
