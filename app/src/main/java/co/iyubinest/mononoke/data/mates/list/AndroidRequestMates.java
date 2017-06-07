package co.iyubinest.mononoke.data.mates.list;

import co.iyubinest.mononoke.data.mates.Mate;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java.util.List;

public class AndroidRequestMates implements RequestMates {
  private final RetrofitRequestMates interactor;

  public AndroidRequestMates(RetrofitRequestMates interactor) {
    this.interactor = interactor;
  }

  @Override
  public Flowable<List<Mate>> connect() {
    return interactor.connect().subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
  }

  @Override
  public Flowable<RetrofitRequestMates.UpdateEvent> subscribeUpdates() {
    return interactor.subscribeUpdates().subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
  }
}
