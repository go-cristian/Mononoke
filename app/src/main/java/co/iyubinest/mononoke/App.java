package co.iyubinest.mononoke;

import android.app.Application;
import co.iyubinest.mononoke.common.Dependencies;
import com.facebook.stetho.Stetho;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class App extends Application {

  private Dependencies dependencies;

  @Override
  public void onCreate() {
    super.onCreate();
    Stetho.initializeWithDefaults(this);
    CalligraphyConfig.initDefault(
        new CalligraphyConfig.Builder().setDefaultFontPath("green_avocado.ttf")
            .setFontAttrId(R.attr.fontPath).build());
    dependencies = new Dependencies();
  }

  public Dependencies dependencies() {
    return dependencies;
  }
}
