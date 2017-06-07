package co.iyubinest.mononoke.data.mates.list;

import co.iyubinest.mononoke.BuildConfig;
import co.iyubinest.mononoke.data.mates.Mate;
import com.squareup.moshi.Moshi;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import retrofit2.Retrofit;
import retrofit2.http.GET;

public class RetrofitRequestMates implements RequestMates {

  private final TeamApi api;
  private final OkHttpClient client;
  private Map<String, String> cache;

  public RetrofitRequestMates(Retrofit retrofit, OkHttpClient client) {
    this.api = retrofit.create(TeamApi.class);
    this.client = client;
  }

  @Override
  public Flowable<List<Mate>> connect() {
    return Flowable.zip(api.fetchAll(), api.roles(), this::zip);
  }

  private List<Mate> zip(List<TeamResponse> team, Map<String, String> roles) {
    cache = roles;
    List<Mate> mates = new ArrayList<>(team.size());
    for (TeamResponse mate : team) {
      mates.add(map(mate, roles));
    }
    return mates;
  }

  private Mate map(TeamResponse mate, Map<String, String> roles) {
    return new Mate(mate.name, mate.avatar, mate.github,
        Mate.Gender.from(mate.gender), mate.location, roles.get(mate.role + ""),
        "", mate.languages, mate.tags);
  }

  @Override
  public Flowable<UpdateEvent> subscribeUpdates() {
    return Flowable.create(e -> {

      final WebSocketListener listener = new WebSocketListener() {
        @Override
        public void onMessage(WebSocket webSocket, String text) {
          e.onNext(parse(text));
        }

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
          super.onOpen(webSocket, response);
        }
      };
      final WebSocket socket = client.newWebSocket(
          new Request.Builder().url(BuildConfig.BASE_WS_URL).build(), listener);
      e.setDisposable(new Disposable() {

        public boolean disposed;

        @Override
        public void dispose() {
          socket.close(1000, "finished");
          disposed = true;
        }

        @Override
        public boolean isDisposed() {
          return disposed;
        }
      });
    }, BackpressureStrategy.LATEST);
  }

  private UpdateEvent parse(String s) {
    Moshi moshi = new Moshi.Builder().build();
    try {
      NewStatusEvent newStatusEvent =
          moshi.adapter(NewStatusEvent.class).fromJson(s);
      if (newStatusEvent.state == null || newStatusEvent.user == null) {
        NewUserResponse newUserEvent =
            moshi.adapter(NewUserResponse.class).fromJson(s);
        if (newUserEvent.user == null) {
          return new UpdateEvent();
        }
        return map(newUserEvent);
      }
      return newStatusEvent;
    } catch (IOException e) {
      try {
        NewUserResponse newUserEvent =
            moshi.adapter(NewUserResponse.class).fromJson(s);
        e.printStackTrace();
        return map(newUserEvent);
      } catch (IOException e1) {
        e1.printStackTrace();
        return new UpdateEvent();
      }
    }
  }

  private NewUserEvent map(NewUserResponse response) {
    NewUserEvent event = new NewUserEvent();
    event.user =
        new Mate(response.user.name, response.user.avatar, response.user.github,
            Mate.Gender.from(response.user.gender), response.user.location,
            cache.get(response.user.role + ""), "", response.user.languages,
            response.user.tags);
    return event;
  }

  static class NewUserResponse {
    TeamResponse user;
  }

  interface TeamApi {

    @GET("/team")
    Flowable<List<TeamResponse>> fetchAll();

    @GET("/roles")
    Flowable<Map<String, String>> roles();
  }

  private static class TeamResponse {
    String name, avatar, github, gender, location;
    int role;
    List<String> languages, tags;
  }
}
