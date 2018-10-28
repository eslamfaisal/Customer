package com.fekrah.customer.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.fekrah.customer.R;
import com.fekrah.customer.activities.ChatActivity;
import com.fekrah.customer.activities.MainActivity;
import com.fekrah.customer.models.Message;
import com.fekrah.customer.models.OrderResponse;
import com.fekrah.customer.models.Room;
import com.fekrah.customer.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.rafakob.drawme.DrawMeButton;

import java.util.List;

import static com.yalantis.ucrop.UCropFragment.TAG;

public class AcceptedOrderDriversAdapter extends RecyclerView.Adapter<AcceptedOrderDriversAdapter.DriverViewHolder> {

    public static final String USER_ID = "user_id";
    Context context;
    List<OrderResponse> drivers;
    public static final String DRIVER_FOR_ORDER_ID = "driver_for_order_id";
    private CountDownTimer mCountDownTimer0;
    private CountDownTimer mCountDownTimer1;
    int i0 = 0;
    private CountDownTimer mCountDownTimer2;
    int i2 = 0;
    private int i1;

    public AcceptedOrderDriversAdapter(Context context, List<OrderResponse> users) {
        this.context = context;
        this.drivers = users;

    }

    @NonNull
    @Override
    public DriverViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_accepted_order_driver_item_list, viewGroup, false);
        return new DriverViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DriverViewHolder holder, final int i) {

        final OrderResponse driver = drivers.get(i);

        holder.userName.setText(driver.getDriver_name());
        holder.rate.setRating(getStars(driver));
        holder.profileImage.setImageURI(driver.getDriver_image());
        holder.distance.setText(driver.getDistance());
        holder.time.setText(driver.getEdtimated_time());
        holder.cost.setText(driver.getCost());
        holder.refuseOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("Customer_current_order")
                        .child(FirebaseAuth.getInstance().getUid()).child("drivers")
                        .child(driver.getResponse_key()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "onComplete: don");
                    }
                });
            }
        });

        holder.mProgressBar.setProgress(0);
        if (i==0){
            mCountDownTimer0 = new CountDownTimer(40000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    Log.v("Log_tag", "Tick of Progress" + i0 + millisUntilFinished);
                    i0++;
                    holder.mProgressBar.setProgress((int) i0 * 100 / (40000 / 1000));

                }

                @Override
                public void onFinish() {
                    //Do what you want
                    FirebaseDatabase.getInstance().getReference().child("drivers_current_order")
                            .child(driver.getDriver_key()).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                        FirebaseDatabase.getInstance().getReference().child("Customer_current_order")
                                                .child(FirebaseAuth.getInstance().getUid()).child("drivers")
                                                .child(drivers.get(0).getResponse_key()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    holder.mProgressBar.setProgress(0);
                                                    holder.mProgressBar.setVisibility(View.GONE);
                                                    drivers.remove(driver);
                                                    notifyDataSetChanged();
                                                }
                                            }
                                        });
                                }
                            });

                }
            };
            mCountDownTimer0.start();
        }else if (i==1){
            mCountDownTimer1 = new CountDownTimer(40000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    Log.v("Log_tag", "Tick of Progress" + i1 + millisUntilFinished);
                    i1++;
                    holder.mProgressBar.setProgress((int) i1 * 100 / (40000 / 1000));

                }

                @Override
                public void onFinish() {
                    //Do what you want
                    FirebaseDatabase.getInstance().getReference().child("drivers_current_order")
                            .child(driver.getDriver_key()).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                        FirebaseDatabase.getInstance().getReference().child("Customer_current_order")
                                                .child(FirebaseAuth.getInstance().getUid()).child("drivers")
                                                .child(drivers.get(1).getResponse_key()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    holder.mProgressBar.setProgress(0);
                                                    holder.mProgressBar.setVisibility(View.GONE);
                                                    drivers.remove(driver);
                                                    notifyDataSetChanged();
                                                }
                                            }
                                        });
                                }
                            });

                }
            };
            mCountDownTimer1.start();
        }else if (i==2){
            mCountDownTimer2 = new CountDownTimer(40000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    Log.v("Log_tag", "Tick of Progress" + i2 + millisUntilFinished);
                    i2++;
                    holder.mProgressBar.setProgress((int) i2 * 100 / (40000 / 1000));

                }

                @Override
                public void onFinish() {
                    //Do what you want
                    FirebaseDatabase.getInstance().getReference().child("drivers_current_order")
                            .child(driver.getDriver_key()).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                        FirebaseDatabase.getInstance().getReference().child("Customer_current_order")
                                                .child(FirebaseAuth.getInstance().getUid()).child("drivers")
                                                .child(drivers.get(2).getResponse_key()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    holder.mProgressBar.setProgress(0);
                                                    holder.mProgressBar.setVisibility(View.GONE);
                                                    drivers.remove(driver);
                                                    notifyDataSetChanged();
                                                }
                                            }
                                        });
                                }
                            });

                }
            };
            mCountDownTimer2.start();
        }

        holder.refuseOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.getAdapterPosition()==0){
                    mCountDownTimer0.cancel();
                    mCountDownTimer0.onFinish();
                }else if (holder.getAdapterPosition()==1){
                    mCountDownTimer1.cancel();
                    mCountDownTimer1.onFinish();

                }else if (holder.getAdapterPosition()==2){
                    mCountDownTimer2.cancel();
                    mCountDownTimer2.onFinish();

                }
            }
        });

        holder.acceptOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.getAdapterPosition()==0){
                    mCountDownTimer0.cancel();
                }else if (holder.getAdapterPosition()==1){
                    mCountDownTimer1.cancel();
                }else if (holder.getAdapterPosition()==2){
                    mCountDownTimer2.cancel();
                }

                holder.mProgressBar.setVisibility(View.GONE);
                final Room room = new Room(
                        driver.getDriver_image(),
                        "",
                        System.currentTimeMillis(),
                        driver.getDriver_key(),
                        driver.getDriver_name()
                );
                FirebaseDatabase.getInstance().getReference().child("Customer_current_order")
                        .child(FirebaseAuth.getInstance().getUid()).child("accepted_driver")
                        .setValue(drivers.get(holder.getAdapterPosition()))
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                FirebaseDatabase.getInstance().getReference().child("rooms")
                                        .child(FirebaseAuth.getInstance().getUid())
                                        .child(driver.getDriver_key())
                                        .setValue(room).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            final Room room2 = new Room(
                                                    MainActivity.currentUser.getName(),
                                                    "",
                                                    System.currentTimeMillis(),
                                                    MainActivity.currentUser.getUser_key(),
                                                    MainActivity.currentUser.getName()
                                            );
                                            FirebaseDatabase.getInstance().getReference().child("rooms")
                                                    .child(driver.getDriver_key())
                                                    .child(MainActivity.currentUser.getUser_key())
                                                    .setValue(room2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        FirebaseDatabase.getInstance().getReference().child("drivers_current_order")
                                                                .child(driver.getDriver_key()).child("offer").setValue("accept").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                FirebaseDatabase.getInstance().getReference().child("Customer_current_order")
                                                                        .child(FirebaseAuth.getInstance().getUid())
                                                                        .child("drivers").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()){
                                                                            User user = new User(
                                                                                    driver.getDriver_name(),
                                                                                    driver.getDriver_image(),
                                                                                    driver.getDriver_key()
                                                                            );
                                                                            Intent intent = new Intent(context, ChatActivity.class);
                                                                            intent.putExtra(USER_ID, user);
                                                                            context.startActivity(intent);
                                                                            drivers.clear();
                                                                            notifyDataSetChanged();
                                                                        }
                                                                    }
                                                                });


                                                            }
                                                        });

                                                    }
                                                }
                                            });

                                        }
                                    }
                                });
                            }
                        });

            }
        });



    }

    @Override
    public int getItemCount() {
        return drivers.size();
    }

    public class DriverViewHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView profileImage;
        TextView userName;
        DrawMeButton acceptOrder;
        DrawMeButton refuseOrder;
        RatingBar rate;
        View mainView;
        TextView time;
        TextView distance;
        TextView cost;
        ProgressBar mProgressBar;

        public DriverViewHolder(@NonNull View view) {
            super(view);

            mainView = view;
            profileImage = view.findViewById(R.id.profile_image);
            userName = view.findViewById(R.id.user_name);
            rate = view.findViewById(R.id.user_rate);
            acceptOrder = view.findViewById(R.id.accept);
            refuseOrder = view.findViewById(R.id.refuse);
            time = view.findViewById(R.id.time);
            cost = view.findViewById(R.id.cost);
            distance = view.findViewById(R.id.distance);
            mProgressBar = view.findViewById(R.id.progressbar);
        }
    }

    private int getStars(OrderResponse driver) {
        int stars = 0;
        int ratingUsersCount = driver.getDriver_rating_count();
        int rating = (int) driver.getDriver_rating();
        if (rating == 0 || ratingUsersCount == 0) {
            return stars;
        } else {
            int maxRating = ratingUsersCount * 5;
            //calculate
            int a = 5 * rating;
            stars = a / maxRating;
            return stars;
        }
    }

    public void add(OrderResponse user) {
        drivers.add(user);
        notifyDataSetChanged();
    }
}
