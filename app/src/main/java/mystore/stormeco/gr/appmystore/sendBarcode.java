package mystore.stormeco.gr.appmystore;

import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

@SuppressWarnings({"unchecked", "ConstantConditions", "NullableProblems", "Convert2Lambda"})
public class sendBarcode {

    private OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();

    public sendBarcode(String barcode,String shop_id, String type_id,final String event_id){

        //got from
        //https://stackoverflow.com/questions/32196424/how-to-add-headers-to-okhttp-request-interceptor

        //Replace Request Headers with an Interceptor to the Request Client
        httpClientBuilder.addInterceptor(new Interceptor() {

              @Override
              public Response intercept(Chain chain) throws IOException {
                  Request original = chain.request();

                  Request request = original.newBuilder()
                          .header("UserData-Agent", "Android")
                          .method(original.method(), original.body())
                          .build();

                  return chain.proceed(request);
              }
          });

        //Rebuild the Client with new Interceptors to accept the new headers
        OkHttpClient client = httpClientBuilder.build();

        //Build the Url with the Query parameters to pass to the Request

        HttpUrl.Builder urlBuilder = HttpUrl.parse(MysStoreApp.getInstance().getResources().getString(R.string.savebarcodeURLAPI)).newBuilder();

//        //this also encodes internally the parameters to pass to the request ;)
//
        
        urlBuilder.addQueryParameter("barcode_id",barcode);
        urlBuilder.addQueryParameter("shop_id",shop_id);
        urlBuilder.addQueryParameter("type_id",type_id);

//        urlBuilder.addQueryParameter("password",Password);

        Log.v("Requesting",urlBuilder.build().toString());
            Request okHttpRequest = new Request.Builder()
                    .url(urlBuilder.build())
                    .build();

            client.newCall(okHttpRequest).enqueue(new Callback() {
                @Override public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override public void onResponse(Call call, Response response) throws IOException {
                    try (ResponseBody responseBody = response.body()) {
                        //for debugging the response headers
//                        Headers responseHeaders = response.headers();
//                        for (int i = 0, size = responseHeaders.size(); i < size; i++) {
//                           // System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
//                        }


                        //make a new hashmap obj for return
                        HashMap resHashMap = new HashMap();
                        //get a Gson Parser Object instance
                        JsonParser gsonParser = new JsonParser();

                        String responseStr = (responseBody.string());

                        //isSuccessful means http status response code is 200 - 300
                        //so here http code is 404 -BUT even though there is a reponse http code 404
                        // from the api this does not mean that we should only check for this.
                        //so we will also check the status code from the api which is a "key" that corresponds to this api call
                        //so its "mobile/user"

                        //parse the response as Json Object
                        //try to check if the response is a json object
                        JsonObject responseJson=null;

                        boolean json_is_ok = false;
                        try {
                            responseJson = gsonParser.parse(responseStr).getAsJsonObject();
                            json_is_ok=true;
                        } catch (IllegalStateException e) {
                            System.out.print(e.getMessage());
                        }

                        resHashMap.put("http_status_code",response.code()); //is an integer

                        if (json_is_ok && response.isSuccessful()){
                            resHashMap = parseAndMapJsonResponse(resHashMap,responseJson);
                            resHashMap.put("status",true);
                        }
                        //check the validity of json OR status Code
                        else if (!json_is_ok  && !response.isSuccessful()) {
                            //parse the message send the status
                            resHashMap.put("status",false);
                            resHashMap.put("message",MysStoreApp.getInstance().getResources().getString(R.string.network_error));
                        }
                        else if (json_is_ok && !response.isSuccessful()){
                            //parse the message send the status
                            resHashMap.put("status",false);
                            //get message from API if json response is ok
                            resHashMap.put("message",responseJson.get("msg").getAsString());
                        }


                        //send back to caller and target with event_id to trigger the correct caller ;)
                        EventBusMessage mesg = new EventBusMessage();
                        mesg.setId(event_id);

                        mesg.setData(resHashMap);
                        //Post msg
                        EventBus.getDefault().post(mesg);


                    }
                }
            });


    }

    private HashMap parseAndMapJsonResponse(HashMap resHashMap, JsonObject responseJson) {

        /**
         * {
         *   "status": 1,
         *   "msg": "Το barcode 6547567 καταχωρήθηκε επιτυχώς!"
         * }
         */
        //get message from API if json response is ok
        resHashMap.put("message",responseJson.get("msg").getAsString());
        //-----------END OF PARSING------------------

        return resHashMap;
    }
}
