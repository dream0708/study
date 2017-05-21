package com.sohu.bp.elite.service;

import java.util.List;

import com.sohu.bp.elite.enums.FeedType;

public interface EliteSquareService {
    /**
     * 获取广场内容
     * @param latestTime
     * @return
     */
    int getNewSquareNum(Long latestTime);
    /**
     * 获取广场更新内容
     * @param latestTime
     * @return
     */
    List<Long> getNewSquareList(Long latestTime);
    /**
     * 获取更早的信息
     * @param complexId
     * @param oldestTime
     * @param start
     * @param count
     * @return
     */
    List<Long> getBackward(Long feedId, FeedType feedType, int count);
    List<Long> getBackward(int start, int count);
    /**
     * 插入到广场
     * @param complexId
     * @param time
     * @return
     */
    boolean insertSquare(Long feedId, FeedType feedType);
    /**
     * 清除广场
     * @return
     */
    void flushSquare();
    /**
     * 删除广场中内容
     * @param complexId
     * @return
     */
    boolean removeSquareItem(Long feedId, FeedType feedType);
    /**
     * 获取优选广场内容
     * @param latestTime
     * @return
     */
    int getNewSelectedSquareNum(Long latestTime);
    /**
     * 获取广场更新内容
     * @param latestTime
     * @return
     */
    List<Long> getNewSelectedSquareList(Long latestTime);
    /**
     * backward
     */
    List<Long> getSelectedSquareBackward(Long feedId, FeedType feedType, int count);
    List<Long> getSelectedSquareBackward(int start, int count);
    List<Long> getSelectedSquareBackward(long oldestTime, int count);
    /**
     * forward
     * @param latestTime
     * @param count
     * @return
     */
    List<Long> getSelectedSquareForward(long latestTime, int count);
    /**
     * 插入到优选广场
     * @param complexId
     * @param time
     * @return
     */
    boolean insertSelectedSquare(Long feedId, FeedType feedType);
    /**
     * 清除优先广场
     * @return
     */
    void flushSelectedSquare();
    /**
     * 删除优选广场中内容
     * @param complexId
     * @return
     */
    boolean removeSelectedSquareItem(Long feedId, FeedType feedType);
    /**
     * 判断是否在优选广场
     * @param feedId
     * @param feedType
     * @return
     */
    boolean isInSelectedSquare(Long feedId, FeedType feedType);
}
