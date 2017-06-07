package co.iyubinest.mononoke.ui.mate.list;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import co.iyubinest.mononoke.R;
import co.iyubinest.mononoke.common.BaseActivity;
import co.iyubinest.mononoke.data.mates.Mate;
import co.iyubinest.mononoke.data.mates.list.AndroidRequestMates;
import co.iyubinest.mononoke.data.mates.list.RequestMates;
import co.iyubinest.mononoke.data.mates.list.RetrofitRequestMates;
import co.iyubinest.mononoke.ui.mate.detail.MateDetailActivity;
import io.reactivex.disposables.CompositeDisposable;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

public class MateListActivity extends BaseActivity implements MatesListView {

  private static final int REQUEST_CODE = 100;
  public static final String USER_EXTRA = "USER_EXTRA";
  public static final String STATUS_EXTRA = "STATUS_EXTRA";

  @BindView(R.id.loading) View loadingView;
  @BindView(R.id.mate_list) MatesWidget matesWidget;

  CompositeDisposable disposables = new CompositeDisposable();
  MatesPresenter presenter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.mate_list_activity);
    ButterKnife.bind(this);
    matesWidget.onMateSelected(this::showMate);
    presenter = new MatesPresenter(buildInteractor(), this);
    presenter.requestAll();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode,
      Intent data) {
    if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
      update(new RequestMates.NewStatusEvent(data.getStringExtra(USER_EXTRA),
          data.getStringExtra(STATUS_EXTRA)));
    }
  }

  @Override
  protected void onDestroy() {
    presenter.unsubscribeUpdates();
    super.onDestroy();
  }

  @Override
  public void showAll(List<Mate> mates) {
    loadingView.setVisibility(View.GONE);
    matesWidget.setVisibility(View.VISIBLE);
    matesWidget.show(mates);
  }

  @Override
  public void update(RequestMates.NewStatusEvent event) {
    matesWidget.updateStatus(event);
  }

  @Override
  public void update(RequestMates.NewUserEvent event) {
    matesWidget.add(event.user());
  }

  @Override
  public void showError(Throwable throwable) {
    loadingView.setVisibility(View.GONE);
    Toast.makeText(this, R.string.mate_list_error, Toast.LENGTH_SHORT).show();
  }

  private void showMate(Mate mate) {
    startActivityForResult(MateDetailActivity.create(this, mate), REQUEST_CODE);
  }

  private AndroidRequestMates buildInteractor() {
    OkHttpClient client = dependencies().client();
    Retrofit retrofit = dependencies().retrofit();
    return new AndroidRequestMates(new RetrofitRequestMates(retrofit, client));
  }

  public static Intent updateIntent(String status, Mate mate) {
    Intent intent = new Intent();
    intent.putExtra(USER_EXTRA, mate.github());
    intent.putExtra(STATUS_EXTRA, status);
    return intent;
  }
}
