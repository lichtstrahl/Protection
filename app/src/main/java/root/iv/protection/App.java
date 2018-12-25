package root.iv.protection;

import android.app.Application;
import android.util.Log;

import root.iv.protection.retrofit.PhoneAPI;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class App extends Application {
    private static final String TAG = "Protection";
    private final String BASE_URL = "https://ivprotection.localtunnel.me";
    private static PhoneAPI phoneAPI;

    @Override
    public void onCreate() {
        super.onCreate();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        phoneAPI = retrofit.create(PhoneAPI.class); // Объект, для выполнения запросов
    }
    public static PhoneAPI getPhoneAPI() {
        return phoneAPI;
    }
    public static void logI(String msg) {
        Log.i(TAG, msg);
    }
    public static void logE(String msg) {
        Log.e(TAG, msg);
    }
}
