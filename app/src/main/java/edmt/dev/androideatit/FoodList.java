package edmt.dev.androideatit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import edmt.dev.androideatit.Interface.ItemClickListner;
import edmt.dev.androideatit.Model.Food;
import edmt.dev.androideatit.ViewHolder.FoodViewHolder;

public class FoodList extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    DatabaseReference foodList;
    String categoryId="";
    FirebaseRecyclerAdapter<Food,FoodViewHolder> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);
        //firebase dialna
        database=FirebaseDatabase.getInstance();
        foodList=database.getReference("Foods");
        recyclerView=(RecyclerView)findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        //passage de page
        if(getIntent()!=null)
            categoryId=getIntent().getStringExtra("CategoryId");
        if(!categoryId.isEmpty() && categoryId !=null)
        {
         loadListFood(categoryId);
        }
}

    private void loadListFood(String categoryId) {
        Query query = foodList.orderByChild("MenuId").equalTo(categoryId);
        FirebaseRecyclerOptions<Food> options =
                new FirebaseRecyclerOptions.Builder<Food>()
                        .setQuery(query, Food.class)
                        .build();
        adapter=new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder holder, int position, @NonNull Food model) {
            holder.food_name.setText(model.getName());
            Picasso.get().load(model.getImage()).into(holder.food_image);
            final Food local=model;
            holder.setItemClickListner(new ItemClickListner() {
                @Override
                public void onClick(View view, int position, boolean isLongClick) {
                   // Toast.makeText(FoodList.this,""+local.getName(),Toast.LENGTH_SHORT).show();
                    Intent foodDetail= new Intent(FoodList.this,FoodDetail.class);
                    foodDetail.putExtra("FoodId",adapter.getRef(position).getKey());
                    startActivity(foodDetail);
                }
            });

            }

            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item,parent,false);

                return new FoodViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
    }
    @Override
    protected void onStart() {
        Log.e("homek","Inside onStart");
        super.onStart();
        loadListFood(categoryId);
        adapter.startListening();

    }


}
