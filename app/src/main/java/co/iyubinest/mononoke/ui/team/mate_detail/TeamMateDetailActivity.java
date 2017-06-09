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
import co.iyubinest.mononoke.data.team.Mate;
import co.iyubinest.mononoke.data.team.mates.SocketTeamMateUpdate;
import co.iyubinest.mononoke.ui.team.list.TeamListActivity;

import static co.iyubinest.mononoke.common.LoadImage.OPTION.FIT;

public class TeamMateDetailActivity extends BaseActivity
    implements TeamMateDetailScreen {

  public static final String MATE_EXTRA = "MATE_EXTRA";
  @BindView(R.id.mate_detail_toolbar) Toolbar toolbarView;
  @BindView(R.id.mate_detail_avatar) ImageView avatarView;
  @BindView(R.id.mate_detail_github) TextView githubView;
  @BindView(R.id.mate_detail_location) ImageView locationView;
  @BindView(R.id.mate_detail_role) TextView roleView;
  @BindView(R.id.mate_detail_languages) TextView languagesView;
  @BindView(R.id.mate_detail_tags) TextView tagsView;
  @BindView(R.id.mate_detail_status) EditText statusField;

  TeamMateDetailPresenter presenter;

  public static Intent create(Context context, Mate mate) {
    Intent intent = new Intent(context, TeamMateDetailActivity.class);
    intent.putExtra(MATE_EXTRA, mate);
    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.team_mate_detail_activity);
    ButterKnife.bind(this);
    configure(toolbarView);
    showMate(mate());
    SocketTeamMateUpdate interactor =
        new SocketTeamMateUpdate(dependencies().client());
    presenter = new TeamMateDetailPresenter(interactor, this);
  }

  @OnClick(R.id.mate_detail_update)
  public void update() {
    presenter.update();
  }

  @Override
  public Mate mate() {
    return getIntent().getParcelableExtra(MATE_EXTRA);
  }

  @Override
  public String status() {
    return statusField.getText().toString();
  }

  @Override
  public void updateList(String status, Mate mate) {
    setResult(RESULT_OK, TeamListActivity.updateIntent(status, mate));
    finish();
  }

  private void configure(Toolbar toolbarView) {
    setSupportActionBar(toolbarView);
    toolbarView.setNavigationIcon(
        getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
    toolbarView.setNavigationOnClickListener(v -> onBackPressed());
  }

  private void showMate(Mate mate) {
    LoadImage.from(mate.avatar(), avatarView, FIT);
    setTitle(mate.name());
    roleView.setText(mate.role());
    githubView.setText(mate.github());
    languagesView.setText(mate.languages().toString());
    tagsView.setText(mate.tags().toString());
    LoadImage.fromResource(locationView, "flag_" + mate.location(),
        R.drawable.flag__unknown);
  }
}
