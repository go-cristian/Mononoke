package co.iyubinest.mononoke.ui.mate.list;

import co.iyubinest.mononoke.data.mates.list.RequestMates;
import io.reactivex.disposables.CompositeDisposable;

class MatesPresenter {
  private final RequestMates interactor;
  private final MatesListView view;
  private final CompositeDisposable disposable = new CompositeDisposable();

  public MatesPresenter(RequestMates interactor, MatesListView view) {
    this.interactor = interactor;
    this.view = view;
  }

  public void requestAll() {
    disposable.add(interactor.connect().subscribe(mates -> {
      subscribeUpdates();
      view.showAll(mates);
    }, view::showError));
  }

  private void subscribeUpdates() {
    disposable.add(
        interactor.subscribeUpdates().subscribe(this::update, view::showError));
  }

  private void update(RequestMates.UpdateEvent event) {
    if (event instanceof RequestMates.NewUserEvent) {
      view.update((RequestMates.NewUserEvent) event);
    } else if (event instanceof RequestMates.NewStatusEvent) {
      view.update((RequestMates.NewStatusEvent) event);
    }
  }

  public void unsubscribeUpdates() {
    disposable.dispose();
  }
}
