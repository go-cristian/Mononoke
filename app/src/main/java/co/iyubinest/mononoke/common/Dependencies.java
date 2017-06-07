package co.iyubinest.mononoke.common;

import co.iyubinest.mononoke.BuildConfig;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class Dependencies {
  private Retrofit retrofit;
  private OkHttpClient client;

  public Retrofit retrofit() {
    if (retrofit == null) {
      retrofit = new Retrofit.Builder()
          .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
          .addConverterFactory(MoshiConverterFactory.create()).client(client())
          .baseUrl(BuildConfig.BASE_URL).build();
    }
    return retrofit;
  }

  public OkHttpClient client() {
    if (client == null) {
      HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
      interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
      client =
          new OkHttpClient.Builder().addInterceptor(new StethoInterceptor())
              .addInterceptor(interceptor).build();
    }
    return client;
  }
}
