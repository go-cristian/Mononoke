package co.iyubinest.mononoke.ui.mate.detail;

import co.iyubinest.mononoke.data.mates.Mate;

interface MateDetailView {
  String status();

  void updateList(String status, Mate mate);

  Mate mate();
}
