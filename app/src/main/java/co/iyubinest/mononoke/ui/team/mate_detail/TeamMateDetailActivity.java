package co.iyubinest.mononoke.ui.team.mate_detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.iyubinest.mononoke.R;
import co.iyubinest.mononoke.common.BaseActivity;
import co.iyubinest.mononoke.common.LoadImage;
import co.iyubinest.mononoke.data.BasicUser;
import co.iyubinest.mononoke.data.User;
import co.iyubinest.mononoke.data.team.update.TeamMateUpdateInteractor;
import co.iyubinest.mononoke.ui.team.list.TeamListActivity;
import javax.inject.Inject;

import static co.iyubinest.mononoke.common.LoadImage.OPTION.FIT;

public class TeamMateDetailActivity extends BaseActivity
    implements TeamMateDetailScreen {

  public static final String MATE_EXTRA = "MATE_EXTRA";

  @BindView(R.id.team_mate_detail_toolbar) Toolbar toolbarView;
  @BindView(R.id.team_mate_detail_avatar) ImageView avatarView;
  @BindView(R.id.team_mate_detail_github) TextView githubView;
  @BindView(R.id.team_mate_detail_location) ImageView locationView;
  @BindView(R.id.team_mate_detail_role) TextView roleView;
  @BindView(R.id.team_mate_detail_languages) TextView languagesView;
  @BindView(R.id.team_mate_detail_tags) TextView tagsView;
  @BindView(R.id.team_mate_detail_status) EditText statusField;

  @Inject TeamMateUpdateInteractor interactor;

  private TeamMateDetailPresenter presenter;

  public static Intent intent(Context context, User user) {
    Intent intent = new Intent(context, TeamMateDetailActivity.class);
    intent.putExtra(MATE_EXTRA, user);
    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.team_mate_detail_activity);
    ButterKnife.bind(this);
    appComponent().teamMateDetailComponent(new TeamMateDetailModule())
        .inject(this);
    configure(toolbarView);
    show(user());
    presenter = new TeamMateDetailPresenter(this, interactor);
  }

  @OnClick(R.id.team_mate_detail_update)
  public void update() {
    presenter.update();
  }

  @Override
  public User user() {
    return getIntent().getParcelableExtra(MATE_EXTRA);
  }

  @Override
  public void error(Throwable throwable) {

  }

  @Override
  public String status() {
    return statusField.getText().toString();
  }

  @Override
  public void updateList(final String status, final User user) {
    final User newUser = BasicUser
        .create(user.name(), user.avatar(), user.github(), user.role(),
            user.location(), status, user.languages(), user.tags());
    setResult(RESULT_OK, TeamListActivity.updateIntent(newUser));
    finish();
  }

  private void configure(final Toolbar toolbarView) {
    setSupportActionBar(toolbarView);
    toolbarView.setNavigationIcon(
        getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
    toolbarView.setNavigationOnClickListener(v -> onBackPressed());
  }

  private void show(final User user) {
    LoadImage.from(user.avatar(), avatarView, FIT);
    setTitle(user.name());
    roleView.setText(user.role());
    githubView.setText(user.github());
    languagesView.setText(user.languages().toString());
    tagsView.setText(user.tags().toString());
    LoadImage.fromResource(locationView, "flag_" + user.location(),
        R.drawable.flag__unknown);
  }
}
