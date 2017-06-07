package co.iyubinest.mononoke.common;

import android.support.v7.app.AppCompatActivity;
import co.iyubinest.mononoke.App;

public class BaseActivity extends AppCompatActivity {

  protected Dependencies dependencies() {
    return app().dependencies();
  }

  App app() {
    return (App) getApplication();
  }
}
