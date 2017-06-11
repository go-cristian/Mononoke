package co.iyubinest.mononoke.ui.team.mate_detail;

import dagger.Subcomponent;

@Subcomponent(modules = TeamMateDetailModule.class)
public interface TeamMateDetailComponent {
  void inject(TeamMateDetailActivity activity);
}
