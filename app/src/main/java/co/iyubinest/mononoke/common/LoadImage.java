package co.iyubinest.mononoke.common;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

public class LoadImage {
  public static void fromResource(ImageView view, String resourceName, int defaultResource) {
    int id = view.getContext().getResources().getIdentifier(
      resourceName,
      "drawable",
      view.getContext().getPackageName()
    );
    if (id < 0) id = defaultResource;
    view.setImageResource(id);
  }

  public static void from(String url, ImageView view, OPTION option) {
    RequestCreator request = Picasso.with(view.getContext()).load(url);
    switch (option) {
      case FIT:
        request = request.fit();
        break;
      case NONE:
        break;
      default:
        throw new IllegalStateException("state not implemented");
    }
    request.into(view);
  }

  public enum OPTION {
    FIT, NONE
  }
}
