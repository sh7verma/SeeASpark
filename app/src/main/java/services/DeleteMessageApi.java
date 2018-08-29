package services;

import android.content.Context;

import models.BaseSuccessModel;
import network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.Utils;

/**
 * Created by dev on 29/8/18.
 */

public class DeleteMessageApi {
    Context mContext;
    Utils util;
    String messageId = "";

    public DeleteMessageApi(Context con, String messageid) {
        // TODO Auto-generated constructor stub
        this.mContext = con;
        util = new Utils(con);
        messageId = messageid;
        hitApi(messageId);
    }

    void hitApi(String id) {
        Call<BaseSuccessModel> call = RetrofitClient.getInstance().deleteMessage(util.getString("access_token", ""), id);
        call.enqueue(new Callback<BaseSuccessModel>() {

            @Override
            public void onResponse(Call<BaseSuccessModel> call, Response<BaseSuccessModel> response) {

            }

            @Override
            public void onFailure(Call<BaseSuccessModel> call, Throwable t) {

            }
        });
    }
}
