package co.iyubinest.mononoke.ui.team.list;

import android.os.Parcel;
import android.os.Parcelable;
import co.iyubinest.mononoke.BuildConfig;
import co.iyubinest.mononoke.data.team.list.RequestTeam;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.squareup.moshi.Moshi;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;
import retrofit2.http.GET;

public class TeamListPresenter {
  private final RequestTeam interactor;
  private final TeamListScreen view;
  private final CompositeDisposable disposable = new CompositeDisposable();

  public TeamListPresenter(RequestTeam interactor, TeamListScreen view) {
    this.interactor = interactor;
    this.view = view;
  }

  public void requestAll() {
    OkHttpClient client = new OkHttpClient.Builder().build();
    Retrofit retrofit = new Retrofit.Builder()
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create()).client(client)
        .baseUrl(BuildConfig.BASE_URL).build();
    Moshi moshi = new Moshi.Builder().build();
    ReceiveSocket socket = new ReceiveSocket(client, BuildConfig.BASE_WS_URL);

    disposable.add(new Team(new HttpTeamRepository(retrofit),
        new HttpRolesRepository(retrofit),
        new ComposedStatusRepository(moshi, socket, cache)).users()
        .subscribe(this::handle, this::error));
  }

  private void handle(TeamEvent event) {
    if (event instanceof TeamEvent.All) {
      handle((TeamEvent.All) event);
    }
    if (event instanceof TeamEvent.Status) {
      handle((TeamEvent.Status) event);
    }
    if (event instanceof TeamEvent.New) {
      handle((TeamEvent.New) event);
    }
    error(new Exception());
  }

  private void handle(TeamEvent.All event) {
    view.show(event.users());
  }

  private void handle(TeamEvent.Status event) {
    view.update(event.user());
  }

  private void handle(TeamEvent.New event) {
    //view.add(event.user());
  }

  private void error(Throwable throwable) {
    throw new IllegalStateException("Not reachable state");
  }

  public void finish() {
    disposable.dispose();
  }

  interface TeamRepository {
    Flowable<List<TeamResponse>> get();
  }

  interface Cache<T> {
    Flowable<T> get();

    void save(T response);
  }

  interface RolesRepository {
    Flowable<Map<String, String>> get();
  }

  interface StatusRepository {
    Flowable<User> get();
  }

  public interface User extends Parcelable {
    String name();

    String avatar();

    String github();

    String role();

    String location();

    String status();

    List<String> languages();

    List<String> tags();
  }

  interface TeamService {
    @GET("/team")
    Flowable<List<TeamResponse>> team();

    @GET("/roles")
    Flowable<Map<String, String>> roles();
  }

  private static class Team {
    private final TeamRepository team;
    private final RolesRepository roles;
    private final StatusRepository status;

    private Team(TeamRepository team, RolesRepository roles,
        StatusRepository status) {
      this.team = team;
      this.roles = roles;
      this.status = status;
    }

    public Flowable<TeamEvent> users() {
      Flowable<TeamEvent.All> allEvents =
          Flowable.zip(team.get(), roles.get(), this::zip);
      Flowable<TeamEvent.Status> statusEvents = status.get().map(this::map);
      return allEvents;
    }

    private TeamEvent.Status map(StatusResponse response) {
      return TeamEvent.Status.with(response);
    }

    private TeamEvent.All zip(List<TeamResponse> team,
        Map<String, String> roles) {
      List<User> users = new ArrayList<>(team.size());
      for (TeamResponse response : team) {
        users.add(userOf(response, roles.get(String.valueOf(response.role))));
      }
      return TeamEvent.All.with(users);
    }

    private User userOf(TeamResponse teamResponse, String role) {
      return BasicUser
          .create(teamResponse.name, teamResponse.avatar, teamResponse.github,
              role, teamResponse.location, "", teamResponse.languages,
              teamResponse.tags);
    }
  }

  static class MemoryCache<T> implements Cache<T> {

    private T response;

    @Override
    public Flowable<T> get() {
      return Flowable.defer(() -> Flowable.just(response))
          .filter(teamResponses -> teamResponses != null);
    }

    @Override
    public void save(T response) {
      this.response = response;
    }
  }

  static class HttpTeamRepository implements TeamRepository {
    private final TeamService service;

    private final Cache<List<TeamResponse>> cache;

    HttpTeamRepository(Retrofit retrofit) {
      service = retrofit.create(TeamService.class);
      cache = new MemoryCache<>();
    }

    @Override
    public Flowable<List<TeamResponse>> get() {
      return Flowable.concat(cache.get(), service.team()).doOnNext(cache::save);
    }
  }

  static class HttpRolesRepository implements RolesRepository {
    private final TeamService service;

    private final Cache<Map<String, String>> cache;

    HttpRolesRepository(Retrofit retrofit) {
      service = retrofit.create(TeamService.class);
      cache = new MemoryCache<>();
    }

    @Override
    public Flowable<Map<String, String>> get() {
      return Flowable.concat(cache.get(), service.roles())
          .doOnNext(cache::save);
    }
  }

  static class ComposedStatusRepository implements StatusRepository {

    private final Moshi moshi;
    private final ReceiveSocket socket;
    private final Cache<List<User>> cache;

    ComposedStatusRepository(Moshi moshi, ReceiveSocket socket,
        Cache<List<User>> cache) {
      this.moshi = moshi;
      this.socket = socket;
      this.cache = cache;
    }

    @Override
    public Flowable<User> get() {
      return socket.get()
          .flatMap(s -> cache.get().flatMap(users -> toEvent(s, users)))
          .doOnError(throwable -> {
          });
    }

    private Flowable<User> toEvent(String responseText, List<User> users) {
      try {
        StatusResponse response =
            moshi.adapter(StatusResponse.class).fromJson(responseText);
        User user = findByName(response.user, users);
        return Flowable.just(user);
      } catch (IOException e) {
        throw new IllegalStateException("response not valid");
      }
    }

    private User findByName(String userName, List<User> users) {
      for (User user : users) {
        if (user.github().equals(userName)) return user;
      }
      throw new IllegalArgumentException("username not found in cache");
    }
  }

  static class ReceiveSocket {

    private final OkHttpClient client;
    private final String url;
    private Flowable<String> flowable;

    ReceiveSocket(OkHttpClient client, String url) {
      this.client = client;
      this.url = url;
      this.flowable =
          Flowable.create(this::create, BackpressureStrategy.LATEST);
    }

    private void create(final FlowableEmitter<String> emitter) {
      final WebSocket socket = client
          .newWebSocket(new Request.Builder().url(url).build(),
              new WebSocketListener() {
                @Override
                public void onMessage(WebSocket webSocket, String text) {
                  emitter.onNext(text);
                }
              });

      emitter.setDisposable(new Disposable() {
        boolean disposed;

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
      return flowable;
    }
  }

  abstract static class TeamEvent {

    static final class All extends TeamEvent {
      private final List<User> users;

      private All(List<User> users) {
        this.users = users;
      }

      static All with(List<User> users) {
        return new All(users);
      }

      public List<User> users() {
        return users;
      }
    }

    static final class Status extends TeamEvent {
      private final User user;

      private Status(User user) {
        this.user = user;
      }

      public static Status with(User user) {
        return new Status(user);
      }

      public User user() {
        return user;
      }
    }

    static final class New extends TeamEvent {
    }

    static final class None extends TeamEvent {
    }
  }

  private static class TeamResponse {
    String name, avatar, github, gender, location;
    List<String> languages, tags;
    Integer role;
  }

  private static class StatusResponse {
    String event;
    String user;
    String state;
  }

  private static class BasicUser implements User {
    public static final Creator<BasicUser> CREATOR = new Creator<BasicUser>() {
      @Override
      public BasicUser createFromParcel(Parcel source) {
        return new BasicUser(source);
      }

      @Override
      public BasicUser[] newArray(int size) {
        return new BasicUser[size];
      }
    };
    private final String name;
    private final String avatar;
    private final String github;
    private final String role;
    private final String location;
    private final String status;
    private final List<String> languages;
    private final List<String> tags;

    private BasicUser(String name, String avatar, String github, String role,
        String location, String status, List<String> languages,
        List<String> tags) {
      this.name = name;
      this.avatar = avatar;
      this.github = github;
      this.role = role;
      this.location = location;
      this.status = status;
      this.languages = languages;
      this.tags = tags;
    }

    protected BasicUser(Parcel in) {
      this.name = in.readString();
      this.avatar = in.readString();
      this.github = in.readString();
      this.role = in.readString();
      this.location = in.readString();
      this.status = in.readString();
      this.languages = in.createStringArrayList();
      this.tags = in.createStringArrayList();
    }

    static User create(String name, String avatar, String github, String role,
        String location, String status, List<String> languages,
        List<String> tags) {
      return new BasicUser(name, avatar, github, role, location, status,
          languages, tags);
    }

    @Override
    public String name() {
      return name;
    }

    @Override
    public String avatar() {
      return avatar;
    }

    @Override
    public String github() {
      return github;
    }

    @Override
    public String role() {
      return role;
    }

    @Override
    public String location() {
      return location;
    }

    @Override
    public String status() {
      return status;
    }

    @Override
    public List<String> languages() {
      return languages;
    }

    @Override
    public List<String> tags() {
      return tags;
    }

    @Override
    public int describeContents() {
      return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(this.name);
      dest.writeString(this.avatar);
      dest.writeString(this.github);
      dest.writeString(this.role);
      dest.writeString(this.location);
      dest.writeString(this.status);
      dest.writeStringList(this.languages);
      dest.writeStringList(this.tags);
    }
  }
}
