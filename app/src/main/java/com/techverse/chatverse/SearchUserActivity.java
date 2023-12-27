package com.techverse.chatverse;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.techverse.chatverse.adapter.SearchUserRecyclerAdapter;
import com.techverse.chatverse.model.UserModel;
import com.techverse.chatverse.utils.AndroidUtil;
import com.techverse.chatverse.utils.FirebaseUtil;

public class SearchUserActivity extends AppCompatActivity {

    EditText searchInput;
    ImageButton searchButton;
    ImageButton backButton;
    RecyclerView recyclerView;
    SearchUserRecyclerAdapter adapter;
    ImageView blueTick;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        searchInput = findViewById(R.id.search_user_name_input);
        searchButton = findViewById(R.id.search_user_btn);
        backButton = findViewById(R.id.back_arrow);
        recyclerView = findViewById(R.id.search_user_recycler_view);
        blueTick = findViewById(R.id.bluetick);

        searchInput.requestFocus();


        backButton.setOnClickListener((v -> {
            onBackPressed();
        }));

        searchButton.setOnClickListener((v -> {
            String searchTerm = searchInput.getText().toString();
            if(searchTerm.isEmpty() || searchTerm.length()<3){
                searchInput.setError("Invalid UserId");
                return;
            }
            setupSearchRecyclerView(searchTerm);
        }));
    }

    void setupSearchRecyclerView(String searchTerm){

        if (!searchTerm.startsWith("@")) {
            searchTerm= "@" + searchTerm;
        }

        Query query = FirebaseUtil.allUserCollectionReference()
                .whereGreaterThanOrEqualTo("accountUserId", searchTerm)
                .whereLessThanOrEqualTo("accountUserId", searchTerm + '\uf8ff')
                .orderBy("accountUserId")
                .startAt(searchTerm)
                .endAt(searchTerm + '\uf8ff');

        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query, UserModel.class).build();

        adapter = new SearchUserRecyclerAdapter(options, getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(adapter!=null)
            adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(adapter!=null)
            adapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter!=null)
            adapter.notifyDataSetChanged();
    }
}