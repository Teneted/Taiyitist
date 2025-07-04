package com.taiyitistmc.util;

import com.taiyitistmc.TaiyitistMCStart;

/**
 * @author Mgazul by MohistMC
 * @date 2023/9/23 6:15:26
 */
public class I18n {

    public static String as(String key) {
        return TaiyitistMCStart.I18N.as(key);
    }

    public static String as(String key, Object... objects) {
        return TaiyitistMCStart.I18N.as(key, objects);
    }
}
