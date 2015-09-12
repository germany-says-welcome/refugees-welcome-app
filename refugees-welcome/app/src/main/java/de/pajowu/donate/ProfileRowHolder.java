package de.pajowu.donate;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
 
public class ProfileRowHolder extends RecyclerView.ViewHolder {
     
    ImageView imageView;
    TextView textView;
 
    public ProfileRowHolder(View view) {
        super(view);
        this.textView = (TextView) view.findViewById(R.id.title);
        this.imageView = (ImageView) view.findViewById(R.id.image);
    }
}