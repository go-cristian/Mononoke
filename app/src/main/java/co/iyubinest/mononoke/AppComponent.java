package co.iyubinest.mononoke;

import co.iyubinest.mononoke.ui.team.list.TeamListComponent;
import co.iyubinest.mononoke.ui.team.list.TeamListModule;
import co.iyubinest.mononoke.ui.team.mate_detail.TeamMateDetailComponent;
import co.iyubinest.mononoke.ui.team.mate_detail.TeamMateDetailModule;
import dagger.Component;
import javax.inject.Singleton;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {

  TeamListComponent teamListComponent(TeamListModule module);

  TeamMateDetailComponent teamMateDetailComponent(TeamMateDetailModule module);
}
