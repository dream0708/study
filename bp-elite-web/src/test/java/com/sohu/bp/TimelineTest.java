//package com.sohu.bp;
//
//
//
//import java.util.List;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.sohu.achelous.model.Feed;
//import com.sohu.achelous.timeline.AchelousTimeline;
//import com.sohu.achelous.timeline.service.TimelineFilter;
//import com.sohu.achelous.timeline.service.TimelineService;
//import com.sohu.achelous.timeline.util.TimeLineUtil;
//import com.sohu.bp.elite.enums.BpType;
//
//public class TimelineTest {
//	
//	private static final Logger log = LoggerFactory.getLogger(TimelineTest.class);
//	private static final TimelineService timelineService = AchelousTimeline.getService();
//	
//	public static void main(String[] args) {
//		Long bpId = 8222285l;
//		Long oldTime = 1474597546409l;
//		Long accountId = TimeLineUtil.getComplexId(bpId, BpType.Elite_User.getValue());
//		List<Feed> feeds = timelineService.forward(accountId, null, null);
//		log.info("feeds infomation : " + feeds.toString());
//	}
//}
