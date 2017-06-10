package co.iyubinest.mononoke.ui.team.mate_detail;

import co.iyubinest.mononoke.data.User;

interface TeamMateDetailScreen {
  String status();

  void updateList(final String status, final User user);

  User user();

  void error(Throwable throwable);
}
