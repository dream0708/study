package com.sohu.bp.elite.task.queue;

import com.sohu.bp.elite.task.TaskEvent;
import java.util.List;

/**
 * @author zhangzhihao
 *         2016/8/27
 */
public interface TaskEventQueue {

    boolean push(boolean isBatch, TaskEvent event);

    TaskEvent poll(boolean isBatch);

    List<TaskEvent> poll(boolean isBatch, int size);

    long size(boolean isBatch);
}
