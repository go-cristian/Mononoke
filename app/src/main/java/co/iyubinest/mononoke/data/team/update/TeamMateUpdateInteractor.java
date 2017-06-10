package co.iyubinest.mononoke.data.team.update;

import io.reactivex.Completable;

public interface TeamMateUpdateInteractor {
  Completable send(String status);
}
