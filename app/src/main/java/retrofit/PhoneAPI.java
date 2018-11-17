package retrofit;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface PhoneAPI {
    @GET("/api/Phones")
    Call<List<Phone>> getAllPhones();
}
