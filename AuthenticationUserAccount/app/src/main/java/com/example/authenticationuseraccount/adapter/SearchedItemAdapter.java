package com.example.authenticationuseraccount.adapter;

import static com.example.authenticationuseraccount.common.Constants.PERMISSION_REQUEST_CODE;

import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.media3.common.MediaItem;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.fragment.FragmentSearchOptionBottomSheet;
import com.example.authenticationuseraccount.model.IClickSearchOptionItemListener;
import com.example.authenticationuseraccount.model.ItemSearchOption;
import com.example.authenticationuseraccount.model.business.Album;
import com.example.authenticationuseraccount.model.business.Artist;
import com.example.authenticationuseraccount.model.business.Song;
import com.example.authenticationuseraccount.service.MediaItemHolder;
import com.example.authenticationuseraccount.utils.CustomDownloadManager;

import java.util.ArrayList;
import java.util.List;

public class SearchedItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_SONG = 1;
    private static final int VIEW_TYPE_ARTIST = 2;
    private static final int VIEW_TYPE_ALBUM = 3;
    private Context mContext;
    private FragmentActivity fragmentActivity;
    private List<Object> listItems;

    public SearchedItemAdapter(Context context, FragmentActivity fragmentActivity, List<Object> song) {
        this.mContext = context;
        this.fragmentActivity = fragmentActivity;
        this.listItems = song;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_SONG:
                View songView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_searched_song_result, parent, false);
                return new SearchedSongViewHolder(songView);
            case VIEW_TYPE_ARTIST:
                View artistView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_searched_artist_result, parent, false);
                return new SearchedArtistViewHolder(artistView);
            case VIEW_TYPE_ALBUM:
                View albumView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_searched_album_result, parent, false);
                return new SearchedAlbumViewHolder(albumView);
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object item = listItems.get(position);
        if (item == null) {
            return;
        }
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_SONG:
                Song song = (Song) item;
                SearchedSongViewHolder searchedSongViewHold = (SearchedSongViewHolder) holder;
                searchedSongViewHold.tvSongName.setText(song.getName());
                searchedSongViewHold.tvArtistName.setText(song.getArtist());
                searchedSongViewHold.tvAlbumName.setText(song.getAlbum());
                searchedSongViewHold.tvSongName.setSelected(true);
                searchedSongViewHold.tvArtistName.setSelected(true);
                searchedSongViewHold.tvAlbumName.setSelected(true);
                Glide.with(mContext)
                        .load(song.getImageURL())
                        .into(searchedSongViewHold.imgSong);

                searchedSongViewHold.container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MediaItemHolder.getInstance().setMediaItem(song);
                        fragmentActivity.finish();
                    }
                });

                searchedSongViewHold.tvOverflowMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickOpenSearchOptionBottomSheetFragment(song);
                    }
                });
                break;

            case VIEW_TYPE_ARTIST:
                Artist artist = (Artist) item;
                SearchedArtistViewHolder searchedArtistViewHolder = (SearchedArtistViewHolder) holder;
                searchedArtistViewHolder.tvArtistName.setText((artist.getName()));
                Glide.with(mContext)
                        .load(artist.getImageURL())
                        .into(searchedArtistViewHolder.imgArtist);
                break;
            case VIEW_TYPE_ALBUM:
                Album album = (Album) item;
                SearchedAlbumViewHolder searchedAlbumViewHolder = (SearchedAlbumViewHolder) holder;
                searchedAlbumViewHolder.tvAlbumName.setText(album.getName());
                Glide.with(mContext)
                        .load(album.getImageURL())
                        .into(searchedAlbumViewHolder.imgAlbum);
                break;
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    MediaItem mediaItem = null;
    FragmentSearchOptionBottomSheet fragmentSearchOptionBottomSheet;
    private void clickOpenSearchOptionBottomSheetFragment(Song song) {
        List<ItemSearchOption> itemSearchOptionList = new ArrayList<>();
        itemSearchOptionList.add(new ItemSearchOption(R.drawable.ic_love, "Thích"));
        itemSearchOptionList.add(new ItemSearchOption(R.drawable.ic_download, "Tải xuống"));
        itemSearchOptionList.add(new ItemSearchOption(R.drawable.library_add_24px, "Thêm vào danh sách phát"));
        itemSearchOptionList.add(new ItemSearchOption(R.drawable.ic_play_next, "Phát tiếp theo"));
        itemSearchOptionList.add(new ItemSearchOption(R.drawable.ic_add_to_queue, "Thêm vào hàng đợi"));

        fragmentSearchOptionBottomSheet = new FragmentSearchOptionBottomSheet(itemSearchOptionList, new IClickSearchOptionItemListener() {
            @Override
            public void clickSearchOptionItem(ItemSearchOption itemSearchOption) {
                switch (itemSearchOption.getText()) {
                    case "Thích":
                        Toast.makeText(mContext, "Thích clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case "Tải xuống":
                        fragmentSearchOptionBottomSheet.dismiss();
                        HandleDownload(song.getSongURL(),song.getName());
                        break;
                    case "Thêm vào danh sách phát":
                        Toast.makeText(mContext, "Thêm vào danh sách phát clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case "Phát tiếp theo":
                        fragmentSearchOptionBottomSheet.dismiss();
                        int currentSongIndex = MediaItemHolder.getInstance().getMediaController().getCurrentMediaItemIndex();
                        MediaItemHolder.getInstance().getListSongs().add(currentSongIndex + 1, song);
                        mediaItem = MediaItem.fromUri(song.getSongURL());
                        MediaItemHolder.getInstance().getMediaController().addMediaItem(currentSongIndex + 1, mediaItem);
                        Toast.makeText(mContext, "", Toast.LENGTH_SHORT).show();
                        break;
                    case "Thêm vào hàng đợi":
                        fragmentSearchOptionBottomSheet.dismiss();
                        MediaItemHolder.getInstance().getListSongs().add(song);
                        mediaItem = MediaItem.fromUri(song.getSongURL());
                        MediaItemHolder.getInstance().getMediaController().addMediaItem(mediaItem);
                        Toast.makeText(mContext, "Thêm vào hàng đợi clicked", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        // Handle default action
                        Toast.makeText(mContext, "Unknown option clicked", Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        });
        fragmentSearchOptionBottomSheet.show(fragmentActivity.getSupportFragmentManager(), fragmentSearchOptionBottomSheet.getTag());
    }

    @Override
    public int getItemCount() {
        if (listItems != null) {
            return listItems.size();
        }
        return 0;
    }

    private void HandleDownload(String fileUrl,String filename) {
        if (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(fragmentActivity, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        } else {
            // Permission already granted, proceed with downloading
            CustomDownloadManager.getInstance(mContext).downloadFile(fileUrl,filename);

        }
    }

    @Override
    public int getItemViewType(int position) {
        Object item = listItems.get(position);
        if (item instanceof Song) {
            return VIEW_TYPE_SONG;
        } else if (item instanceof Artist) {
            return VIEW_TYPE_ARTIST;
        } else if (item instanceof Album) {
            return VIEW_TYPE_ALBUM;
        }
        return -1;
    }

    public class SearchedSongViewHolder extends RecyclerView.ViewHolder {
        private TextView tvSongName, tvArtistName, tvAlbumName, tvOverflowMenu;
        private ImageView imgSong;

        private ConstraintLayout container;

        public SearchedSongViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.layout_container);
            tvSongName = itemView.findViewById(R.id.tv_nameSong);
            tvArtistName = itemView.findViewById(R.id.tv_name_artist);
            tvAlbumName = itemView.findViewById(R.id.tv_album_name);
            imgSong = itemView.findViewById(R.id.imageview_song);
            tvOverflowMenu = itemView.findViewById(R.id.overflow_menu);
        }
    }

    public class SearchedArtistViewHolder extends RecyclerView.ViewHolder {
        private TextView tvArtistName;
        private ImageView imgArtist;

        public SearchedArtistViewHolder(@NonNull View itemView) {
            super(itemView);
            tvArtistName = itemView.findViewById(R.id.tv_artist_name);
            imgArtist = itemView.findViewById(R.id.img_artist);
        }
    }

    public class SearchedAlbumViewHolder extends RecyclerView.ViewHolder {
        private TextView tvAlbumName;
        private ImageView imgAlbum;

        public SearchedAlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAlbumName = itemView.findViewById(R.id.tv_album_name);
            imgAlbum = itemView.findViewById(R.id.img_album);
        }
    }

}
