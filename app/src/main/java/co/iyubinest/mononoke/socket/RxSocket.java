package co.iyubinest.mononoke.socket;

import android.util.Log;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.disposables.Disposable;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class RxSocket {

  private final OkHttpClient client;
  private final String url;
  private final Flowable<String> receive;

  public RxSocket(OkHttpClient client, String url) {
    this.client = client;
    this.url = url;
    this.receive = Flowable.create(this::receiver, BackpressureStrategy.BUFFER);
  }

  private void receiver(final FlowableEmitter<String> emitter) {
    final WebSocket socket = client
        .newWebSocket(new Request.Builder().url(url).build(),
            new WebSocketListener() {
              @Override
              public void onMessage(final WebSocket webSocket,
                  final String text) {
                Log.v("WebSocket", text);
                emitter.onNext(text);
              }
            });

    emitter.setDisposable(new Disposable() {
      private boolean disposed;

      @Override
      public void dispose() {
        socket.close(1000, "Closed");
        disposed = true;
      }

      @Override
      public boolean isDisposed() {
        return disposed;
      }
    });
  }

  public Flowable<String> get() {
    return receive;
  }

  public Completable send(final String message) {
    return Completable.create(emitter -> send(message, emitter));
  }

  private void send(final String message, final CompletableEmitter emitter) {
    final WebSocket socket = client
        .newWebSocket(new Request.Builder().url(url).build(),
            new WebSocketListener() {
              @Override
              public void onMessage(final WebSocket webSocket,
                  final String text) {
                Log.v("WebSocket", text);
                if (text.contains(message)) emitter.onComplete();
              }

              @Override
              public void onFailure(WebSocket webSocket, Throwable t,
                  Response response) {
                Log.e("WebSocket", t.getMessage());
                emitter.onError(t);
              }
            });

    emitter.setDisposable(new Disposable() {

      private boolean disposed;

      @Override
      public void dispose() {
        socket.close(1000, "Closed");
        disposed = true;
      }

      @Override
      public boolean isDisposed() {
        return disposed;
      }
    });
    socket.send(message);
  }
}
