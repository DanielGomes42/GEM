package API;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface GemineApi {

    // Método para obter a lista de tarefas
    @GET("tasks")
    Call<List<Task>> getTasks();

    // Método para adicionar uma nova tarefa
    @POST("tasks")
    Call<Task> addTask(@Body Task task);

    // Método para fazer upload de uma foto para uma tarefa específica
    @Multipart
    @POST("tasks/{id}/photo")
    Call<ResponseBody> uploadPhoto(@Path("id") int taskId, @Part MultipartBody.Part photo);

    @POST("generateDescription")
    Call<String> generateDescription(@Body Task task);

}
