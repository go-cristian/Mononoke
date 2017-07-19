package co.iyubinest.mononoke.common;
import android.util.Pair;
import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import java.util.concurrent.TimeUnit;
import org.reactivestreams.Publisher;

public class RxUtils {

  private static final int UNCHECKED_ERROR_TYPE_CODE = -100;
  private static final int NUM_RETRIES = 3;
  private static final double INITIAL_DELAY = 2;

  public static Function<? super Flowable<Throwable>, ? extends Publisher<?>> incremental() {
    return errors -> errors.zipWith(Flowable.range(1, NUM_RETRIES + 1),
        (error, integer) -> new Pair<>(error, UNCHECKED_ERROR_TYPE_CODE)).flatMap(RxUtils::delayed);
  }

  private static Flowable<Long> delayed(Pair<Throwable, Integer> pair) {
    int retryAttempt = pair.second;
    if (retryAttempt == UNCHECKED_ERROR_TYPE_CODE) {
      return Flowable.error(pair.first);
    }
    long currentDelay = (long) Math.pow(INITIAL_DELAY, retryAttempt);
    return Flowable.timer(currentDelay, TimeUnit.SECONDS);
  }
}
