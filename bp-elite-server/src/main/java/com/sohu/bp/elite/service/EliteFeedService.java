package com.sohu.bp.elite.service;

import com.sohu.bp.elite.enums.EliteFeedAttitudeType;
import com.sohu.bp.elite.enums.ProduceActionType;

public interface EliteFeedService {
    void notify2Timeline(EliteFeedAttitudeType attitudeType, Long accountId, Long unitId, ProduceActionType actionType);
}
