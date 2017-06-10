package co.iyubinest.mononoke.data;

import java.util.List;

public abstract class TeamEvent {

  public static final class All extends TeamEvent {
    private final List<User> users;

    private All(List<User> users) {
      this.users = users;
    }

    public static All with(List<User> users) {
      return new All(users);
    }

    public List<User> users() {
      return users;
    }

    @Override
    public String toString() {
      return "Event with " + users.size() + " users.";
    }
  }

  public static final class Status extends TeamEvent {
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

    @Override
    public String toString() {
      return "Event with status " + user.status() + " for " + user.github();
    }
  }

  public static final class New extends TeamEvent {
    private final User user;

    private New(User user) {
      this.user = user;
    }

    public static New with(User user) {
      return new New(user);
    }

    @Override
    public String toString() {
      return "Event New";
    }

    public User user() {
      return user;
    }
  }

  public static final class None extends TeamEvent {
    @Override
    public String toString() {
      return "Event None";
    }
  }
}
