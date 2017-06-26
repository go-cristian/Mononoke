package co.iyubinest.mononoke.ui.team.list;

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
import com.squareup.moshi.Moshi;
import dagger.Module;
import dagger.Provides;
import io.reactivex.Flowable;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Retrofit;

@SuppressWarnings("Convert2streamapi")
@Module
public class TeamListModule {
  private final TeamListActivity activity;

  TeamListModule(TeamListActivity activity) {
    this.activity = activity;
  }

  @Provides
  TeamListScreen teamListScreen() {
    return activity;
  }

  @Provides
  TeamRepository teamRepository(Retrofit retrofit) {
    return new HttpTeamRepository(retrofit);
  }

  @Provides
  TeamInteractor teamInteractor(TeamRepository team, RolesRepository roles,
      UpdatesRepository status) {
    return new ComposedTeamInteractor(team, roles, status);
  }

  @Provides
  RolesRepository rolesRepository(Retrofit retrofit) {
    return new HttpRolesRepository(retrofit);
  }

  @Provides
  UpdatesRepository updatesRepository(Moshi moshi, RxSocket socket,
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
