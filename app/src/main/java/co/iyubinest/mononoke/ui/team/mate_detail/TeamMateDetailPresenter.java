package co.iyubinest.mononoke.ui.team.mate_detail;

import co.iyubinest.mononoke.data.team.mates.TeamMateUpdate;

class TeamMateDetailPresenter {
  private final TeamMateUpdate interactor;
  private final TeamMateDetailScreen view;

  TeamMateDetailPresenter(TeamMateUpdate interactor,
      TeamMateDetailScreen view) {
    this.interactor = interactor;
    this.view = view;
  }

  public void update() {
    interactor.send(view.status());
    view.updateList(view.status(), view.mate());
  }
}
