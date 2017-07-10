package co.iyubinest.mononoke.data.updates;
import co.iyubinest.mononoke.data.TeamEvent;
import io.reactivex.Flowable;

public interface UpdatesRepository {
  Flowable<TeamEvent> get();
}
