package traha.taxi.Utils;


import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import traha.taxi.services.RetrofitService;

/**
 * Created by Vijendra Patidar on 14/10/17.
 */

/*All main urls and constant values*/

public class Constants {

//    Staging Server
    private static String baseserverUrl = "http://businesspkr.com/Websites/taxi/api/";




    private static final OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
            .connectTimeout(6000, TimeUnit.SECONDS)
            .readTimeout(6000, TimeUnit.SECONDS)
            .writeTimeout(6000, TimeUnit.SECONDS).connectionPool(new ConnectionPool(50, 50000, TimeUnit.SECONDS))
            .build();

    public static final RetrofitService service = new Retrofit.Builder()
            .baseUrl(baseserverUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(RetrofitService.class);


}
