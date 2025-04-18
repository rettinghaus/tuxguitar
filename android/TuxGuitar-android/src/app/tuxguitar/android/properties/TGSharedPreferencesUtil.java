package app.tuxguitar.android.properties;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import androidx.core.content.pm.PackageInfoCompat;

public class TGSharedPreferencesUtil {

	public static String getSharedPreferencesName(Activity activity, String module, String resource) {
		return (TGSharedPreferencesUtil.getPreferencesPrefix(activity) + "." + module + "-" + resource);
	}

	public static String getPreferencesPrefix(Activity activity) {
		StringBuilder prefix = new StringBuilder("tuxguitar");
		try {
			PackageInfo packageInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
			if( packageInfo != null ) {
				prefix.append("-" + PackageInfoCompat.getLongVersionCode(packageInfo));

			}
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return prefix.toString();
	}
}
