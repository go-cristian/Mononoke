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
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;
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
  private static final Subject<TeamEvent> subject = BehaviorSubject.create();
  private static final TeamEvent ALL_USERS_EVENT = TeamEvent.All.with(USERS);
  private static final Flowable ERROR = Flowable.error(new Exception());
  private static final User UPDATE_USER = BasicUser
      .create("", "avatar", "github2", "", "", "ready", Collections.emptyList(),
          Collections.emptyList());
  private static final TeamEvent UPDATE_USER_EVENT =
      TeamEvent.Status.with(UPDATE_USER);

  static {
    for (int i = 0; i < TOTAL; i++) {
      User user = BasicUser
          .create("name " + i, "avatar " + i, "github" + i, "role" + i, "co",
              "", Collections.singletonList("spanish"),
              Collections.singletonList("java"));
      USERS.add(user);
    }
  }

  @Rule public ActivityTestRule<TeamListActivity> rule =
      new ActivityTestRule<>(TeamListActivity.class, false, false);
  @Rule public DaggerRule daggerRule = new DaggerRule();
  @Mock public TeamInteractor interactor;

  private static App app() {
    return (App) InstrumentationRegistry.getInstrumentation().getTargetContext()
        .getApplicationContext();
  }

  @Test
  public void showAllUsers() {
    when(interactor.users())
        .thenReturn(subject.toFlowable(BackpressureStrategy.LATEST));
    subject.onNext(ALL_USERS_EVENT);
    rule.launchActivity(new Intent());
    onViewId(R.id.team_list).check(matches(isDisplayed()));
    onViewId(R.id.team_list).check(count(TOTAL));
    //check all elements on recycler view
    for (int i = 0; i < TOTAL; i++) {
      User user = USERS.get(i);
      verifyItemPosition(user, i);
    }
  }

  @Test
  public void updateUsersStatus() {
    when(interactor.users())
        .thenReturn(subject.toFlowable(BackpressureStrategy.LATEST));
    subject.onNext(ALL_USERS_EVENT);
    rule.launchActivity(new Intent());
    subject.onNext(UPDATE_USER_EVENT);
    onViewId(R.id.team_list).perform(scrollTo(2));
    onViewId(R.id.team_list)
        .check(item(2, R.id.team_list_item_status, "ready"));
  }

  private void verifyItemPosition(User user, int index) {
    onViewId(R.id.team_list).perform(scrollTo(index));
    onViewId(R.id.team_list)
        .check(item(index, R.id.team_list_item_name, user.name()));
    onViewId(R.id.team_list)
        .check(item(index, R.id.team_list_item_github, user.github()));
    onViewId(R.id.team_list)
        .check(item(index, R.id.team_list_item_role, user.role()));
    onViewId(R.id.team_list).check(item(index, R.id.team_list_item_status,
        string(R.string.team_list_item_status)));
    onViewId(R.id.team_list).perform(clickAt(index));
    //checks on new screen
    onViewId(R.id.team_mate_detail_github)
        .check(matches(withText(user.github())));
    onViewId(R.id.team_mate_detail_role).check(matches(withText(user.role())));
    Espresso.pressBack();
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
