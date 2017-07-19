package co.iyubinest.mononoke.assertions;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.ViewActions;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import org.hamcrest.Matcher;

import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static org.hamcrest.CoreMatchers.allOf;

public class RecyclerViewActions {

  public static ViewAction clickAt(final int position) {
    return new ViewAction() {
      private final ViewAction click = ViewActions.click();

      @Override public Matcher<View> getConstraints() {
        return click.getConstraints();
      }

      @Override public String getDescription() {
        return "clicking recycler view at " + position;
      }

      @Override public void perform(UiController uiController, View view) {
        RecyclerView recyclerView = (RecyclerView) view;
        RecyclerView.LayoutManager layout = recyclerView.getLayoutManager();
        click.perform(uiController, layout.findViewByPosition(position));
      }
    };
  }

  public static ViewAction scrollTo(int position) {
    return new ViewAction() {
      @Override public Matcher<View> getConstraints() {
        return allOf(isAssignableFrom(RecyclerView.class), isDisplayed());
      }

      @Override public String getDescription() {
        return "Scroll RecyclerView to position: " + position;
      }

      @Override public void perform(UiController uiController, View view) {
        RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.scrollToPosition(position);
      }
    };
  }
}
