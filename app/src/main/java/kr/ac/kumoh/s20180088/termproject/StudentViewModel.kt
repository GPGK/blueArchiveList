package kr.ac.kumoh.s20180088.termproject

import android.app.Application
import android.content.Intent
import android.graphics.Bitmap
import android.widget.Toast
import androidx.collection.LruCache
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder

class StudentViewModel(application: Application) : AndroidViewModel(application) {
    data class Student(
        var stu_id: Int,
        var stu_name: String,
        var stu_club: String,
        var stu_school: String,
        var stu_image: String
    )


    companion object {
        const val QUEUE_TAG = "StudentVolleyRequest"
        const val SERVER_URL = "https://expresssongdb-rwhxi.run.goorm.io"
    }
    private val school_id = MutableLiveData<Int>()


    //private val school_id = school_id;
    private val students = ArrayList<Student>()
    private val _list = MutableLiveData<ArrayList<Student>>()
    val list: LiveData<ArrayList<Student>>
        get() = _list

    private var queue: RequestQueue
    val imageLoader: ImageLoader

    init {
        _list.value = students
        queue = Volley.newRequestQueue(getApplication())

        imageLoader = ImageLoader(queue,
            object : ImageLoader.ImageCache {
                private val cache =
                    androidx.collection.LruCache<String, Bitmap>(100)   // 한번에 이미지 로드할 개수
                override fun getBitmap(url: String): Bitmap? {
                    return cache.get(url)
                }
                override fun putBitmap(url: String, bitmap: Bitmap) {
                    cache.put(url, bitmap)
                }
            })
    }

    fun getImageUrl(i: Int): String = "$SERVER_URL/student/" + URLEncoder.encode(students[i].stu_image, "utf-8")
    //fun getImageUrl(i: Int): String = "https://picsum.photos/200/300?random=${songs[i].id}"
    // 이미지를 랜덤하게 넣어준다.

    fun requestStudent() {
        // NOTE: 서버 주소는 본인의 서버 IP 사용할 것
        // Array를 반환할 경우에는 JsonObjectRequest 대신 JsonArrayRequest 사용
        val request = JsonArrayRequest(
            Request.Method.GET,
            SERVER_URL + "/sch?id=" + school_id.value,
            null,
            {
                //Toast.makeText(getApplication(), it.toString(), Toast.LENGTH_LONG).show()
                students.clear()
                parseJson(it)
                _list.value = students
            },
            {
                Toast.makeText(getApplication(), it.toString(), Toast.LENGTH_LONG).show()
            }
        )

        request.tag = QUEUE_TAG
        queue.add(request)
    }

    fun updateValue(index: Int) {
        school_id.value = index
    }

    private fun parseJson(items: JSONArray) {
        for (i in 0 until items.length()) {
            val item: JSONObject = items[i] as JSONObject
            val id = item.getInt("stu_id")
            val name = item.getString("stu_name")
            val club = item.getString("stu_club")
            val school = item.getString("stu_school")
            val image = item.getString("stu_image")

            students.add(Student(id, name, club, school, image))
        }
    }

    override fun onCleared() {
        super.onCleared()
        queue.cancelAll(QUEUE_TAG)
    }
}