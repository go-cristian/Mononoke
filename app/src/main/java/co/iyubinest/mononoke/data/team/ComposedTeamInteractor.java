package co.iyubinest.mononoke.data.team;

import android.util.Log;
import co.iyubinest.mononoke.data.BasicUser;
import co.iyubinest.mononoke.data.TeamEvent;
import co.iyubinest.mononoke.data.User;
import co.iyubinest.mononoke.data.roles.RolesRepository;
import co.iyubinest.mononoke.data.updates.UpdatesRepository;
import io.reactivex.Flowable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ComposedTeamInteractor implements TeamInteractor {
  private final TeamRepository team;
  private final RolesRepository roles;
  private final UpdatesRepository status;

  public ComposedTeamInteractor(TeamRepository team, RolesRepository roles,
      UpdatesRepository status) {
    this.team = team;
    this.roles = roles;
    this.status = status;
  }

  public static User userOf(final TeamService.TeamResponse teamResponse,
      final String role) {
    return BasicUser
        .create(teamResponse.name, teamResponse.avatar, teamResponse.github,
            role, teamResponse.location, "", teamResponse.languages,
            teamResponse.tags);
  }

  @Override
  public Flowable<TeamEvent> users() {
    final Flowable<TeamEvent> allEvents =
        Flowable.zip(team.get(), roles.get(), this::zip);
    final Flowable<TeamEvent> statusEvents = status.get();
    return Flowable.merge(allEvents, statusEvents)
        .doOnNext(teamEvent -> Log.v("New Update", teamEvent.toString()))
        .doOnError(throwable -> new TeamEvent.None());
  }

  private TeamEvent zip(final List<TeamService.TeamResponse> team,
      final Map<String, String> roles) {
    final List<User> users = new ArrayList<>(team.size());
    for (TeamService.TeamResponse response : team) {
      users.add(userOf(response, roles.get(String.valueOf(response.role))));
    }
    return TeamEvent.All.with(users);
  }
}
