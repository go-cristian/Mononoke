package co.iyubinest.mononoke.common;

import co.iyubinest.mononoke.BuildConfig;
import co.iyubinest.mononoke.data.User;
import co.iyubinest.mononoke.data.roles.HttpRolesRepository;
import co.iyubinest.mononoke.data.roles.RolesRepository;
import co.iyubinest.mononoke.data.team.ComposedTeamInteractor;
import co.iyubinest.mononoke.data.team.HttpTeamRepository;
import co.iyubinest.mononoke.data.team.TeamInteractor;
import co.iyubinest.mononoke.data.team.TeamRepository;
import co.iyubinest.mononoke.data.team.TeamService;
import co.iyubinest.mononoke.data.team.update.ComposedTeamMateInteractor;
import co.iyubinest.mononoke.data.team.update.TeamMateUpdateInteractor;
import co.iyubinest.mononoke.data.updates.SocketUpdatesRepository;
import co.iyubinest.mononoke.data.updates.UpdatesRepository;
import co.iyubinest.mononoke.socket.RxSocket;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.squareup.moshi.Moshi;
import io.reactivex.Flowable;
import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class Dependencies {
  private final Retrofit retrofit;
  private final OkHttpClient client;
  private final Moshi moshi;
  private final RxSocket socket;

  public Dependencies() {
    moshi = moshi();
    client = client();
    retrofit = retrofit();
    socket = socket();
  }

  public TeamInteractor teamInteractor() {
    TeamRepository team = teamRepository();
    RolesRepository roles = rolesRepository();
    UpdatesRepository status = statusRepository(team, roles);
    return new ComposedTeamInteractor(team, roles, status);
  }

  private RxSocket socket() {
    return new RxSocket(client, BuildConfig.BASE_WS_URL);
  }

  private TeamRepository teamRepository() {
    return new HttpTeamRepository(retrofit);
  }

  private RolesRepository rolesRepository() {
    return new HttpRolesRepository(retrofit);
  }

  private UpdatesRepository statusRepository(final TeamRepository team,
      final RolesRepository roles) {
    final Flowable<List<User>> cache =
        Flowable.zip(team.get(), roles.get(), (teamResponses, rolesMap) -> {
          final List<User> users = new ArrayList<>(teamResponses.size());
          for (TeamService.TeamResponse response : teamResponses) {
            users.add(ComposedTeamInteractor
                .userOf(response, rolesMap.get(String.valueOf(response.role))));
          }
          return users;
        });
    return new SocketUpdatesRepository(moshi, socket, cache, rolesRepository());
  }

  private Moshi moshi() {
    return new Moshi.Builder().build();
  }

  private OkHttpClient client() {
    final HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
    interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    return new OkHttpClient.Builder().addInterceptor(new StethoInterceptor())
        .addInterceptor(interceptor).build();
  }

  private Retrofit retrofit() {
    return new Retrofit.Builder()
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create()).client(client)
        .baseUrl(BuildConfig.BASE_URL).build();
  }

  public TeamMateUpdateInteractor teamMateInteractor() {
    return new ComposedTeamMateInteractor(socket);
  }
}
