package oska.joyiochat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import oska.joyiochat.R;
import oska.joyiochat.module.ChatContactItem;
import oska.joyiochat.module.FavPeopleItem;


/**
 * Created by froger_mcs on 05.11.14.
 * <p/>
 * FavPeopleAdapter: An adapter for the ChatRoom  which put  in the part of recyclerview in the ChatRoomFragment ,
 * Model use:  FavPeopleItem
 */
public class ChatContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEW_TYPE_DEFAULT = 1;
    public static final int VIEW_TYPE_LOADER = 2;


    private List<ChatContactItem> chatContactItems = new ArrayList<>();
    private Context context;
    private OnChatContactItemClickListener onChatContactItemClickListener;


    public ChatContactAdapter(Context context, List<ChatContactItem> chatContactItems) {
        this.chatContactItems = chatContactItems;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_DEFAULT) {
            View view = LayoutInflater.from(context).inflate(R.layout.row_contact_chat, parent, false);

            ChatContactHolder chatContactHolder = new ChatContactHolder(view);
            setupClickableViews(view, chatContactHolder);
            return chatContactHolder;
        }
        return null;
    }

    private void setupClickableViews(final View view, final  ChatContactHolder chatContactHolder) {
        chatContactHolder.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChatContactItemClickListener.onChatContactImageClick(view, chatContactHolder.getAdapterPosition());
            }
        });
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ((ChatContactHolder) viewHolder).bindView(chatContactItems.get(position));
    }


    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_DEFAULT;
    }

    @Override
    public int getItemCount() {
        return chatContactItems.size();
    }


    public void setOnChatContactClickListener(OnChatContactItemClickListener onChatContactItemClickListener) {
        this.onChatContactItemClickListener = onChatContactItemClickListener;
    }

    public static class ChatContactHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.chat_contact_profile_image)
        ImageView profileImage;

        @Bind(R.id.chat_contact_name)
        TextView contactName;

        ChatContactItem chatContactItem;

        public ChatContactHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bindView(ChatContactItem chatContactItem) {
            this.chatContactItem = chatContactItem;
            profileImage.setImageResource(chatContactItem.getChatContactProfilePic());
            contactName.setText(chatContactItem.getContactName());
        }

        public ChatContactItem getChatContactItem() {
            return chatContactItem;
        }
    }


    public interface OnChatContactItemClickListener {
        void onChatContactImageClick(View v, int position);
    }
}
