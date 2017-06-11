package co.iyubinest.mononoke.ui.team.list;

import dagger.Subcomponent;

@Subcomponent(modules = TeamListModule.class)
public interface TeamListComponent {
  void inject(TeamListActivity activity);
}
