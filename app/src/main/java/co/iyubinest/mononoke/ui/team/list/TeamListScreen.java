package co.iyubinest.mononoke.ui.team.list;

import java.util.List;

interface TeamListScreen {

  void show(List<TeamListPresenter.User> users);

  void update(TeamListPresenter.User user);
}
