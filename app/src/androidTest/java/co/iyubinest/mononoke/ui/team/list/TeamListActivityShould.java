package co.iyubinest.mononoke.ui.team.list;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import co.iyubinest.mononoke.App;
import co.iyubinest.mononoke.DaggerRule;
import co.iyubinest.mononoke.R;
import co.iyubinest.mononoke.data.BasicUser;
import co.iyubinest.mononoke.data.TeamEvent;
import co.iyubinest.mononoke.data.User;
import co.iyubinest.mononoke.data.team.get.TeamInteractor;
import io.reactivex.Flowable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static co.iyubinest.mononoke.assertions.RecyclerViewActions.clickAt;
import static co.iyubinest.mononoke.assertions.RecyclerViewActions.scrollTo;
import static co.iyubinest.mononoke.assertions.RecyclerViewAssertions.count;
import static co.iyubinest.mononoke.assertions.RecyclerViewAssertions.item;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class TeamListActivityShould {

  private static final int TOTAL = 10;
  private static final List<User> USERS = new ArrayList<>(TOTAL);
  private static final Flowable<TeamEvent> ALL_USERS_EVENT =
      Flowable.defer(() -> Flowable.just(TeamEvent.All.with(USERS)));
  private static final Flowable ERROR = Flowable.error(new Exception());

  static {
    for (int i = 0; i < TOTAL; i++) {
      User user = BasicUser
          .create("name " + i, "avatar " + i, "github" + i, "role" + i, "co",
              "", Collections.singletonList("spanish"),
              Collections.singletonList("java"));
      USERS.add(user);
    }
  }

  @Rule public ActivityTestRule rule =
      new ActivityTestRule<>(TeamListActivity.class, false, false);
  @Rule public DaggerRule daggerRule = new DaggerRule();
  @Mock public TeamInteractor interactor;

  private static App app() {
    return (App) InstrumentationRegistry.getInstrumentation().getTargetContext()
        .getApplicationContext();
  }

  @Test
  public void showAllUsers() {
    when(interactor.users()).thenReturn(ALL_USERS_EVENT);
    rule.launchActivity(new Intent());
    onViewId(R.id.team_list).check(matches(isDisplayed()));
    onViewId(R.id.team_list).check(count(TOTAL));
    //check all elements on recycler view
    for (int productCount = 0; productCount < TOTAL; productCount++) {

      User user = USERS.get(productCount);
      onViewId(R.id.team_list).perform(scrollTo(productCount));
      onViewId(R.id.team_list)
          .check(item(productCount, R.id.team_list_item_name, user.name()));
      onViewId(R.id.team_list)
          .check(item(productCount, R.id.team_list_item_github, user.github()));
      onViewId(R.id.team_list)
          .check(item(productCount, R.id.team_list_item_role, user.role()));
      onViewId(R.id.team_list).check(
          item(productCount, R.id.team_list_item_status,
              string(R.string.team_list_item_status)));

      onViewId(R.id.team_list).perform(clickAt(productCount));
      //checks on new screen
      onViewId(R.id.team_mate_detail_github)
          .check(matches(withText(user.github())));
      onViewId(R.id.team_mate_detail_role)
          .check(matches(withText(user.role())));
      Espresso.pressBack();
    }
  }

  private ViewInteraction onViewId(@IdRes int idRes) {
    return onView(withId(idRes));
  }

  private ViewInteraction onViewText(@StringRes int stringRes) {
    return onView(withText(stringRes));
  }

  private String string(@StringRes int id) {
    return app().getString(id);
  }
}
