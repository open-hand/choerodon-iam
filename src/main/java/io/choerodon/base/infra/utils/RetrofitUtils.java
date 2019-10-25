package io.choerodon.base.infra.utils;

import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

public class RetrofitUtils {

    public Boolean executeWithBoolean(Call<Object> call) {
        Response<Object> execute = null;
        try {
            execute = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (execute == null ||
                !execute.isSuccessful() ||
                (execute.isSuccessful() &&
                        (execute.body() == null ||
                                !(execute.body() instanceof Boolean) ||
                                !((Boolean) execute.body()).booleanValue()
                        )
                )
        ) {
            return false;
        }
        return true;
    }
}
