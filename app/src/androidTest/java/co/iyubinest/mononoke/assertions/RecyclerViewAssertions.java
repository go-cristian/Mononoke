package co.iyubinest.mononoke.assertions;

import android.support.annotation.IdRes;
import android.support.test.espresso.ViewAssertion;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class RecyclerViewAssertions {
  public static ViewAssertion count(int count) {
    return (view, noViewFoundException) -> {
      if (noViewFoundException != null) {
        throw noViewFoundException;
      }
      RecyclerView recyclerView = (RecyclerView) view;
      RecyclerView.Adapter adapter = recyclerView.getAdapter();
      assertThat(adapter.getItemCount(), is(count));
    };
  }

  public static ViewAssertion item(int position, @IdRes int layoutId,
      String string) {
    return (view, noViewFoundException) -> {
      if (noViewFoundException != null) {
        throw noViewFoundException;
      }
      RecyclerView recyclerView = (RecyclerView) view;
      RecyclerView.LayoutManager layoutManager =
          recyclerView.getLayoutManager();
      layoutManager.findViewByPosition(position);
      TextView textView =
          (TextView) (layoutManager.findViewByPosition(position))
              .findViewById(layoutId);
      assertThat(textView.getText().toString(), is(string));
    };
  }
}
