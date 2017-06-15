package co.iyubinest.mononoke.ui.team.list;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import co.iyubinest.mononoke.R;
import co.iyubinest.mononoke.common.BaseActivity;
import co.iyubinest.mononoke.data.User;
import co.iyubinest.mononoke.ui.team.mate_detail.TeamMateDetailActivity;
import java.util.List;
import javax.inject.Inject;

public class TeamListActivity extends BaseActivity implements TeamListScreen {

  public static final String USER_EXTRA = "USER_EXTRA";
  private static final int REQUEST_CODE = 100;
  private static final String USERS = "Users_Extra";

  @Inject TeamListPresenter presenter;
  @BindView(R.id.loading) View loadingView;
  @BindView(R.id.team_list) TeamListWidget teamListWidget;

  public static Intent updateIntent(final User user) {
    Intent intent = new Intent();
    intent.putExtra(USER_EXTRA, user);
    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.team_list_activity);

    ButterKnife.bind(this);
    teamListWidget.onUserSelected(this::show);

    appComponent().teamListComponent(new TeamListModule(this)).inject(this);
    presenter.requestAll();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    outState.putParcelableArrayList("Users", teamListWidget.users());
    super.onSaveInstanceState(outState);
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    teamListWidget.show(savedInstanceState.getParcelableArrayList("Users"));
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode,
      Intent data) {
    if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
      update(data.getParcelableExtra(USER_EXTRA));
    }
  }

  @Override
  protected void onDestroy() {
    presenter.finish();
    super.onDestroy();
  }

  @Override
  public void show(final List<User> users) {
    loadingView.setVisibility(View.GONE);
    teamListWidget.setVisibility(View.VISIBLE);
    teamListWidget.show(users);
  }

  @Override
  public void add(final User user) {
    teamListWidget.add(user);
  }

  @Override
  public void error(String message) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
  }

  @Override
  public void update(final User user) {
    teamListWidget.update(user);
  }

  private void show(final User user) {
    startActivityForResult(TeamMateDetailActivity.intent(this, user),
        REQUEST_CODE);
  }
}
