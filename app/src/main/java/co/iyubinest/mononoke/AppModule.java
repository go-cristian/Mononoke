package co.iyubinest.mononoke;

import co.iyubinest.mononoke.socket.RxSocket;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.squareup.moshi.Moshi;
import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

@Module
class AppModule {

  private final App app;

  public AppModule(App app) {
    this.app = app;
  }

  @Provides
  public OkHttpClient okHttpClient() {
    return new OkHttpClient.Builder().build();
  }

  @Provides
  public Retrofit retrofit(OkHttpClient client) {
    return new Retrofit.Builder()
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create()).client(client)
        .baseUrl(BuildConfig.BASE_URL).build();
  }

  @Provides
  public Moshi moshi() {
    return new Moshi.Builder().build();
  }

  @Provides
  public RxSocket rxSocket(OkHttpClient client) {
    return new RxSocket(client, BuildConfig.BASE_WS_URL);
  }
}
