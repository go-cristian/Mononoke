package co.iyubinest.mononoke.cache;
import io.reactivex.Flowable;

public interface Cache<T> {
  Flowable<T> get();
  void save(final T response);
}
