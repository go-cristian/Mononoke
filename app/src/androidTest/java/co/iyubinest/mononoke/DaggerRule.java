package co.iyubinest.mononoke;
import android.support.test.InstrumentationRegistry;
import it.cosenonjaviste.daggermock.DaggerMockRule;

public class DaggerRule extends DaggerMockRule<AppComponent> {
  public DaggerRule() {
    super(
      AppComponent.class,
      new AppModule(app())
    );
    set(component -> app().setComponent(component));
  }

  private static App app() {
    return (App) InstrumentationRegistry.getInstrumentation().getTargetContext()
      .getApplicationContext();
  }
}