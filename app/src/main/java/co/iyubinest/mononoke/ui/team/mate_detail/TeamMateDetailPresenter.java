package co.iyubinest.mononoke.ui.team.mate_detail;

import co.iyubinest.mononoke.data.team.update.TeamMateUpdateInteractor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

class TeamMateDetailPresenter {

  private final TeamMateDetailScreen view;
  private final TeamMateUpdateInteractor interactor;

  TeamMateDetailPresenter(TeamMateDetailScreen view,
      TeamMateUpdateInteractor interactor) {
    this.view = view;
    this.interactor = interactor;
  }

  public void update() {
    interactor.send(view.status()).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(() -> view.updateList(view.status(), view.user()),
            view::error);
  }
}
