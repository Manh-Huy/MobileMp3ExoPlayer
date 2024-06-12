package com.example.authenticationuseraccount.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.media3.common.MediaItem;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.common.ErrorUtils;
import com.example.authenticationuseraccount.fragment.FragmentSearchOptionBottomSheet;
import com.example.authenticationuseraccount.model.IClickSearchOptionItemListener;
import com.example.authenticationuseraccount.model.ItemSearchOption;
import com.example.authenticationuseraccount.model.business.LocalSong;
import com.example.authenticationuseraccount.model.business.Song;
import com.example.authenticationuseraccount.service.MediaItemHolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LocalMusicAdapter extends RecyclerView.Adapter<LocalMusicAdapter.ViewHolder> {

    private List<LocalSong> musicList;
    private FragmentActivity mFragmentActivity;
    private Context mContext;
    MediaItem mediaItem = null;

    public LocalMusicAdapter(Context context, FragmentActivity fragmentActivity, List<LocalSong> musicList) {
        this.mContext = context;
        this.musicList = musicList;
        this.mFragmentActivity = fragmentActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_searched_song_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LocalSong song = musicList.get(position);

        Song songLocalTest = new Song();
        songLocalTest.setName(song.getTitle());
        songLocalTest.setArtist(song.getArtistName());
        songLocalTest.setSongURL(song.getData());

        holder.tvSongName.setText(song.getTitle());
        holder.tvArtistName.setText(song.getArtistName());
        holder.tvAlbumName.setText(song.getAlbumName());

        byte[] image = null;
        try {
            image = getAlbumArt(song.getData());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (image != null) {
            songLocalTest.setImageData(image);
            Glide.with(mContext)
                    .load(image)
                    .into(holder.imgSong);
        } else {
            Glide.with(mContext)
                    .load(R.drawable.logo)
                    .into(holder.imgSong);
        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ErrorUtils.showError(mContext, "Clicked");
                MediaItemHolder.getInstance().setMediaItem(songLocalTest);
            }
        });

        holder.tvOverflowMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ErrorUtils.showError(mContext, "3 dot clicked");
                clickOpenOptionBottomSheetFragment(songLocalTest);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (musicList != null) {
            return musicList.size();
        }
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvSongName, tvArtistName, tvAlbumName, tvOverflowMenu;
        private ImageView imgSong;
        private RelativeLayout layout;

        public ViewHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.layout_container);
            tvSongName = itemView.findViewById(R.id.tv_nameSong);
            tvArtistName = itemView.findViewById(R.id.tv_name_artist);
            tvAlbumName = itemView.findViewById(R.id.tv_album_name);
            imgSong = itemView.findViewById(R.id.imageview_song);
            tvOverflowMenu = itemView.findViewById(R.id.overflow_menu);
        }
    }

    private byte[] getAlbumArt(String path) throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(path);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }

    private void clickOpenOptionBottomSheetFragment(Song song) {
        List<ItemSearchOption> itemSearchOptionList = new ArrayList<>();
        itemSearchOptionList.add(new ItemSearchOption(R.drawable.ic_add_to_queue, "Thêm vào hàng đợi"));
        itemSearchOptionList.add(new ItemSearchOption(R.drawable.ic_play_next, "Phát tiếp theo"));
        itemSearchOptionList.add(new ItemSearchOption(R.drawable.library_add_24px, "Thêm vào danh sách phát"));

        FragmentSearchOptionBottomSheet fragmentSearchOptionBottomSheet = new FragmentSearchOptionBottomSheet(itemSearchOptionList, new IClickSearchOptionItemListener() {
            @Override
            public void clickSearchOptionItem(ItemSearchOption itemSearchOption) {
                switch (itemSearchOption.getText()) {
                    case "Thêm vào danh sách phát":
                        Toast.makeText(mContext, "Thêm vào danh sách phát clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case "Phát tiếp theo":
                        if (MediaItemHolder.getInstance().getListSongs().isEmpty()) {
                            MediaItemHolder.getInstance().getListSongs().add(song);
                            mediaItem = MediaItem.fromUri(song.getSongURL());
                            MediaItemHolder.getInstance().getMediaController().addMediaItem(mediaItem);
                        } else {
                            int currentSongIndex = MediaItemHolder.getInstance().getMediaController().getCurrentMediaItemIndex();
                            MediaItemHolder.getInstance().getListSongs().add(currentSongIndex + 1, song);
                            mediaItem = MediaItem.fromUri(song.getSongURL());
                            MediaItemHolder.getInstance().getMediaController().addMediaItem(currentSongIndex + 1, mediaItem);
                        }
                        Toast.makeText(mContext, "Phát tiếp theo clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case "Thêm vào hàng đợi":
                        MediaItemHolder.getInstance().getListSongs().add(song);
                        mediaItem = MediaItem.fromUri(song.getSongURL());
                        MediaItemHolder.getInstance().getMediaController().addMediaItem(mediaItem);
                        //Toast.makeText(mContext, "Thêm vào hàng đợi clicked", Toast.LENGTH_SHORT).show();
                        Toast.makeText(mContext, "Thêm vào hàng đợi clicked: " + MediaItemHolder.getInstance().getListSongs().size(), Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        // Handle default action
                        Toast.makeText(mContext, "Unknown option clicked", Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        });
        fragmentSearchOptionBottomSheet.show(mFragmentActivity.getSupportFragmentManager(), fragmentSearchOptionBottomSheet.getTag());
    }
}

