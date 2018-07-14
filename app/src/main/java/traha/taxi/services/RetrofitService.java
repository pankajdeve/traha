package traha.taxi.services;



import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import traha.taxi.models.LoginModel;

/**
 * Created by Vijendra Patidar on 10/4/17.
 */

/*Retrofit Methods*/

public interface RetrofitService {


    @POST("user_register")
    Call<ResponseBody> userReg(@Header("X-API-KEY") String header,@Body  HashMap<String,Object> hashMap);

    @POST("user_login")
    Call<LoginModel> userLogin(@Header("X-API-KEY") String header,@Body  HashMap<String,Object> hashMap);
}
