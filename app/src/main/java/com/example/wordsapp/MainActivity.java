package com.example.wordsapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
  final OkHttpClient client = new OkHttpClient();
  EditText editText;
  Button addWordButton;
  String SERVER_PATH = "http://192.168.1.103:8089";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    editText = (EditText) findViewById(R.id.wordEditText);
    addWordButton = (Button) findViewById(R.id.addWordBtn);
    addWordButton.setOnClickListener(v -> {
      try {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", "");
        jsonObject.put("name", editText.getText().toString());

        RequestBody body = RequestBody
          .create(
            MediaType.parse("application/json"),
            jsonObject.toString()
          );

        Request request = new Request.Builder()
          .url(SERVER_PATH)
          .post(body)
          .build();

        client.newCall(request).enqueue(new Callback() {
          @Override
          public void onFailure(Call call, IOException e) {
            Toast.makeText(MainActivity.this, "Failed request", Toast.LENGTH_SHORT).show();
          }

          @Override
          public void onResponse(Call call, Response response) {
            if (response.isSuccessful()) {
              runOnUiThread(() -> Toast.makeText(MainActivity.this, "Successful request", Toast.LENGTH_SHORT).show());
            }
          }
        });
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }


}