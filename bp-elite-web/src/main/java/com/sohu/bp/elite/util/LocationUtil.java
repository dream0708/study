package com.sohu.bp.elite.util;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sohu.bp.service.adapter.BpExtendServiceAdapter;
import com.sohu.bp.service.adapter.BpServiceAdapterFactory;
import com.sohu.bp.utils.IpUtil;
import com.sohu.bp.utils.RealIpUtil;
import com.sohu.bp.utils.ResourcesUtil;
import com.sohu.bp.utils.crypt.Base64Util;
import com.sohu.bp.utils.http.CookieUtil;
import com.sohu.bp.utils.http.HttpParameterUtil;
import com.sohu.nuc.model.CodeMsgData;
import com.sohu.nuc.model.ResponseConstants;
import org.apache.log4j.Logger;
import org.springframework.mock.web.MockHttpServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LocationUtil {
    private static final Logger logger = Logger.getLogger(LocationUtil.class);

    private static IPLocatorUtil ipLocator = new IPLocatorUtil();

    private static Map<String, String> provinces = Maps.newHashMap();

    private static Map<String, String> cities = Maps.newHashMap();

    private static Map<String, String[]> areas = Maps.newLinkedHashMap();

    private static Map<String, String[]> hotCities = Maps.newLinkedHashMap();

    private static BpExtendServiceAdapter bpExtendServiceAdapter = BpServiceAdapterFactory.getBpExtendServiceAdapter();

    private static Map<String, Long> cacheCities = new ConcurrentHashMap<>();
    private static Map<String, Long> cacheCityProvinceMap = new ConcurrentHashMap<>();
    private static Map<Long, String> cacheProvinces = new ConcurrentHashMap<>();

    static {
        try {
            String[] _provinces = ResourcesUtil.getResourceContent("provinces.data", "UTF-8").split("\\n");
            String[] _cities = ResourcesUtil.getResourceContent("cities.data", "UTF-8").split("\\n");
            for (String province : _provinces) {
                provinces.put(province.replace("省", ""), province);
            }
            for (String city : _cities) {
                cities.put(city.replace("市", "").replace("自治州", "").replace("地区", "").replace("区", ""), city);
            }
            String[] areaLines = ResourcesUtil.getResourceContent("areas.data", "UTF-8").split("\\n");
            for (String line : areaLines) {
                String[] names = line.split("\\s+");
                if (names.length > 1) {
                    areas.put(names[0], Arrays.copyOfRange(names, 1, names.length));
                }
            }
            String[] cityLines = ResourcesUtil.getResourceContent("hotCities.data", "UTF-8").split("\\n");
            for (String line : cityLines) {
                String[] names = line.split("\\s+");
                if (names.length > 1) {
                    hotCities.put(names[0], Arrays.copyOfRange(names, 1, names.length));
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 获取ip所在省
     * 可能返回""，即未找到对应的省
     *
     * @param ip 要定位的ip地址
     * @return 省名称(包含省字)
     */
    public static String getProvince(String ip) {
        String location = getLocation(ip);
        for (String province : provinces.keySet()) {
            if (location.contains(province)) {
                return provinces.get(province);
            }
        }
        if (location.contains("省")) {
            return location.substring(0, location.indexOf("省") + 1);
        } else {
            return "";
        }
    }

    /**
     * 获取ip所在市
     *
     * @param ip 要定位的ip地址
     * @return 市名称(包含市字)
     */
    public static String getCity(String ip) {
        String location = getLocation(ip);
        for (String city : cities.keySet()) {
            if (location.contains(city)) {
                return cities.get(city);
            }
        }
        if (location.contains("市")) {
            return location.substring(0, location.indexOf("市") + 1);
        }
        //开始去除省
        location = location.replace("省", "");
        for (String province : provinces.keySet()) {
            location = location.replace(province, "");
        }
        //开始去除多余字
        location = location.replace("市", "").replace("自治州", "").replace("地区", "").replace("区", "");
        if (location.length() > 2) {
            location = location.replace("州", "");
        }
        for (String city : cities.keySet()) {
            if (location.contains(city) || city.contains(location)) {
                return cities.get(city);
            }
        }
        return "";
    }

    /**
     * 获取地理位置 未校正
     *
     * @param ip 要定位的ip地址
     * @return 地理位置
     */
    private static String getLocation(String ip) {
        if (!IpUtil.isValidIp(ip)) {
            return "";
        }
        String location = ipLocator.getLocation(ip);
        if (location.contains("市")) {
            location = location.substring(0, location.indexOf("市") + 1);
        }
        return location;
    }

    public static Map<String, String[]> getAreas() {
        return areas;
    }

    public static Map<String, String[]> getHotCities() {
        return hotCities;
    }
    
    public static Map<String, String> getProvinces() {
        return provinces;
    }

    /**
     * 从cookie中读取用户保存的城市名，如果没有就根据ip返回地址信息，但是并不保存
     * @param request
     * @return
     */
    public static String getSavedCity(HttpServletRequest request) {
        int cityId = HttpParameterUtil.getInt(request, "cityId", 0);
        if (cityId > 0) {
            CodeMsgData getArea = bpExtendServiceAdapter.getAreaCodeDataByAd(cityId);
            if (getArea.getCode() == ResponseConstants.OK) {
                JSONObject data = JSONObject.parseObject(getArea.getData());
                if (!Strings.isNullOrEmpty(data.getString("name"))) {
                    return data.getString("name").replace("市", "");
                }
            }
        }
        String city = "全国";
        String locationString = CookieUtil.getCookieValue(request, "bpLocation");
        if (!Strings.isNullOrEmpty(locationString)) {
            city = JSONObject.parseObject(Base64Util.decode(locationString)).getString("cityName");
        } else {
            String ip = RealIpUtil.getRealIP(request);
            String foundCity = LocationUtil.getCity(ip);
            if (!Strings.isNullOrEmpty(foundCity)) {
                city = foundCity.replace("市", "");
            }
        }
//        CodeMsgData getAreaCodeByType = bpExtendServiceAdapter.getAreaCodeByType(city, 1);
//        if (getAreaCodeByType.getCode() == ResponseConstants.OK) {
//            JSONObject data = JSONObject.parseObject(getAreaCodeByType.getData());
//            if (data.getIntValue("show") != 1) {
//                city = "全国";
//            }
//        } else {
//            city = "全国";
//        }
        return city;
    }

    /**
     * 从cookie中读取用户保存的地址信息，如果没有就根据ip返回地址信息，但是并不保存
     * @param request
     * @return
     */
    public static JSONObject getSavedArea(HttpServletRequest request) {
        String locationString = CookieUtil.getCookieValue(request, "bpLocation");
        if (!Strings.isNullOrEmpty(locationString)) {
            return JSONObject.parseObject(Base64Util.decode(locationString));
        } else {
            JSONObject location = new JSONObject();
            location.put("cityName", "");
            location.put("provinceName", "");
            location.put("districtName", "");
            location.put("cityId", -1);
            location.put("provinceId", -1);
            location.put("districtId", -1);
            String city = getSavedCity(request);
            Long cacheCityId = cacheCities.get(city);
            if (cacheCityId == null) {
                CodeMsgData getAreaCodeByType = bpExtendServiceAdapter.getAreaCodeByType(city, 1);
                if (getAreaCodeByType.getCode() == ResponseConstants.OK) {
                    int cityId = JSONObject.parseObject(getAreaCodeByType.getData()).getIntValue("areaCode");
                    location.put("cityId", cityId);
                    location.put("cityName", city);
                    cacheCities.put(city, (long) cityId);
                    CodeMsgData getParentArea = bpExtendServiceAdapter.getParentArea(cityId);
                    if (getParentArea.getCode() == ResponseConstants.OK) {
                        int provinceId = JSONObject.parseObject(getParentArea.getData()).getIntValue("areaCode");
                        String provinceName = JSONObject.parseObject(getParentArea.getData()).getString("name");
                        cacheCityProvinceMap.put(city, (long)provinceId);
                        cacheProvinces.put((long)provinceId, provinceName);
                        location.put("provinceId", provinceId);
                        location.put("provinceName", provinceName);
                    }
                }
            } else {
                location.put("cityId", cacheCityId);
                location.put("cityName", city);
                Long provinceId = cacheCityProvinceMap.get(city);
                if (provinceId != null) {
                    String provinceName = cacheProvinces.get(provinceId);
                    location.put("provinceId", provinceId);
                    location.put("provinceName", provinceName);
                }
            }
            return location;
        }
    }

    private static JSONArray DELIVERY_AREAS = new JSONArray();

    static {
        Map<String, Integer> areaCodeProvince = new LinkedHashMap<String, Integer>();
        areaCodeProvince.put("北京", 110000);
        areaCodeProvince.put("天津", 120000);
        areaCodeProvince.put("河北", 130000);
        areaCodeProvince.put("山西", 140000);
        areaCodeProvince.put("内蒙古", 150000);
        areaCodeProvince.put("辽宁", 210000);
        areaCodeProvince.put("吉林", 220000);
        areaCodeProvince.put("黑龙江", 230000);
        areaCodeProvince.put("上海", 310000);
        areaCodeProvince.put("江苏", 320000);
        areaCodeProvince.put("浙江", 330000);
        areaCodeProvince.put("安徽", 340000);
        areaCodeProvince.put("福建", 350000);
        areaCodeProvince.put("江西", 360000);
        areaCodeProvince.put("山东", 370000);
        areaCodeProvince.put("河南", 410000);
        areaCodeProvince.put("湖北", 420000);
        areaCodeProvince.put("湖南", 430000);
        areaCodeProvince.put("广东", 440000);
        areaCodeProvince.put("广西", 450000);
        areaCodeProvince.put("海南", 460000);
        areaCodeProvince.put("重庆", 500000);
        areaCodeProvince.put("四川", 510000);
        areaCodeProvince.put("贵州", 520000);
        areaCodeProvince.put("云南", 530000);
        areaCodeProvince.put("西藏", 540000);
        areaCodeProvince.put("陕西", 610000);
        areaCodeProvince.put("甘肃", 620000);
        areaCodeProvince.put("青海", 630000);
        areaCodeProvince.put("宁夏", 640000);
        areaCodeProvince.put("新疆", 650000);
        areaCodeProvince.put("台湾", 710000);
        areaCodeProvince.put("香港", 810000);
        areaCodeProvince.put("澳门", 820000);
        for (String tempKey : areaCodeProvince.keySet()) {
            JSONObject tempMap = new JSONObject();
            tempMap.put("name", tempKey);
            CodeMsgData getAreaCodeByType = bpExtendServiceAdapter.getAreaCodeByType(tempKey, 0);
            if (getAreaCodeByType.getCode() == ResponseConstants.OK) {
                tempMap.put("areaCode", JSONObject.parseObject(getAreaCodeByType.getData()).getIntValue("areaCode"));
            } else {
                tempMap.put("areaCode", areaCodeProvince.get(tempKey));
            }
            DELIVERY_AREAS.add(tempMap);
        }
    }

    /**
     * 电商平台用的送货地址信息列表（目前已无用）
     * @return
     */
    @Deprecated
    public static JSONArray getDeliveryAreas() {
        return DELIVERY_AREAS;
    }

    /**
     * 电商平台用的送货地址信息列表（目前已无用）
     * @param areaName
     * @return
     */
    @Deprecated
    public static int getDeliveryAreaCode(String areaName) {
        for (int i = 0; i < DELIVERY_AREAS.size(); i++) {
            JSONObject area = DELIVERY_AREAS.getJSONObject(i);
            if (area.getString("name").equals(areaName)) {
                return area.getIntValue("areaCode");
            }
        }
        for (int i = 0; i < DELIVERY_AREAS.size(); i++) {
            JSONObject area = DELIVERY_AREAS.getJSONObject(i);
            if (area.getString("name").contains(areaName) || areaName.contains(area.getString("name"))) {
                return area.getIntValue("areaCode");
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        while (true) {
            System.out.println(getSavedArea(new MockHttpServletRequest()));
        }

    }
}
