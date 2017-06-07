package co.iyubinest.mononoke.data.mates.update;

import co.iyubinest.mononoke.BuildConfig;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class SocketMateUpdate implements MateUpdate {
  private final WebSocket socket;

  public SocketMateUpdate(OkHttpClient client) {
    socket = client.newWebSocket(
        new Request.Builder().url(BuildConfig.BASE_WS_URL).build(),
        new WebSocketListener() {
        });
  }

  @Override
  public void send(String msg) {
    socket.send(msg);
  }
}
