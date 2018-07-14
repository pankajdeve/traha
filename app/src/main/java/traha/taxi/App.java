

package traha.taxi;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.facebook.FacebookSdk;

import traha.taxi.Utils.FontsOverride;
import traha.taxi.Utils.UserModel;

public class App extends MultiDexApplication {
    private Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        UserModel.initInstance(mContext);
        //FacebookSdk.sdkInitialize(this.getApplicationContext());

        FontsOverride.setDefaultFont(this, "SERIF", "Roboto-Regular.ttf");
        FontsOverride.setDefaultFont(this, "MONOSPACE", "Roboto-Medium.ttf");
        FontsOverride.setDefaultFont(this, "SANS_SERIF", "Roboto-Thin.ttf");
    }
}
