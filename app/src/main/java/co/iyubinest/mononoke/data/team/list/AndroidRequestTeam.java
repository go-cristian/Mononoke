package co.iyubinest.mononoke.data.team.list;

import co.iyubinest.mononoke.data.team.Mate;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java.util.List;

public class AndroidRequestTeam implements RequestTeam {
  private final RetrofitRequestTeam interactor;

  public AndroidRequestTeam(RetrofitRequestTeam interactor) {
    this.interactor = interactor;
  }

  @Override
  public Flowable<List<Mate>> connect() {
    return interactor.connect().subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
  }

  @Override
  public Flowable<RetrofitRequestTeam.UpdateEvent> subscribeUpdates() {
    return interactor.subscribeUpdates().subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
  }
}
