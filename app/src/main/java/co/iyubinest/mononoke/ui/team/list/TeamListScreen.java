package co.iyubinest.mononoke.ui.team.list;

import co.iyubinest.mononoke.data.User;
import java.util.List;

interface TeamListScreen {

  void show(final List<User> users);

  void update(final User user);

  void add(final User user);
}
