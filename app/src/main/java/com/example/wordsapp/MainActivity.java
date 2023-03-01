package com.example.wordsapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

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
  Button scanButton;
  String SERVER_PATH = "http://192.168.1.103:8089";

  ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(
    new ScanContract(),
    result -> {
      if (result.getContents() != null) {
        sendMessage(result.getContents());

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Result");
        builder.setMessage(result.getContents());
        builder.setPositiveButton("OK",
          (dialogInterface, i) -> dialogInterface.dismiss()).show();
      }
    });

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    editText = (EditText) findViewById(R.id.wordEditText);
    addWordButton = (Button) findViewById(R.id.addWordBtn);
    scanButton = (Button) findViewById(R.id.scanBtn);

    addWordButton.setOnClickListener(v -> sendMessage(editText.getText().toString()));

    scanButton.setOnClickListener(view -> {
      ScanOptions options = new ScanOptions();

      options.setBeepEnabled(true);
      options.setOrientationLocked(true);
      options.setCaptureActivity(appCaptureActivity.class);

      barLauncher.launch(options);
    });

  }

  private void sendMessage(String message) {
    try {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("id", "");
      jsonObject.put("name", message);

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
  }
}