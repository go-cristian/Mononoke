package co.iyubinest.mononoke.cache;

import android.util.Log;
import io.reactivex.Flowable;

public class MemoryCache<T> implements Cache<T> {

  private T response;

  @Override
  public Flowable<T> get() {
    return Flowable.defer(() -> {
      if (response == null) {
        return Flowable.empty();
      } else {
        return Flowable.just(response);
      }
    });
  }

  @Override
  public void save(final T response) {
    Log.v("Cache", response.toString());
    this.response = response;
  }
}
