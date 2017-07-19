package co.iyubinest.mononoke.ui.team.list;
import android.content.Context;
import android.content.res.TypedArray;
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
import co.iyubinest.mononoke.data.User;
import java.util.ArrayList;
import java.util.List;

import static co.iyubinest.mononoke.common.LoadImage.OPTION.FIT;

class TeamListWidget extends RecyclerView {

  private final TeamListAdapter adapter = new TeamListAdapter();

  public TeamListWidget(Context context) {
    this(context, null);
  }

  public TeamListWidget(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TeamListWidget, 0, 0);
    int spanCount = typedArray.getInt(R.styleable.TeamListWidget_columns, 1);
    setLayoutManager(new GridLayoutManager(getContext(), spanCount));
    typedArray.recycle();
    setBackgroundColor(Color.TRANSPARENT);
    setAdapter(adapter);
  }

  public void show(List<User> users) {
    adapter.show(users);
  }

  public void onUserSelected(final OnUserSelected listener) {
    adapter.onUserSelected(listener);
  }

  public void add(final User user) {
    adapter.add(user);
  }

  public void update(final User user) {
    adapter.update(user);
  }

  public ArrayList<User> users() {
    return new ArrayList<>(adapter.users);
  }

  interface OnUserSelected {

    void onMateSelected(final User mate);
  }

  private static class TeamListAdapter extends RecyclerView.Adapter<TeamListHolder> {

    private final List<User> users = new ArrayList<>();
    private OnUserSelected listener;

    @Override public TeamListHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
      return new TeamListHolder(
          LayoutInflater.from(parent.getContext()).inflate(R.layout.team_list_item, parent, false));
    }

    @Override public void onBindViewHolder(final TeamListHolder holder, int position) {
      holder.mate(users.get(position));
      holder.onPositionSelected(pos -> {
        if (listener != null) listener.onMateSelected(users.get(pos));
      });
    }

    @Override public int getItemCount() {
      return users.size();
    }

    void show(final List<User> users) {
      this.users.addAll(users);
      notifyDataSetChanged();
    }

    void onUserSelected(final OnUserSelected listener) {
      this.listener = listener;
    }

    void add(final User user) {
      users.add(user);
      notifyItemInserted(users.size());
    }

    public void update(final User user) {
      int index = findByName(user.github());
      if (index != -1) {
        users.set(index, user);
        notifyItemChanged(index);
      }
    }

    private int findByName(final String name) {
      for (int i = 0; i < users.size(); i++) {
        final User user = users.get(i);
        if (user.github().equals(name)) return i;
      }
      return -1;
    }
  }

  static class TeamListHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.team_list_item_name) TextView nameView;
    @BindView(R.id.team_list_item_avatar) ImageView avatarView;
    @BindView(R.id.team_list_item_github) TextView githubView;
    @BindView(R.id.team_list_item_location) ImageView locationView;
    @BindView(R.id.team_list_item_role) TextView roleView;
    @BindView(R.id.team_list_item_languages) TextView languagesView;
    @BindView(R.id.team_list_item_tags) TextView tagsView;
    @BindView(R.id.team_list_item_status) TextView statusView;
    private OnPositionSelected listener;

    TeamListHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    void onPositionSelected(OnPositionSelected onPositionSelected) {
      listener = onPositionSelected;
    }

    void mate(User user) {
      itemView.setOnClickListener(v -> {
        if (listener != null) listener.onPositionSelected(getAdapterPosition());
      });
      LoadImage.from(user.avatar(), avatarView, FIT);
      nameView.setText(user.name());
      roleView.setText(user.role());
      githubView.setText(user.github());
      languagesView.setText(user.languages().toString());
      tagsView.setText(user.tags().toString());
      if (!TextUtils.isEmpty(user.status())) statusView.setText(user.status());
      LoadImage.fromResource(locationView, "flag_" + user.location(), R.drawable.flag__unknown);
    }

    interface OnPositionSelected {

      void onPositionSelected(int position);
    }
  }
}
