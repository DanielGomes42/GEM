package API;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String BASE_URL = "https://api.gemine.com/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

            // Interceptor para adicionar a chave da API
            Interceptor apiKeyInterceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request originalRequest = chain.request();
                    Request requestWithApiKey = originalRequest.newBuilder()
                            .header("Authorization", "Bearer AIzaSyCohDIvzt8siF5rzEY6zme-uiDTsdLictM")
                            .build();
                    return chain.proceed(requestWithApiKey);
                }
            };

            clientBuilder.addInterceptor(apiKeyInterceptor);

            OkHttpClient client = clientBuilder.build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}