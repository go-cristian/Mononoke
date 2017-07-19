package co.iyubinest.mononoke.data.roles;
import io.reactivex.Flowable;
import java.util.Map;

public interface RolesRepository {

  Flowable<Map<String, String>> get();
}
