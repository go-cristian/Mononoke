package co.iyubinest.mononoke.data.roles;
import co.iyubinest.mononoke.cache.Cache;
import co.iyubinest.mononoke.cache.MemoryCache;
import co.iyubinest.mononoke.common.RxUtils;
import io.reactivex.Flowable;
import java.util.Map;
import retrofit2.Retrofit;

public class HttpRolesRepository implements RolesRepository {
  private final RolesService service;
  private final Cache<Map<String, String>> cache;

  public HttpRolesRepository(final Retrofit retrofit) {
    service = retrofit.create(RolesService.class);
    cache = new MemoryCache<>();
  }

  @Override
  public Flowable<Map<String, String>> get() {
    return Flowable.concat(
      cache.get(),
      service.roles().retryWhen(RxUtils.incremental())
    ).take(1).doOnNext(cache::save);
  }
}
