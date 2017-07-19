package co.iyubinest.mononoke;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class BaseTest {

  protected static ViewInteraction onViewId(@IdRes int idRes) {
    return onView(withId(idRes));
  }

  protected static ViewInteraction onViewText(@StringRes int stringRes) {
    return onView(withText(stringRes));
  }

  protected static String string(@StringRes int id) {
    return app().getString(id);
  }

  protected static App app() {
    return (App) InstrumentationRegistry.getInstrumentation()
        .getTargetContext()
        .getApplicationContext();
  }
}
