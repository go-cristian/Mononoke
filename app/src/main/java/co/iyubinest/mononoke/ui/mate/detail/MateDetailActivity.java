package co.iyubinest.mononoke.ui.mate.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.iyubinest.mononoke.R;
import co.iyubinest.mononoke.common.BaseActivity;
import co.iyubinest.mononoke.common.LoadImage;
import co.iyubinest.mononoke.data.mates.Mate;
import co.iyubinest.mononoke.data.mates.update.SocketMateUpdate;
import co.iyubinest.mononoke.ui.mate.list.MateListActivity;

import static co.iyubinest.mononoke.common.LoadImage.OPTION.FIT;

public class MateDetailActivity extends BaseActivity implements MateDetailView {

  public static final String MATE_EXTRA = "MATE_EXTRA";
  @BindView(R.id.mate_detail_name) TextView nameView;
  @BindView(R.id.mate_detail_avatar) ImageView avatarView;
  @BindView(R.id.mate_detail_github) TextView githubView;
  @BindView(R.id.mate_detail_location) ImageView locationView;
  @BindView(R.id.mate_detail_role) TextView roleView;
  @BindView(R.id.mate_detail_languages) TextView languagesView;
  @BindView(R.id.mate_detail_tags) TextView tagsView;
  @BindView(R.id.mate_detail_status) EditText statusField;

  MateDetailPresenter presenter;

  public static Intent create(Context context, Mate mate) {
    Intent intent = new Intent(context, MateDetailActivity.class);
    intent.putExtra(MATE_EXTRA, mate);
    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.mate_detail_activity);
    ButterKnife.bind(this);
    showMate(mate());
    SocketMateUpdate interactor = new SocketMateUpdate(dependencies().client());
    presenter = new MateDetailPresenter(interactor, this);
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
    setResult(RESULT_OK, MateListActivity.updateIntent(status, mate));
    finish();
  }

  private void showMate(Mate mate) {
    LoadImage.from(mate.avatar(), avatarView, FIT);
    nameView.setText(mate.name());
    roleView.setText(mate.role());
    githubView.setText(mate.github());
    languagesView.setText(mate.languages().toString());
    tagsView.setText(mate.tags().toString());
    LoadImage.fromResource(locationView, "flag_" + mate.location(),
        R.drawable.flag__unknown);
  }
}
