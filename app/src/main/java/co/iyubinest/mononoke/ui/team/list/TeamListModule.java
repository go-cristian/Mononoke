package co.iyubinest.mononoke.ui.team.list;

import dagger.Module;
import dagger.Provides;

@Module
public class TeamListModule {
  private final TeamListActivity activity;

  public TeamListModule(TeamListActivity activity) {
    this.activity = activity;
  }

  @Provides
  public TeamListScreen teamListScreen() {
    return activity;
  }
}
