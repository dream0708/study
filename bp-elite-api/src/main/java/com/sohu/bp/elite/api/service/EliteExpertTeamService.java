package com.sohu.bp.elite.api.service;

import java.util.List;

public interface EliteExpertTeamService {
    List<String> getExpertTeamList();
    boolean addExpert(Long bpId);
    boolean removeExpert(Long bpId);
    void updateCache();
}
