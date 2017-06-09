package co.iyubinest.mononoke.ui.team.list;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import co.iyubinest.mononoke.R;
import co.iyubinest.mononoke.common.LoadImage;
import co.iyubinest.mononoke.data.team.Mate;
import co.iyubinest.mononoke.data.team.list.RequestTeam;
import java.util.ArrayList;
import java.util.List;

import static co.iyubinest.mononoke.common.LoadImage.OPTION.FIT;

class TeamListWidget extends RecyclerView {

  private MateAdapter adapter = new MateAdapter();

  public TeamListWidget(Context context) {
    this(context, null);
  }

  public TeamListWidget(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    setAdapter(adapter);
    int spanCount =
        getContext().getResources().getInteger(R.integer.team_list_columns);
    setLayoutManager(new GridLayoutManager(getContext(), spanCount));
    setBackgroundColor(Color.TRANSPARENT);
  }

  public void show(List<Mate> people) {
    adapter.show(people);
  }

  public void add(Mate user) {
    adapter.add(user);
  }

  public void updateStatus(RequestTeam.NewStatusEvent event) {
    adapter.updateStatus(event);
  }

  public void onMateSelected(OnMateSelected listener) {
    adapter.onMateSelected(listener);
  }

  interface OnMateSelected {

    void onMateSelected(Mate mate);
  }

  private static class MateAdapter extends RecyclerView.Adapter<MateHolder> {

    private final List<Mate> mates = new ArrayList<>();

    private OnMateSelected listener;

    @Override
    public MateHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      return new MateHolder(LayoutInflater.from(parent.getContext())
          .inflate(R.layout.team_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(MateHolder holder, int position) {
      holder.mate(mates.get(position));
      holder.onPositionSelected(pos -> {
        if (listener != null) listener.onMateSelected(mates.get(pos));
      });
    }

    @Override
    public int getItemCount() {
      return mates.size();
    }

    void show(List<Mate> people) {
      this.mates.addAll(people);
      notifyDataSetChanged();
    }

    void onMateSelected(OnMateSelected listener) {
      this.listener = listener;
    }

    void add(Mate user) {
      mates.add(user);
      notifyItemInserted(mates.size());
    }

    void updateStatus(RequestTeam.NewStatusEvent event) {
      int index = findByName(event.user());
      Mate oldMate = mates.get(index);
      Mate newMate =
          new Mate(oldMate.name(), oldMate.avatar(), oldMate.github(),
              oldMate.gender(), oldMate.location(), oldMate.role(),
              event.state(), oldMate.languages(), oldMate.tags());
      mates.set(index, newMate);
      notifyItemChanged(index);
    }

    private int findByName(String name) {
      for (int i = 0; i < mates.size(); i++) {
        Mate mate = mates.get(i);
        if (mate.github().equals(name)) return i;
      }
      return -1;
    }
  }

  static class MateHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.mate_list_item_name) TextView nameView;
    @BindView(R.id.mate_list_item_avatar) ImageView avatarView;
    @BindView(R.id.mate_list_item_github) TextView githubView;
    @BindView(R.id.mate_list_item_location) ImageView locationView;
    @BindView(R.id.mate_list_item_role) TextView roleView;
    @BindView(R.id.mate_list_item_languages) TextView languagesView;
    @BindView(R.id.mate_list_item_tags) TextView tagsView;
    @BindView(R.id.mate_list_item_status) TextView statusView;
    private OnPositionSelected listener;

    MateHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    void onPositionSelected(OnPositionSelected onPositionSelected) {
      listener = onPositionSelected;
    }

    void mate(Mate mate) {
      itemView.setOnClickListener(v -> {
        if (listener != null) listener.onPositionSelected(getAdapterPosition());
      });
      LoadImage.from(mate.avatar(), avatarView, FIT);
      nameView.setText(mate.name());
      roleView.setText(mate.role());
      githubView.setText(mate.github());
      languagesView.setText(mate.languages().toString());
      tagsView.setText(mate.tags().toString());
      if (!TextUtils.isEmpty(mate.status())) statusView.setText(mate.status());
      LoadImage.fromResource(locationView, "flag_" + mate.location(),
          R.drawable.flag__unknown);
    }

    interface OnPositionSelected {

      void onPositionSelected(int position);
    }
  }
}
