package kr.ac.kumoh.s20180088.termproject

import android.app.Application
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

class SchoolViewModel(application: Application) : AndroidViewModel(application) {
    data class School(
        var sch_id: Int,
        var sch_name: String,
        var sch_description: String,
        var sch_image: String
    )

    companion object {
        const val QUEUE_TAG = "SchoolVolleyRequest"
        const val SERVER_URL = "https://expresssongdb-rwhxi.run.goorm.io/"
    }

    private val schools = ArrayList<School>()
    private val _list = MutableLiveData<ArrayList<School>>()
    val list: LiveData<ArrayList<School>>
        get() = _list

    private var queue: RequestQueue
    val imageLoader: ImageLoader

    init {
        _list.value = schools
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

    fun getImageUrl(i: Int): String = "$SERVER_URL/school/" + URLEncoder.encode(schools[i].sch_image, "utf-8")


    fun requestSchool() {
        // NOTE: 서버 주소는 본인의 서버 IP 사용할 것
        // Array를 반환할 경우에는 JsonObjectRequest 대신 JsonArrayRequest 사용
        val request = JsonArrayRequest(
            Request.Method.GET,
            SERVER_URL + "/schools",
            null,
            {
                schools.clear()
                parseJson(it)
                _list.value = schools
            },
            {
                Toast.makeText(getApplication(), it.toString(), Toast.LENGTH_LONG).show()
            }
        )

        request.tag = QUEUE_TAG
        queue.add(request)
    }

    private fun parseJson(items: JSONArray) {
        for (i in 0 until items.length()) {
            val item: JSONObject = items[i] as JSONObject
            val id = item.getInt("sch_id")
            val name = item.getString("sch_name")
            val description = item.getString("sch_description")
            val image = item.getString("sch_image")

            schools.add(School(id, name, description, image))
        }
    }

    override fun onCleared() {
        super.onCleared()
        queue.cancelAll(QUEUE_TAG)
    }
}