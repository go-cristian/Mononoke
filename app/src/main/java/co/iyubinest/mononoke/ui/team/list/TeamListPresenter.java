package co.iyubinest.mononoke.ui.team.list;

import co.iyubinest.mononoke.data.TeamEvent;
import co.iyubinest.mononoke.data.team.TeamInteractor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class TeamListPresenter {

  private final CompositeDisposable disposable = new CompositeDisposable();
  private final TeamListScreen view;
  private final TeamInteractor interactor;

  public TeamListPresenter(TeamListScreen view, TeamInteractor interactor) {
    this.view = view;
    this.interactor = interactor;
  }

  public void requestAll() {
    disposable.add(interactor.users().subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(this::success, this::error));
  }

  private void success(final TeamEvent event) {
    if (event instanceof TeamEvent.All) {
      success((TeamEvent.All) event);
    } else if (event instanceof TeamEvent.Status) {
      success((TeamEvent.Status) event);
    } else if (event instanceof TeamEvent.New) {
      success((TeamEvent.New) event);
    } else if (event instanceof TeamEvent.None) {

    } else {
      error(new Exception());
    }
  }

  private void success(final TeamEvent.All event) {
    view.show(event.users());
  }

  private void success(final TeamEvent.Status event) {
    view.update(event.user());
  }

  private void success(final TeamEvent.New event) {
    view.add(event.user());
  }

  private void error(final Throwable throwable) {

  }

  public void finish() {
    disposable.dispose();
  }
}
