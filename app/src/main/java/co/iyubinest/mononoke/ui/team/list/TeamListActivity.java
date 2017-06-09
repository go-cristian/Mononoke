package co.iyubinest.mononoke.ui.team.list;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import butterknife.BindView;
import butterknife.ButterKnife;
import co.iyubinest.mononoke.R;
import co.iyubinest.mononoke.common.BaseActivity;
import co.iyubinest.mononoke.data.team.Mate;
import co.iyubinest.mononoke.data.team.list.AndroidRequestTeam;
import co.iyubinest.mononoke.data.team.list.RetrofitRequestTeam;
import co.iyubinest.mononoke.ui.team.mate_detail.TeamMateDetailActivity;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

public class TeamListActivity extends BaseActivity implements TeamListScreen {

  public static final String USER_EXTRA = "USER_EXTRA";
  public static final String STATUS_EXTRA = "STATUS_EXTRA";
  private static final int REQUEST_CODE = 100;
  @BindView(R.id.loading) View loadingView;
  @BindView(R.id.mate_list) TeamListWidget teamListWidget;

  TeamListPresenter presenter;

  public static Intent updateIntent(String status, Mate mate) {
    Intent intent = new Intent();
    intent.putExtra(USER_EXTRA, mate.github());
    intent.putExtra(STATUS_EXTRA, status);
    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.team_list_activity);
    ButterKnife.bind(this);
    teamListWidget.onUserSelected(this::showMate);
    presenter = new TeamListPresenter(buildInteractor(), this);
    presenter.requestAll();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode,
      Intent data) {
    if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
      /*update(new RequestTeam.NewStatusEvent(data.getStringExtra(USER_EXTRA),
          data.getStringExtra(STATUS_EXTRA)));*/
    }
  }

  @Override
  protected void onDestroy() {
    presenter.finish();
    super.onDestroy();
  }


  /*

  @Override
  public void showAll(List<Mate> mates) {
    loadingView.setVisibility(View.GONE);
    teamListWidget.setVisibility(View.VISIBLE);
    teamListWidget.show(mates);
  }

  @Override
  public void update(RequestTeam.NewStatusEvent event) {
    teamListWidget.updateStatus(event);
  }

  @Override
  public void update(RequestTeam.NewUserEvent event) {
    teamListWidget.add(event.user());
  }

  @Override
  public void showError(Throwable throwable) {
    loadingView.setVisibility(View.GONE);
    Toast.makeText(this, R.string.team_list_error, Toast.LENGTH_SHORT).show();
  }
  */

  private void showMate(TeamListPresenter.User user) {
    startActivityForResult(TeamMateDetailActivity.create(this, user),
        REQUEST_CODE);
  }

  private AndroidRequestTeam buildInteractor() {
    OkHttpClient client = dependencies().client();
    Retrofit retrofit = dependencies().retrofit();
    return new AndroidRequestTeam(new RetrofitRequestTeam(retrofit, client));
  }

  @Override
  public void show(List<TeamListPresenter.User> users) {
    loadingView.setVisibility(View.GONE);
    teamListWidget.setVisibility(View.VISIBLE);
    teamListWidget.show(users);
  }

  @Override
  public void update(TeamListPresenter.User user) {
    teamListWidget.update(user);
  }
}
