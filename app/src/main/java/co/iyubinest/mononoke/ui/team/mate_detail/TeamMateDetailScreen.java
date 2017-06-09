package co.iyubinest.mononoke.ui.team.mate_detail;

import co.iyubinest.mononoke.data.team.Mate;

interface TeamMateDetailScreen {
  String status();

  void updateList(String status, Mate mate);

  Mate mate();
}
