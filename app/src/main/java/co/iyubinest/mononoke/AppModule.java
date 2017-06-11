package co.iyubinest.mononoke;

import co.iyubinest.mononoke.data.User;
import co.iyubinest.mononoke.data.roles.HttpRolesRepository;
import co.iyubinest.mononoke.data.roles.RolesRepository;
import co.iyubinest.mononoke.data.team.get.ComposedTeamInteractor;
import co.iyubinest.mononoke.data.team.get.HttpTeamRepository;
import co.iyubinest.mononoke.data.team.get.TeamInteractor;
import co.iyubinest.mononoke.data.team.get.TeamRepository;
import co.iyubinest.mononoke.data.team.get.TeamService;
import co.iyubinest.mononoke.data.updates.SocketUpdatesRepository;
import co.iyubinest.mononoke.data.updates.UpdatesRepository;
import co.iyubinest.mononoke.socket.RxSocket;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.squareup.moshi.Moshi;
import dagger.Module;
import dagger.Provides;
import io.reactivex.Flowable;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Singleton;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

@Module
public class AppModule {

  private final App app;

  public AppModule(App app) {
    this.app = app;
  }

  @Singleton
  @Provides
  public OkHttpClient okHttpClient() {
    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
    interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    return new OkHttpClient.Builder().addInterceptor(new StethoInterceptor())
        .addInterceptor(interceptor).build();
  }

  @Singleton
  @Provides
  public Retrofit retrofit(OkHttpClient client) {
    return new Retrofit.Builder()
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create()).client(client)
        .baseUrl(BuildConfig.BASE_URL).build();
  }

  @Singleton
  @Provides
  public Moshi moshi() {
    return new Moshi.Builder().build();
  }

  @Singleton
  @Provides
  public RxSocket rxSocket(OkHttpClient client) {
    return new RxSocket(client, BuildConfig.BASE_WS_URL);
  }

  @Singleton
  @Provides
  public TeamRepository teamRepository(Retrofit retrofit) {
    return new HttpTeamRepository(retrofit);
  }

  @Singleton
  @Provides
  public TeamInteractor teamInteractor(TeamRepository team,
      RolesRepository roles, UpdatesRepository status) {
    return new ComposedTeamInteractor(team, roles, status);
  }

  @Singleton
  @Provides
  public RolesRepository rolesRepository(Retrofit retrofit) {
    return new HttpRolesRepository(retrofit);
  }

  @Singleton
  @Provides
  public UpdatesRepository updatesRepository(Moshi moshi, RxSocket socket,
      TeamRepository team, RolesRepository roles) {
    final Flowable<List<User>> cache =
        Flowable.zip(team.get(), roles.get(), (teamResponses, rolesMap) -> {
          final List<User> users = new ArrayList<>(teamResponses.size());
          for (TeamService.TeamResponse response : teamResponses) {
            users.add(ComposedTeamInteractor
                .userOf(response, rolesMap.get(String.valueOf(response.role))));
          }
          return users;
        });
    return new SocketUpdatesRepository(moshi, socket, cache, roles);
  }
}
