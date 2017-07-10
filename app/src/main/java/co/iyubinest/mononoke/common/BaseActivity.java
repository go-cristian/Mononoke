package co.iyubinest.mononoke.common;
import android.support.v7.app.AppCompatActivity;
import co.iyubinest.mononoke.App;
import co.iyubinest.mononoke.AppComponent;

public class BaseActivity extends AppCompatActivity {
  protected AppComponent appComponent() {
    return app().appComponent();
  }

  protected App app() {
    return (App) getApplication();
  }
}
