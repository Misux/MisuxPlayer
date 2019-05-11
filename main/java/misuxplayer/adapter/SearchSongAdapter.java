package com.example.francesco.masterplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.francesco.masterplayer.MainActivity;
import com.example.francesco.masterplayer.model.Song;
import com.example.francesco.masterplayer.R;

import java.util.ArrayList;

public class SearchSongAdapter extends RecyclerView.Adapter<SearchSongAdapter.SongViewHolder> implements Filterable {

    private Context context;
    private ArrayList<Song> songList;
    private RecyclerItemClickListener listener;
    private int selectedPosition;
    public MainActivity parent;
    //search var
    private ArrayList<Song> filteredUserList;
    private SongFilter songFilter;


    //MainActivity parent
    public SearchSongAdapter(Context context, ArrayList <Song> songList,  RecyclerItemClickListener listener){
        this.context = context;
        this.songList = songList;
        this.listener = listener;
        this.filteredUserList = new ArrayList<>();

    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_search_row, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SongViewHolder holder, int position) {

        final Song song = songList.get(position);
        if(song != null){
            holder.title_search.setText(song.getTitle());
            holder.artist_search.setText(song.getArtist());

            holder.bind(song, listener);
        }
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }



    public static class SongViewHolder extends RecyclerView.ViewHolder{

        private TextView title_search, artist_search;

        public SongViewHolder(View itemView) {
            super(itemView);
            title_search = (TextView) itemView.findViewById(R.id.title_search);
            artist_search = (TextView) itemView.findViewById(R.id.artist_search);

        }

        public void bind(final Song song, final RecyclerItemClickListener listener){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClickListener(song, getLayoutPosition());
                }
            });
        }

    }

    public interface RecyclerItemClickListener{
        void onClickListener(Song song, int position);
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;

    }

    public int getSelectedPosition() {
        return selectedPosition;
    }



    @Override
    public Filter getFilter() {
        if(songFilter == null)
            songFilter = new SongFilter(this, songList);
        return songFilter;
    }


    private static class SongFilter extends Filter {

        private final SearchSongAdapter adapter;

        private final ArrayList<Song> originalList;

        private final ArrayList<Song> filteredList;

        private SongFilter(SearchSongAdapter adapter, ArrayList<Song> originalList) {
            super();
            this.adapter = adapter;
            this.originalList = new ArrayList<>(originalList);
            this.filteredList = new ArrayList<>();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            filteredList.clear();
            final FilterResults results = new FilterResults();

            if (constraint.length() == 0) {
                filteredList.addAll(originalList);
            } else {
                final String filterPattern = constraint.toString().toLowerCase().trim();

                for (final Song song : originalList) {
                    if (song.getTitle().contains(filterPattern)) {
                        filteredList.add(song);
                    }
                }
            }
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            adapter.filteredUserList.clear();
            adapter.filteredUserList.addAll((ArrayList<Song>) results.values);
            adapter.notifyDataSetChanged();
        }
    }

}
