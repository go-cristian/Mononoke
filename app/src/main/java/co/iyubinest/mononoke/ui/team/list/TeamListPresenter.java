package co.iyubinest.mononoke.ui.team.list;

import co.iyubinest.mononoke.data.team.list.RequestTeam;
import io.reactivex.disposables.CompositeDisposable;

class TeamListPresenter {
  private final RequestTeam interactor;
  private final TeamListScreen view;
  private final CompositeDisposable disposable = new CompositeDisposable();

  TeamListPresenter(RequestTeam interactor, TeamListScreen view) {
    this.interactor = interactor;
    this.view = view;
  }

  void requestAll() {
    disposable.add(interactor.connect().doAfterNext(mates -> subscribeUpdates())
        .subscribe(view::showAll, view::showError));
  }

  private void subscribeUpdates() {
    disposable.add(
        interactor.subscribeUpdates().subscribe(this::update, view::showError));
  }

  private void update(RequestTeam.UpdateEvent event) {
    if (event instanceof RequestTeam.NewUserEvent) {
      view.update((RequestTeam.NewUserEvent) event);
    } else if (event instanceof RequestTeam.NewStatusEvent) {
      view.update((RequestTeam.NewStatusEvent) event);
    }
  }

  void unsubscribeUpdates() {
    disposable.dispose();
  }
}
