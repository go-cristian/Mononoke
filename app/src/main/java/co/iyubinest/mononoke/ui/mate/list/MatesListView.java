package co.iyubinest.mononoke.ui.mate.list;

import co.iyubinest.mononoke.data.mates.Mate;
import co.iyubinest.mononoke.data.mates.list.RequestMates;
import java.util.List;

interface MatesListView {
  void showAll(List<Mate> mates);

  void showError(Throwable throwable);

  void update(RequestMates.NewStatusEvent event);

  void update(RequestMates.NewUserEvent event);
}
