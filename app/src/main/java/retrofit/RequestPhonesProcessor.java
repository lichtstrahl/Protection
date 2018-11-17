package retrofit;

import android.util.Log;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import root.iv.protection.R;

public class RequestPhonesProcessor implements Callback<List<Phone>> {
    private final String TAG = "RequestPhonesProcessor";
    private bodyAnalyzer bodyAnalyzer;


    public RequestPhonesProcessor(bodyAnalyzer sub) {
        bodyAnalyzer = sub;
    }

    @Override
    public void onResponse(Call<List<Phone>> call, Response<List<Phone>> response) {
        if (response.body() != null)
            bodyAnalyzer.done(response.body());
        else
            bodyAnalyzer.fail(R.string.invalidBody);
    }

    @Override
    public void onFailure(Call<List<Phone>> call, Throwable t) {
        Log.e(TAG, t.getMessage());
        bodyAnalyzer.fail(R.string.retrofitFailure);
    }
}
