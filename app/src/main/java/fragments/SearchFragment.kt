package fragments

import adapters.SearchListAdapter
import models.SearchItemDetails
import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.munche.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : Fragment() {

    private val firebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var searchList : List<SearchItemDetails> = ArrayList()
    private val searchListAdapter  = SearchListAdapter(searchList)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_search, container, false)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        searchItemRecyclerView.hasFixedSize()
        searchItemRecyclerView.layoutManager = LinearLayoutManager(context)
        searchItemRecyclerView.adapter = searchListAdapter

        searchEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                val searchText: String = searchEditText.text.toString()

                searchInFirestore(searchText.toLowerCase())

            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

    }

    @SuppressLint("LogNotTimber")
    private fun searchInFirestore(searchText: String) {
        firebaseFirestore.collection("RestaurantList").orderBy("search_restaurant")
                .startAt(searchText)
                .endAt("$searchText\uf8ff").get().addOnCompleteListener {
                    if (it.isSuccessful){
                        searchList = it.result!!.toObjects(SearchItemDetails::class.java)
                        searchListAdapter.searchList = searchList
                        searchListAdapter.notifyDataSetChanged()
                    }
                }

    }

}