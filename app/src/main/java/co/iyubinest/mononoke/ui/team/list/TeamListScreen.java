package co.iyubinest.mononoke.ui.team.list;

import co.iyubinest.mononoke.data.team.Mate;
import co.iyubinest.mononoke.data.team.list.RequestTeam;
import java.util.List;

interface TeamListScreen {
  void showAll(List<Mate> mates);

  void showError(Throwable throwable);

  void update(RequestTeam.NewStatusEvent event);

  void update(RequestTeam.NewUserEvent event);
}
