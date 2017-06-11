package co.iyubinest.mononoke;

import android.app.Application;
import android.support.annotation.VisibleForTesting;
import com.facebook.stetho.Stetho;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class App extends Application {

  private AppComponent component;

  @Override
  public void onCreate() {
    super.onCreate();
    Stetho.initializeWithDefaults(this);
    CalligraphyConfig.initDefault(
        new CalligraphyConfig.Builder().setDefaultFontPath("green_avocado.ttf")
            .setFontAttrId(R.attr.fontPath).build());
    component =
        DaggerAppComponent.builder().appModule(new AppModule(this)).build();
  }

  public AppComponent appComponent() {
    return component;
  }

  @VisibleForTesting
  public void setComponent(AppComponent component) {
    this.component = component;
  }
}
