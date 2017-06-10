package co.iyubinest.mononoke.data.team.update;

import co.iyubinest.mononoke.socket.RxSocket;
import io.reactivex.Completable;

public class ComposedTeamMateInteractor implements TeamMateUpdateInteractor {

  private final RxSocket socket;

  public ComposedTeamMateInteractor(RxSocket socket) {
    this.socket = socket;
  }

  @Override
  public Completable send(String status) {
    return socket.send(status);
  }
}
