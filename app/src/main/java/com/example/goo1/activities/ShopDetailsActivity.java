package com.example.goo1.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.goo1.Constants;
import com.example.goo1.R;
import com.example.goo1.adapters.AdapterProductUser;
import com.example.goo1.adapters.AdapterShop;
import com.example.goo1.models.ModelProduct;
import com.example.goo1.models.ModelShop;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ShopDetailsActivity extends AppCompatActivity {

    private ImageView shopIv;
    private TextView shopNameTv,phoneTv,emailTv,openCloseTv,deliveryFeeTv,addressTv,filteredProductsTv;
    private ImageButton callBtn,mapBtn,cartBtn,backBtn,filterProductBtn;
    private EditText searchProductEt;
    private RecyclerView productsRv;

    private String shopUid;
    private String myLatitude,myLongitude;
    private String shopName,shopEmail,shopPhone,shopAddress,shopLatitude,shopLongitude;

    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelProduct> productsList;
    private AdapterProductUser adapterProductUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_details);

        shopIv=findViewById(R.id.shopIv);
        shopNameTv=findViewById(R.id.shopNameTv);
        phoneTv=findViewById(R.id.phoneTv);
        emailTv=findViewById(R.id.emailTv);
        openCloseTv=findViewById(R.id.openCloseTv);
        deliveryFeeTv=findViewById(R.id.deliveryFeeTv);
        addressTv=findViewById(R.id.addressTv);
        filteredProductsTv=findViewById(R.id.filteredProductsTv);
        callBtn=findViewById(R.id.callBtn);
        mapBtn=findViewById(R.id.mapBtn);
        cartBtn=findViewById(R.id.cartBtn);
        backBtn=findViewById(R.id.backBtn);
        filterProductBtn=findViewById(R.id.filterProductBtn);
        searchProductEt=findViewById(R.id.searchProductEt);
        productsRv=findViewById(R.id.productsRv);

        shopUid =getIntent().getStringExtra("shopUid");
        firebaseAuth = FirebaseAuth.getInstance();
        loadMyInfo();
        loadShopDetails();
        loadShopProducts();
        searchProductEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try{
                    adapterProductUser.getFilter().filter(s);

                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialPhone();
            }
        });
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMap();
            }
        });

        filterProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder =new AlertDialog.Builder(ShopDetailsActivity.this);
                builder.setTitle("Choose Category")
                        .setItems(Constants.productCategories1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                String selected =Constants.productCategories1[which];
                                filteredProductsTv.setText(selected);
                                if(selected.equals("All")){
                                    loadShopProducts();
                                }
                                else{
                                    adapterProductUser.getFilter().filter(selected);
                                }
                            }
                        }).show();
            }
        });

    }

    private void openMap() {
        String address="https://maps.google.com/maps?saddr"+myLatitude+","+myLongitude+"&daddr"+shopLatitude+","+shopLongitude;
        Intent intent =new Intent(Intent.ACTION_VIEW,Uri.parse(address));
        startActivity(intent);
    }

    private void dialPhone() {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+Uri.encode(shopPhone))));
        Toast.makeText(this, ""+shopPhone, Toast.LENGTH_SHORT).show();
    }

    private void loadMyInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds:snapshot.getChildren()) {
                            String name = "" + ds.child("name").getValue();
                            String email = "" + ds.child("email").getValue();
                            String phone = "" + ds.child("phone").getValue();
                            String profileImage = "" + ds.child("profileImage").getValue();
                            String accountType = "" + ds.child("accountType").getValue();
                            String city = "" + ds.child("city").getValue();
                            myLatitude = "" + ds.child("latitude").getValue();
                            myLongitude = "" + ds.child("longitude").getValue();


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void loadShopDetails() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("shopUid").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String name =""+dataSnapshot.child("name").getValue();
                        shopName =""+dataSnapshot.child("shopName").getValue();
                        shopEmail =""+dataSnapshot.child("email").getValue();
                        shopPhone =""+dataSnapshot.child("phone").getValue();
                        shopLatitude =""+dataSnapshot.child("latitude").getValue();
                        shopAddress =""+dataSnapshot.child("address").getValue();
                        shopLongitude =""+dataSnapshot.child("longitude").getValue();
                        String deliveryFee =""+dataSnapshot.child("deliveryFee").getValue();
                        String profileImage =""+dataSnapshot.child("profileImage").getValue();
                        String shopOpen =""+dataSnapshot.child("shopOpen").getValue();


                        shopNameTv.setText(shopName);
                        emailTv.setText(shopEmail);
                        deliveryFeeTv.setText("Delivery Fee $:"+deliveryFee);
                        addressTv.setText(shopAddress);
                        phoneTv.setText(shopPhone);
                        if(shopOpen.equals("true")){
                            openCloseTv.setText("Open");
                        }
                        else{
                            openCloseTv.setText("Closed");

                        }
                        try{
                            Picasso.get().load(profileImage).into(shopIv);
                        }catch(Exception e)
                        {

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadShopProducts() {
        productsList= new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(shopUid).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        productsList.clear();
                        for(DataSnapshot ds:dataSnapshot.getChildren()){
                            ModelProduct modelProduct =ds.getValue(ModelProduct.class);
                            productsList.add(modelProduct);
                        }
                        adapterProductUser =new AdapterProductUser(ShopDetailsActivity.this,productsList);
                        productsRv.setAdapter(adapterProductUser);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }


}