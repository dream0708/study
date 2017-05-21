package com.sohu.bp;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringEscapeUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.util.HtmlUtils;

import com.sohu.bp.elite.Configuration;
import com.sohu.bp.elite.service.web.KafkaService;

import net.sf.json.JSONArray;

/**
 * 测试kafka消费者
 * @author zhijungou
 * 2017年1月17日
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = {"classpath:bpEliteWeb/*.xml","classpath:*.xml"})
public class TestKafka {
	private static final Logger log = LoggerFactory.getLogger(TestKafka.class);
	@Resource
	private Configuration configuration;
	@Resource
	private KafkaService kafkaService;
	
	@Test
	public void testConsume(){
		String topic = configuration.get("kafka.topic.square");
		log.info("kafka topic : " + topic);
		kafkaService.produce(topic, "test kafka consume");
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			log.error("", e);
		}
	}
	
	public static void main(String[] args) {
		String text =org.apache.commons.lang.StringEscapeUtils.unescapeHtml("[{&quot;cont_item_time&quot;: &quot;2016-08-16 09:13:19&quot;, &quot;cont_item_digg_count&quot;: &quot;0&quot;, &quot;cont_item_body&quot;: &quot;自古&quot;黄金有价玉无价&quot;，你说的这个貔貅应该是行内人所说的金镶玉，带证书的一般不会假。证书里面有黄金的克重，你按金价乘以克重所得的价格；而和田玉的价格就要看具体水头，通透性等，这就需要具备专业知识的人才清楚，在香港拍卖会中，一个戒指就拍了3个亿的价格。、希望对你有用，望采纳价格来源网络仅供参考&quot;, &quot;cont_item_author_id&quot;: 0, &quot;cont_item_author_avatar&quot;: &quot;http://jj-crawl-img.bjctc.img.sohucs.com/fd324f9477403698337ad974b01dbfd8.jpg&quot;, &quot;cont_item_author_name&quot;: &quot;橙小七1994&quot;, &quot;cont_item_author_url&quot;: &quot;http://bbs.to8to.com/space-uid-6493504.html&quot;}, {&quot;cont_item_time&quot;: &quot;2016-08-16 12:43:40&quot;, &quot;cont_item_digg_count&quot;: &quot;0&quot;, &quot;cont_item_body&quot;: &quot;广州心福珠宝贸易有限公司 2年尺寸 整体14*12*11裸石14*8*102980.00元 貔貅是瑞兽玉貔貅有挡煞辟邪护主聚财的功能，带貔貅要根据本人的生辰八字的五行缺属和喜用神来选择貔貅的材质，一般情况是黄色属土黑色属水玉貔貅五行属金。所以，在选择貔貅之前应该先请人分析一下生辰八字比较好。以上价格来源于网络，仅供参考。&quot;, &quot;cont_item_author_id&quot;: 0, &quot;cont_item_author_avatar&quot;: &quot;http://jj-crawl-img.bjctc.img.sohucs.com/fd324f9477403698337ad974b01dbfd8.jpg&quot;, &quot;cont_item_author_name&quot;: &quot;翘翘来了&quot;, &quot;cont_item_author_url&quot;: &quot;http://bbs.to8to.com/space-uid-5871577.html&quot;}, {&quot;cont_item_time&quot;: &quot;2016-08-16 13:27:12&quot;, &quot;cont_item_digg_count&quot;: &quot;0&quot;, &quot;cont_item_body&quot;: &quot;您好，价格不等，看普通的也就是200元，吉祥山人的博客里说过碧貔貅玉不注重价钱而是它的寓意；又 名天禄、辟邪，是古代五大瑞 兽之一，其中貔貅被称作“招 财神兽”，具有极强的招财旺 运、辟邪镇宅、保平安之功效。价格来源于网络，仅供参考。&quot;, &quot;cont_item_author_id&quot;: 0, &quot;cont_item_author_avatar&quot;: &quot;http://jj-crawl-img.bjctc.img.sohucs.com/70d892181120bb39c5f4cdc2d1d8a453.jpg&quot;, &quot;cont_item_author_name&quot;: &quot;背靠暖阳123&quot;, &quot;cont_item_author_url&quot;: &quot;http://bbs.to8to.com/space-uid-6907975.html&quot;}, {&quot;cont_item_time&quot;: &quot;&quot;, &quot;cont_item_digg_count&quot;: 0, &quot;cont_item_body&quot;: &quot;&quot;, &quot;cont_item_author_id&quot;: 0, &quot;cont_item_author_avatar&quot;: &quot;http://jj-crawl-img.bjctc.img.sohucs.com/70d892181120bb39c5f4cdc2d1d8a453.jpg&quot;, &quot;cont_item_author_name&quot;: &quot;搜狐焦点网友&quot;, &quot;cont_item_author_url&quot;: &quot;&quot;}]");
		String aa = text.substring(95);
		System.out.println("error at " + aa);
		JSONArray.fromObject(text);
	}
}
