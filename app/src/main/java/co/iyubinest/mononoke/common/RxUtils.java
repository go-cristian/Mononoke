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

    return errors -> errors
        .zipWith(Flowable.range(1, NUM_RETRIES + 1), (error, integer) -> {
          if (integer == NUM_RETRIES + 1) {
            return new Pair<>(error, UNCHECKED_ERROR_TYPE_CODE);
          }
          return new Pair<>(error, UNCHECKED_ERROR_TYPE_CODE);
        }).flatMap(errorRetryCountTuple -> {

          int retryAttempt = errorRetryCountTuple.second;

          // If not a known error type, pass the error through.
          if (retryAttempt == UNCHECKED_ERROR_TYPE_CODE) {
            return Flowable.error(errorRetryCountTuple.first);
          }

          long delay = (long) Math.pow(INITIAL_DELAY, retryAttempt);

          // Else, exponential backoff for the passed in error types.
          return Flowable.timer(delay, TimeUnit.SECONDS);
        });
  }
}
