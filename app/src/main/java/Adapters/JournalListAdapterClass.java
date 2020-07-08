package Adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myjournal.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import Utils.Journal;

public class JournalListAdapterClass extends RecyclerView.Adapter<JournalListAdapterClass.ViewHolder> {

    private Context mContext;
    private List<Journal> mData;

    public JournalListAdapterClass(Context mContext, List<Journal> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.journal_list_single_item , parent , false);
        return new ViewHolder(view , mContext);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Journal journal = mData.get(position);
        String imageUrl;

        holder.item_title.setText("Title : " + journal.getTitle());
        holder.item_thought.setText("Thought : " + journal.getThought());

        imageUrl = journal.getImageURL();

        Picasso.get().load(imageUrl).placeholder(R.drawable.basic_layout).fit().into(holder.item_imageBg);

        String timeAgo = (String) DateUtils.getRelativeTimeSpanString(journal.getTimeAdded().getSeconds() * 1000);
        holder.item_createdAt.setText(timeAgo);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView item_title,item_thought,item_createdAt;
        private ImageView item_imageBg;

        public ViewHolder(@NonNull View itemView,Context ctx) {
            super(itemView);

            mContext = ctx;

            item_title = itemView.findViewById(R.id.journal_list_title);
            item_thought = itemView.findViewById(R.id.journal_list_thought);
            item_createdAt = itemView.findViewById(R.id.journal_list_timestamp);

            item_imageBg = itemView.findViewById(R.id.journal_list_imageView);
        }
    }
}
