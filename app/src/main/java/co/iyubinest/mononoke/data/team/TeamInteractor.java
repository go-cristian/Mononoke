package co.iyubinest.mononoke.data.team;

import co.iyubinest.mononoke.data.TeamEvent;
import io.reactivex.Flowable;

public interface TeamInteractor {
  Flowable<TeamEvent> users();
}
