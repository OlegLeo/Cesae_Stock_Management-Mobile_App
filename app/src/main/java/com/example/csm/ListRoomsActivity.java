package com.example.csm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ListRoomsActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference roomRef = db.collection("Rooms");
    private RoomsAdapter adapter;
    private FloatingActionButton bt_roomAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_rooms);

        bt_roomAdd = findViewById(R.id.bt_roomAdd);
        setUpRecyclerView();

        adapter.setOnItemClickListener(new RoomsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int pos) {
                Rooms rooms = documentSnapshot.toObject(Rooms.class);
                String path = documentSnapshot.getReference().getPath();

                Intent i = new Intent(ListRoomsActivity.this, RoomsDataActivity.class);
                i.putExtra("path", path);
                startActivity(i);
            }
        });

        bt_roomAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ListRoomsActivity.this, newRoomActivity.class);
                startActivity(i);
            }
        });

    }

    private void setUpRecyclerView() {

        Query query = roomRef.orderBy("roomName", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Rooms> options = new FirestoreRecyclerOptions.Builder<Rooms>()
                .setQuery(query, Rooms.class).build();

        adapter = new RoomsAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.rv_listRooms);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    ////// Search button/////

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search,menu);
        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                txtSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                txtSearch(query);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void txtSearch(String str){
        Query query = roomRef.orderBy("roomName", Query.Direction.ASCENDING).startAt(str).endAt(str+"~");
        FirestoreRecyclerOptions<Rooms> options = new FirestoreRecyclerOptions.Builder<Rooms>()
                .setQuery(query, Rooms.class).build();

        adapter = new RoomsAdapter(options);
        adapter.startListening();

        RecyclerView recyclerView = findViewById(R.id.rv_listRooms);
        recyclerView.setAdapter(adapter);
    }
    /////

}