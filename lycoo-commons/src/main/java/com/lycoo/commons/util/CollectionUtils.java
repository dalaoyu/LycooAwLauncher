package com.lycoo.commons.util;

import java.util.Collection;
import java.util.Map;

/**
 * xxx
 *
 * Created by lancy on 2017/6/8
 */

public class CollectionUtils {

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

}
