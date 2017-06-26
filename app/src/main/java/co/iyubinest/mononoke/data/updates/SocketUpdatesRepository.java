package co.iyubinest.mononoke.data.updates;

import co.iyubinest.mononoke.data.BasicUser;
import co.iyubinest.mononoke.data.TeamEvent;
import co.iyubinest.mononoke.data.User;
import co.iyubinest.mononoke.data.roles.RolesRepository;
import co.iyubinest.mononoke.socket.RxSocket;
import com.squareup.moshi.Moshi;
import io.reactivex.Flowable;
import java.util.List;
import java.util.Map;

public class SocketUpdatesRepository implements UpdatesRepository {

  private final Moshi moshi;
  private final RxSocket socket;
  private final Flowable<List<User>> users;
  private final RolesRepository roles;

  public SocketUpdatesRepository(Moshi moshi, RxSocket socket,
      Flowable<List<User>> users, RolesRepository roles) {
    this.moshi = moshi;
    this.socket = socket;
    this.users = users;
    this.roles = roles;
  }

  @Override
  public Flowable<TeamEvent> get() {
    return socket.get().flatMap(msg -> users.flatMap(
        users -> roles.get().flatMap(roles -> toEvent(msg, users, roles))));
  }

  private Flowable<TeamEvent> toEvent(final String responseText,
      final List<User> users, final Map<String, String> roles) {
    try {
      final Object response = from(responseText);
      if (response instanceof StatusResponse) {
        final StatusResponse statusResponse = (StatusResponse) response;
        final User user = findByName(statusResponse.user, users);
        final User newUser = BasicUser
            .create(user.name(), user.avatar(), user.github(), user.role(),
                user.location(), statusResponse.state, user.languages(),
                user.tags());
        return Flowable.just(TeamEvent.Status.with(newUser));
      } else if (response instanceof NewUserResponse) {
        final UserResponse newUserResponse = ((NewUserResponse) response).user;
        final User newUser = BasicUser
            .create(newUserResponse.name, newUserResponse.avatar,
                newUserResponse.github,
                roles.get(String.valueOf(newUserResponse.role)),
                newUserResponse.location, "", newUserResponse.languages,
                newUserResponse.tags);
        return Flowable.just(TeamEvent.New.with(newUser));
      }
      return Flowable.never();
    } catch (Exception e) {
      return Flowable.never();
    }
  }

  private Object from(final String responseText) {
    try {
      final StatusResponse statusResponse = statusFrom(responseText);
      if (isValidStatus(statusResponse)) {
        return statusResponse;
      }
      final NewUserResponse newUserResponse = userFrom(responseText);
      if (isValidUser(newUserResponse)) {
        return newUserResponse;
      }
      throw new IllegalArgumentException("response is not valid");
    } catch (Exception e) {
      throw new IllegalArgumentException("response is not valid");
    }
  }

  private StatusResponse statusFrom(final String responseText) {
    try {
      return moshi.adapter(StatusResponse.class).fromJson(responseText);
    } catch (Exception e) {
      return null;
    }
  }

  private NewUserResponse userFrom(final String responseText) {
    try {
      return moshi.adapter(NewUserResponse.class).fromJson(responseText);
    } catch (Exception e) {
      return null;
    }
  }

  private boolean isValidStatus(final StatusResponse statusResponse) {
    return statusResponse != null && statusResponse.user != null &&
        statusResponse.state != null &&
        statusResponse.event.equals("state_change");
  }

  private boolean isValidUser(final NewUserResponse newUserResponse) {
    return newUserResponse != null && newUserResponse.user != null &&
        newUserResponse.event.equals("user_new");
  }

  private User findByName(final String userName, final List<User> users) {
    for (User user : users) {
      if (user.github().equals(userName)) return user;
    }
    throw new IllegalArgumentException("username not found in users");
  }

  private static class StatusResponse {
    String event;
    String user;
    String state;
  }

  private static class NewUserResponse {
    String event;
    UserResponse user;
  }

  private static class UserResponse {
    String name;
    String avatar;
    String github;
    String location;
    List<String> languages, tags;
    Integer role;
  }
}