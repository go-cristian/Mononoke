package co.iyubinest.mononoke.data;
import android.os.Parcelable;
import java.util.List;

public interface User extends Parcelable {
  String name();
  String avatar();
  String github();
  String role();
  String location();
  String status();
  List<String> languages();
  List<String> tags();
}

