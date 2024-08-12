package com.example.rotina_gem


import API.ApiClient
import API.GemineApi
import API.Task // Certifique-se de que esta linha está importando a classe Task correta
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var btnAddTask: Button
    private lateinit var recyclerView: RecyclerView
    private var taskAdapter: TaskAdapter? = null
    private lateinit var gemineApi: GemineApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnAddTask = findViewById(R.id.btnAddTask)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        gemineApi = ApiClient.getClient().create(GemineApi::class.java)

        btnAddTask.setOnClickListener {
            val intent = Intent(this@MainActivity, AddTaskActivity::class.java)
            startActivity(intent)
        }

        loadTasks()
    }

    private fun loadTasks() {
        val call: Call<List<Task>> = gemineApi.getTasks()
        call.enqueue(object : Callback<List<Task>> {
            override fun onResponse(call: Call<List<Task>>, response: Response<List<Task>>) {
                if (response.isSuccessful && response.body() != null) {
                    val tasks: List<Task> = response.body()!!
                    taskAdapter = TaskAdapter(tasks)
                    recyclerView.adapter = taskAdapter
                }
            }

            override fun onFailure(call: Call<List<Task>>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }
}
