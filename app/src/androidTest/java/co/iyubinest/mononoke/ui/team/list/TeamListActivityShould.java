package co.iyubinest.mononoke.ui.team.list;
import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import co.iyubinest.mononoke.BaseTest;
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

import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static co.iyubinest.mononoke.assertions.RecyclerViewActions.clickAt;
import static co.iyubinest.mononoke.assertions.RecyclerViewActions.scrollTo;
import static co.iyubinest.mononoke.assertions.RecyclerViewAssertions.count;
import static co.iyubinest.mononoke.assertions.RecyclerViewAssertions.item;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class TeamListActivityShould extends BaseTest {
  private static final int TOTAL = 10;
  private static final List<User> USERS = new ArrayList<>(TOTAL);
  private static final Subject<TeamEvent> subject = BehaviorSubject.create();
  private static final TeamEvent ALL_USERS_EVENT = TeamEvent.All.with(USERS);
  private static final Flowable ERROR = Flowable.error(new Exception());
  private static final User USER = BasicUser.create(
    "Cristian Gomez",
    "https://scontent.feoh3-1.fna.fbcdn.net/v/t1.0-9/15826806_10210753267884430_5673865899618602352_n.jpg?oh=8c14a9a582f32f8ea39ef5a5cd745c67&oe=59D3AF60",
    "github2",
    "Developer",
    "Medellin",
    "Not available",
    Collections.singletonList("Spanish"),
    Collections.singletonList("Java")
  );
  private static final User READY_USER = BasicUser.create(
      "Cristian Gomez",
      "https://scontent.feoh3-1.fna.fbcdn.net/v/t1.0-9/15826806_10210753267884430_5673865899618602352_n.jpg?oh=8c14a9a582f32f8ea39ef5a5cd745c67&oe=59D3AF60",
      "github 2",
      "Developer",
      "Medellin",
      "Ready",
      Collections.singletonList("Spanish"),
      Collections.singletonList("Java")
  );
  private static final TeamEvent UPDATE_USER_EVENT = TeamEvent.Status.with(READY_USER);

  static {
    prepareTestUsers();
  }

  @Rule
  public ActivityTestRule<TeamListActivity> rule = new ActivityTestRule<>(
    TeamListActivity.class,
    false,
    false
  );
  @Rule
  public DaggerRule daggerRule = new DaggerRule();
  @Mock
  public TeamInteractor interactor;

  private static void prepareTestUsers() {
    for (int i = 0; i < TOTAL; i++) {
      User user = BasicUser
          .from(USER)
          .name("Test name " + i)
          .github("github " + i)
          .build();
      USERS.add(user);
    }
  }

  @Test
  public void showAllUsers() {
    when(interactor.users()).thenReturn(subject.toFlowable(BackpressureStrategy.LATEST));
    subject.onNext(ALL_USERS_EVENT);
    rule.launchActivity(new Intent());
    onViewId(R.id.team_list).check(matches(isDisplayed()));
    onViewId(R.id.team_list).check(count(TOTAL));
    //check all elements on recycler view
    for (int position = 0; position < TOTAL; position++) {
      User user = USERS.get(position);
      verifyItemPosition(
        user,
        position
      );
    }
  }

  private void verifyItemPosition(User user, int index) {
    verifyOnList(
      user,
      index
    );
    onViewId(R.id.team_list).perform(clickAt(index));
    verifyOnDetail(user);
    pressBack();
  }

  private void verifyOnList(User user, int index) {
    onViewId(R.id.team_list).perform(scrollTo(index));
    onViewId(R.id.team_list).check(item(
      index,
      R.id.team_list_item_name,
      user.name()
    ));
    onViewId(R.id.team_list).check(item(
      index,
      R.id.team_list_item_github,
      user.github()
    ));
    onViewId(R.id.team_list).check(item(
      index,
      R.id.team_list_item_role,
      user.role()
    ));
    onViewId(R.id.team_list).check(item(
      index,
      R.id.team_list_item_status,
      string(R.string.team_list_item_status)
    ));
  }

  private void verifyOnDetail(User user) {
    onViewId(R.id.team_mate_detail_github).check(matches(withText(user.github())));
    onViewId(R.id.team_mate_detail_role).check(matches(withText(user.role())));
  }

  @Test
  public void updateUsersStatus() {
    when(interactor.users()).thenReturn(subject.toFlowable(BackpressureStrategy.LATEST));
    subject.onNext(ALL_USERS_EVENT);
    rule.launchActivity(new Intent());
    subject.onNext(UPDATE_USER_EVENT);
    onViewId(R.id.team_list).perform(scrollTo(2));
    onViewId(R.id.team_list).check(item(
      2,
      R.id.team_list_item_status,
      "Ready"
    ));
  }
}
