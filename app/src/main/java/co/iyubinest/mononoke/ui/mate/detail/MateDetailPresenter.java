package co.iyubinest.mononoke.ui.mate.detail;

import co.iyubinest.mononoke.data.mates.update.MateUpdate;

class MateDetailPresenter {
  private final MateUpdate interactor;
  private final MateDetailView view;

  public MateDetailPresenter(MateUpdate interactor, MateDetailView view) {
    this.interactor = interactor;
    this.view = view;
  }

  public void update() {
    interactor.send(view.status());
    view.updateList(view.status(), view.mate());
  }
}
