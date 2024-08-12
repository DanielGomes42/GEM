package com.example.rotina_gem;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddTaskActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private EditText taskTitle, taskDescription;
    private Button btnSaveTask;
    private ImageView imageView;
    private GeminiApiService geminiApiService;
    private Bitmap taskImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        taskTitle = findViewById(R.id.taskTitle);
        taskTitle.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                Toast.makeText(this, "Título finalizado", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });

        taskDescription = findViewById(R.id.taskDescription);
        taskDescription.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_GO) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                Toast.makeText(this, "Descrição finalizada", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });

        btnSaveTask = findViewById(R.id.btnSaveTask02);
        imageView = findViewById(R.id.imageView);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://generativelanguage.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        geminiApiService = retrofit.create(GeminiApiService.class);

        btnSaveTask.setOnClickListener(v -> takePhoto());
    }

    @Override
    protected void onResume() {
        super.onResume();
        taskTitle.clearFocus();
        taskDescription.clearFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            taskImage = (Bitmap) extras.get("data");
            imageView.setImageBitmap(taskImage);
            generateDescriptionAndPost();
        }
    }

    private void generateDescriptionAndPost() {
        String title = taskTitle.getText().toString();
        String description = taskDescription.getText().toString();

        // Criação do corpo da requisição
        GenerateContentRequest request = new GenerateContentRequest(
                Collections.singletonList(
                        new GenerateContentRequest.Content(
                                Collections.singletonList(
                                        new GenerateContentRequest.Content.Part(title + ": " + description)
                                )
                        )
                )
        );

        // Imprimir o JSON que está sendo enviado para depuração
        Log.d("API_REQUEST", new Gson().toJson(request));

        // Fazer a chamada à API
        Call<GenerateContentResponse> call = geminiApiService.generateContent("YOUR_API_KEY", request);
        call.enqueue(new Callback<GenerateContentResponse>() {
            @Override
            public void onResponse(Call<GenerateContentResponse> call, Response<GenerateContentResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String generatedDescription = response.body().getGeneratedText();
                    shareOnSocialMedia(generatedDescription);
                } else {
                    Log.e("API_ERROR", "Response Code: " + response.code());
                    Log.e("API_ERROR", "Response Message: " + response.message());
                    Toast.makeText(AddTaskActivity.this, "Falha ao gerar descrição: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GenerateContentResponse> call, Throwable t) {
                t.printStackTrace();
                Log.e("API_ERROR", "onFailure: " + t.getMessage());
                Toast.makeText(AddTaskActivity.this, "Erro ao conectar com a API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void shareOnSocialMedia(String description) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_TEXT, description);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), taskImage, "Tarefa", null);
        shareIntent.putExtra(Intent.EXTRA_STREAM, path);
        startActivity(Intent.createChooser(shareIntent, "Compartilhar Tarefa"));
    }
}
