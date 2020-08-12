package com.example.p_intent_share;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.List;

public class ShareUtils {
    private static ShareUtils instance;
    private Context mContent;
    public final String QQ_PACKAGE_NAME = "com.tencent.mobileqq";

    public static ShareUtils getInstance(Context context) {
        if (instance == null) {
            instance = new ShareUtils(context);
        }
        return instance;
    }

    public ShareUtils(Context context) {
        this.mContent = context;
    }

    public boolean isQQClientAvailable() {
        return isClientAvailable(QQ_PACKAGE_NAME);
    }

    public boolean isClientAvailable(String packageName) {
        final PackageManager packageManager = mContent.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                Log.v("hank", "有安裝的檔案名稱：" + pn + "\n");
                if (pn.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }


}
