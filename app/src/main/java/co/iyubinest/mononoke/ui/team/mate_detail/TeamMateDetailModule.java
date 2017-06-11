package co.iyubinest.mononoke.ui.team.mate_detail;

import co.iyubinest.mononoke.data.team.update.ComposedTeamMateInteractor;
import co.iyubinest.mononoke.data.team.update.TeamMateUpdateInteractor;
import co.iyubinest.mononoke.socket.RxSocket;
import dagger.Module;
import dagger.Provides;

@Module
public class TeamMateDetailModule {
  @Provides
  public TeamMateUpdateInteractor teamMateUpdateInteractor(RxSocket socket) {
    return new ComposedTeamMateInteractor(socket);
  }
}
