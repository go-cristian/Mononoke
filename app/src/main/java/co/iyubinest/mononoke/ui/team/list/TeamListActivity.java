package co.iyubinest.mononoke.ui.team.list;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import butterknife.BindView;
import butterknife.ButterKnife;
import co.iyubinest.mononoke.R;
import co.iyubinest.mononoke.common.BaseActivity;
import co.iyubinest.mononoke.data.User;
import co.iyubinest.mononoke.ui.team.mate_detail.TeamMateDetailActivity;
import java.util.List;

public class TeamListActivity extends BaseActivity implements TeamListScreen {

  public static final String USER_EXTRA = "USER_EXTRA";
  private static final int REQUEST_CODE = 100;

  @BindView(R.id.loading) View loadingView;
  @BindView(R.id.mate_list) TeamListWidget teamListWidget;

  private TeamListPresenter presenter;

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
    presenter = new TeamListPresenter(this, dependencies().teamInteractor());
    presenter.requestAll();
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
  public void update(final User user) {
    teamListWidget.update(user);
  }

  private void show(final User user) {
    startActivityForResult(TeamMateDetailActivity.intent(this, user),
        REQUEST_CODE);
  }
}
