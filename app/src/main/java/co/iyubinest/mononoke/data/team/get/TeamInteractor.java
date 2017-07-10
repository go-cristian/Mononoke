package co.iyubinest.mononoke.data.team.get;
import co.iyubinest.mononoke.data.TeamEvent;
import io.reactivex.Flowable;

public interface TeamInteractor {
  Flowable<TeamEvent> users();
}
