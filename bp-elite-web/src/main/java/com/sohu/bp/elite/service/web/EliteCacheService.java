package com.sohu.bp.elite.service.web;

public interface EliteCacheService {

    /**
     * 获取回答的总数
     * @return
     */
    int getTotalAnswerNum();
    

    /**
     * 清空回答总数缓存
     */
    void clearTotalAnswerNumCache();
    
    //缓存标签广场首页,问题,回答feed流数据
    Object getTagIndex(int tagId, Long viewerId);
    Object getTagQuestions(int tagId, Long viewerId);
    Object getTagAnswers(int tagId, Long viewerId);

    void reloadTagIndex(int tagId, Long viewerId);
    void reloadTagQuestions(int tagId, Long viewerId);
    void reloadTagAnswers(int tagId, Long viewerId);

    void reloadTagFeeds(String tagIds, Long viewerId);

    String getLoginToken();

    String getLoginToken(boolean update);
}
