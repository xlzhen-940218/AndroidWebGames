package com.xlzhen.webgames;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;

/**
 * 网络状态检查工具类
 */
public class NetworkUtils {

    /**
     * 检查设备是否连接到网络（Wi-Fi 或移动数据）。
     * 适配了 Android M (API 23) 及以上和旧版本。
     *
     * @param context 应用程序或活动的上下文
     * @return 如果网络已连接，则返回 true；否则返回 false
     */
    public static boolean isNetworkConnected(Context context) {
        if (context == null) {
            return false;
        }

        ConnectivityManager connectivityManager = 
            (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        }

        // --- 适用于 Android M (API 23) 及以上版本 ---
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network network = connectivityManager.getActiveNetwork();
            if (network == null) {
                return false;
            }

            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            if (capabilities == null) {
                return false;
            }

            // 判断网络是否具有互联网能力 (NET_CAPABILITY_INTERNET)
            return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
            
            // 💡 提示：如果需要判断网络是否“真正能上网” (即通过了连接验证)，
            // 可以添加以下判断：
            // return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            //        && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);

        } else {
            // --- 兼容旧版本 (API 22 及以下) ---
            // 注意：getActiveNetworkInfo() 在 API 29 中被废弃，但在旧版本中必须使用
            
            @SuppressWarnings("deprecation")
            android.net.NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
    }
}