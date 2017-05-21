package com.sohu.bp.elite.api.util;

import com.sohu.bp.elite.api.constants.Constants;
import com.sohu.bp.elite.enums.BpType;

/**
 * 用于生成各种合成ID
 * @author zhijungou
 * 2017年3月30日
 */
public class CompositeIDUtil {
    
    public static Long getCompositeId(Long objectId, BpType bpType) {
        if (null == objectId || null == bpType || objectId <= 0) return null;
        return objectId * Constants.COMPLEX_ID_SHIFT_BIG + bpType.getValue();
    }
    
    public static BpType getBpTypeByCompositeId(Long compositeId) {
        if (null == compositeId) return null;
        int type = (int) (compositeId % Constants.COMPLEX_ID_SHIFT_BIG);
        return BpType.valueMap.get(type);
    }
    
    public static Long getObjectIdByCompositeId(Long compositeId) {
        if (null == compositeId) return null;
        long objectId = compositeId / Constants.COMPLEX_ID_SHIFT_BIG;
        return objectId;
    }
}
