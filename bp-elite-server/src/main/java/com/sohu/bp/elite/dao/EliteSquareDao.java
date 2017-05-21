package com.sohu.bp.elite.dao;

import java.util.List;

import com.sohu.bp.elite.enums.FeedType;

public interface EliteSquareDao {
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
    boolean insertSquare(Long complexId, Long time);
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
    boolean removeSquareItem(Long complexId);
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
     */
    List<Long> getSelectedSquareForward(long latestTime, int count);
    /**
     * 插入到优选广场
     * @param complexId
     * @param time
     * @return
     */
    boolean insertSelectedSquare(Long complexId, Long time);
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
    boolean removeSelectedSquareItem(Long complexId);
    /**
     * 判断进入优选广场的内容是否有效，六条内不能有同样的bpId和questionId
     * @param questionId
     * @param bpId
     * @param time
     * @return
     */
    boolean isSelectedValid(Long questionId, Long bpId, long time);
    /**
     * 判断是否在优选广场
     * @param complexId
     * @return
     */
    boolean isInSelectedSquare(Long complexId);
}
