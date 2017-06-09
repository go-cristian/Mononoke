package co.iyubinest.mononoke.data.team.mates;

import co.iyubinest.mononoke.BuildConfig;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class SocketTeamMateUpdate implements TeamMateUpdate {
  private final WebSocket socket;

  public SocketTeamMateUpdate(OkHttpClient client) {
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
