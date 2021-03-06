package com.example.artisansfinal;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ArtisanOrderRequestAdapter extends RecyclerView.Adapter<ArtisanOrderRequestAdapter.ArtisanOrderRequestViewHolder> {

    private ArrayList<orderInfo> order;
    private Context context;
    private String temp_id;
    private String temp_ballance;
    private String temp_productprice;
    private String temp_ballance_artisan;
    DatabaseReference databaseUsers;
    DatabaseReference databaseArtisans;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser userX = firebaseAuth.getCurrentUser();
    final DatabaseReference dba = FirebaseDatabase.getInstance().getReference("Orders/Artisans/" + userX.getPhoneNumber() + "/Order Requests");
    String fromMail="artisanuser@gmail.com";


    public static class ArtisanOrderRequestViewHolder extends RecyclerView.ViewHolder {
        TextView productName;
        ImageView image;
        TextView productPrice;
        TextView dueDate;
        TextView userUID;
        RelativeLayout layout;
        TextView quantity;
        CardView card;


        public ArtisanOrderRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.artisan_order_request_tv_product_name);
            image = itemView.findViewById(R.id.artisan_order_request_iv_product_image);
            productPrice = itemView.findViewById(R.id.artisan_order_request_tv_product_price);
            dueDate = itemView.findViewById(R.id.artisan_order_request_tv_date);
            userUID = itemView.findViewById(R.id.artisan_order_request_tv_user_uid);
            quantity=itemView.findViewById(R.id.artisan_order_request_tv_quantity);
            layout = itemView.findViewById(R.id.artisan_order_request_rl);
            card = itemView.findViewById(R.id.artisan_order_request_cv);
        }
    }

    public ArtisanOrderRequestAdapter(Context context, ArrayList<orderInfo> order) {
        this.order = order;
        this.context = context;
    }

    @NonNull
    @Override
    public ArtisanOrderRequestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.artisan_order_request_layout, viewGroup, false);
        ArtisanOrderRequestViewHolder viewHolder = new ArtisanOrderRequestViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ArtisanOrderRequestViewHolder viewHolder, final int i) {
        final orderInfo orderX = order.get(i);

        temp_productprice=orderX.getPrice();
        viewHolder.productName.setText(orderX.getName());
        viewHolder.dueDate.setText(orderX.getDate());
        viewHolder.productPrice.setText(orderX.getPrice());
        viewHolder.userUID.setText(orderX.getUserUID());
        viewHolder.userUID.setVisibility(View.GONE);
        viewHolder.quantity.setText(orderX.getQuantity());
        //Log.d("HEY", orderX.toString() + "!" + i);
//        viewHolder.card.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //Log.d("HEY",orderX.getPrice());
//
//            }
//        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://artisansfinal.firebaseapp.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final Api api = retrofit.create(Api.class);

        //Log.d("HEY",orderX.toString());

        if (orderX.getC().equals("g")) {
            viewHolder.card.setCardBackgroundColor(Color.parseColor("#76FF03"));
        } else if (orderX.getC().equals("r")) {
            viewHolder.card.setCardBackgroundColor(Color.parseColor("#E64A19"));
        } else {
            viewHolder.card.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
        }



        databaseUsers= FirebaseDatabase.getInstance().getReference("User");
        databaseArtisans= FirebaseDatabase.getInstance().getReference("Artisans");




        databaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot user: dataSnapshot.getChildren()){
                    if(user.child("userEmail").getValue().toString().equals(orderX.getUserEmail()))

                    {

                        temp_id=user.child("userPnumber").getValue().toString();
                        temp_ballance=user.child("userWallet").getValue().toString();

                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseDatabase.getInstance().getReference("Artisans").child(userX.getPhoneNumber()).child("wallet").
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                        temp_ballance_artisan=dataSnapshot.getValue(String.class);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        viewHolder.card.setOnClickListener(new View.OnClickListener() {

            private String artisanKey, userKey;


            @Override
            public void onClick(View v) {
                if(orderX.getC().equals("d")) {
                    final DatabaseReference dbu = FirebaseDatabase.getInstance().getReference("Orders/Users/" + orderX.userUID + "/Orders Requested");
                    Log.d("onClick", orderX.toString());

                    dba.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //Log.d("HEY",dataSnapshot.getChildren().toString());
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                //Log.d("HEY",child.child("userUID").getValue().toString());
                                //Log.d("HERE",viewHolder.userUID.getText().toString()+" "+child.getKey());
                                if (child.child("userUID").getValue().toString().equals(orderX.userUID) && child.child("date").getValue().toString().equals(orderX.date) && child.child("name").getValue().toString().equals(orderX.name)) {
                                    //Log.d("HERE",child.getKey());
                                    artisanKey = child.getKey();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    dbu.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //Log.d("HEY",dataSnapshot.getChildren().toString());
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                //Log.d("HEY",child.child("userUID").getValue().toString());
                                //Log.d("HERE",viewHolder.userUID.getText().toString()+" "+child.getKey());
                                //Log.d("HEY",child.getKey());
                                //Log.d("HEY2",child.getValue().toString());
                                //Log.d("HEY2",child.child("userUID").getValue().toString());
                                //Log.d("HEY2",child.child("date").getValue().toString());
                                //Log.d("HEY2",child.child("name").getValue().toString());
                                if (child.child("userUID").getValue().toString().equals(orderX.userUID) && child.child("date").getValue().toString().equals(orderX.date) && child.child("name").getValue().toString().equals(orderX.name)) {
                                    userKey = child.getKey();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("Order Request");
                    builder.setMessage("Accept order request?");

                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            float f=Float.parseFloat(temp_ballance);
                            float t=Float.parseFloat(temp_productprice);
                            float a=Float.parseFloat(temp_ballance_artisan);

                                a=a+t;
                                f=f-t;


                                FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
                                DatabaseReference myRef=firebaseDatabase.getReference();
                                myRef.child("User").child(temp_id).child("userWallet").setValue(Float.toString(f));
                                myRef.child("Artisans").child(userX.getPhoneNumber()).child("wallet").setValue(Float.toString(a));




                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    sendMailUsingSendGrid(fromMail,orderX.getUserEmail(),
                                            "Email from the artisan developers team",
                                            "Your Order has been accepted by the artisan and an amount of  Rs."+temp_productprice+" is deducted from your wallet.Your product will be delivered shortly.Have a Good Day Sir/Maam :)");

                                    Toast.makeText(context,"Sending mail...", Toast.LENGTH_SHORT).show();
                                }


                            });

                            orderX.setC("g");
                            Log.d("HERE", "Val" + i);
                            viewHolder.card.setCardBackgroundColor(Color.parseColor("#76FF03"));
                            DatabaseReference dbam = FirebaseDatabase.getInstance().getReference("Orders/Artisans/" + userX.getPhoneNumber() + "/Orders Pending");
                            String x = dbam.push().getKey();
                            move(dba.child(artisanKey), dbam.child(x), artisanKey);
                            //dba.child(artisanKey).setValue(null);
                            //Log.d("HERE2", dba.child(artisanKey).toString());
                            DatabaseReference dbum = FirebaseDatabase.getInstance().getReference("Orders/Users/" + viewHolder.userUID.getText().toString() + "/Orders Accepted");
                            x = dbum.push().getKey();
                            //dbum.child(x).setValue("Hai");
                            move(dbu.child(userKey), dbum.child(x), userKey);
                            //Log.d("HERE", dbu.child(userKey).toString());
                            //dbu.child(userKey).setValue(null);
                            //Log.d("HERE2",artisanKey);
                            //Log.d("HERE2",userKey);
                            Call<ResponseBody> call=api.sendNotification(orderX.fcmToken,"Order Accepted!","Your order for "+viewHolder.productName.getText().toString()+" has been accepted by the artisan");
                            call.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {

                                }
                            });

                            FirebaseDatabase.getInstance().getReference("ArtisanProducts/"+userX.getPhoneNumber()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for(DataSnapshot data: dataSnapshot.getChildren()){
                                        if(data.child("productID").getValue().toString().equals(orderX.productID)){
                                            FirebaseDatabase.getInstance().getReference("ArtisanProducts/"+userX.getPhoneNumber()+"/"+data.getKey()).child("numberOfSales").setValue(Integer.toString(Integer.parseInt(data.child("numberOfSales").getValue().toString())+1));
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            FirebaseDatabase.getInstance().getReference("Categories").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for(DataSnapshot data: dataSnapshot.getChildren()){
                                        for(DataSnapshot data2:data.getChildren()){
                                            if(data2.getKey().equals(orderX.productID)){
                                                Log.d("HEY",data.getKey()+"!"+data2.getKey());
                                                FirebaseDatabase.getInstance().getReference("Categories/"+data.getKey()+"/"+data2.getKey()).child("numberOfSales").setValue(Integer.toString(Integer.parseInt(data2.child("numberOfSales").getValue().toString())+1));
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            FirebaseDatabase.getInstance().getReference("Products").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for(DataSnapshot data: dataSnapshot.getChildren()){
                                        if(data.getKey().equals(orderX.productID)){
                                            FirebaseDatabase.getInstance().getReference("Products/"+data.getKey()).child("numberOfSales").setValue(Integer.toString(Integer.parseInt(data.child("numberOfSales").getValue().toString())+1));
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }

                        private void sendMailUsingSendGrid(String from, String to, String subject, String mailBody){
                            Hashtable<String, String> params = new Hashtable<>();
                            params.put("to", to);
                            params.put("from", from);
                            params.put("subject", subject);
                            params.put("text", mailBody);

                            SendGridAsyncTask email = new SendGridAsyncTask();
                            try{
                                email.execute(params);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }



                    });

                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                @Override


                                public void run() {
                                    sendMailUsingSendGrid(fromMail, orderX.getUserEmail(),
                                            "Email from the artisan developers team",
                                            "Sorry to say but your order is rejected as the artisan is already busy.Please place order to some other artisan.Have a Good Day Sir/Maam :)");

                                    Toast.makeText(context,"Sending mail...", Toast.LENGTH_SHORT).show();
                                }
                            });


                            orderX.setC("r");
                            viewHolder.card.setCardBackgroundColor(Color.parseColor("#E64A19"));
                            dba.child(artisanKey).setValue(null);
                            dbu.child(userKey).setValue(null);
                            Call<ResponseBody> call=api.sendNotification(orderX.fcmToken,"Order Rejected :(","Your order for "+viewHolder.productName.getText().toString()+" has been rejected as the artisan is very busy at this point.");
                            call.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {

                                }
                            });
                            dialog.dismiss();
                        }


                        private void sendMailUsingSendGrid(String from, String to, String subject, String mailBody){
                            Hashtable<String, String> params = new Hashtable<>();
                            params.put("to", to);
                            params.put("from", from);
                            params.put("subject", subject);
                            params.put("text", mailBody);

                            SendGridAsyncTask email = new SendGridAsyncTask();
                            try{
                                email.execute(params);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });


                    Dialog dialog = builder.create();
                    builder.show();

                }

            }});
    }

    @Override
    public int getItemCount() {
        return order.size();
    }

    public void added(orderInfo orderX) {
        order.add(orderX);
        notifyItemInserted(order.indexOf(orderX));
    }

    private void move(final DatabaseReference fromPath, final DatabaseReference toPath, final String key) {
        fromPath.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                toPath.setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                        if (firebaseError != null) {
                            System.out.println("Copy failed");
                        } else {
                            System.out.println("Success");
                            //Log.d("HERE",key);
                            //Log.d("HERE2",fromPath.getParent().child(key).toString());
                            fromPath.getParent().child(key).setValue(null);
                        }
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
