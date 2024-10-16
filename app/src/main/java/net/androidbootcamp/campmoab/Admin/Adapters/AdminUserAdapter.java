package net.androidbootcamp.campmoab.Admin.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.androidbootcamp.campmoab.R;
import net.androidbootcamp.campmoab.Classes.UserClass;

import java.util.ArrayList;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.ViewHolder2> {
    Context context;
    ArrayList<UserClass> userClassArrayList;
    ArrayList<String> uid;

    public AdminUserAdapter(ArrayList<UserClass> userClassArrayList, ArrayList<String> uid, Context context) {
        this.userClassArrayList = userClassArrayList;
        this.uid = uid;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder2 holder, int position) {
        UserClass userClass = userClassArrayList.get(position);
        holder.name.setText(userClass.getFirstName() + " " + userClass.getLastName());
        Log.d("AdminUserAdapter", "User Name: " + holder.name.getText().toString());
        holder.phoneNum.setText(userClass.getPhoneNum());
        Log.d("AdminUserAdapter","User Phone Num: " + holder.phoneNum.getText().toString());
        holder.email.setText(userClass.getEmail());
        holder.Id.setText(uid.get(position));
    }

    @Override
    public int getItemCount() {
        return userClassArrayList == null ? 0 : userClassArrayList.size();
    }

    @NonNull
    @Override
    public ViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_admin_user_card, parent, false);
        return new AdminUserAdapter.ViewHolder2(view);
    }


    //*** Sub Class to create references of the views in Card view ***\\
    // ("activity_reservations_card.menu") \\
    public class ViewHolder2 extends RecyclerView.ViewHolder {
        TextView name, phoneNum, email, Id;

        public ViewHolder2(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            phoneNum = itemView.findViewById(R.id.phoneNum);
            email = itemView.findViewById(R.id.email);
            Id = itemView.findViewById(R.id.ID);
        }

    }
}

