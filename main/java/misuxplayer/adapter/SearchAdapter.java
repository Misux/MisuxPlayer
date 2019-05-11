package com.example.francesco.masterplayer.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.francesco.masterplayer.model.Song;
import com.example.francesco.masterplayer.R;

public class SearchAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private int id;
    private ArrayList<Song> songList;
    private ArrayList<Song> songValues;
    private DataFilter filter;

    public SearchAdapter(Context context, int textViewResourceId, ArrayList<Song> songList) {
        this.context = context;
        this.id = textViewResourceId;
        this.songList = songList;
        songValues = new ArrayList<Song>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_search_row, parent, false);
        Holder holder = new Holder();
        holder.title_search = view.findViewById(R.id.title_search);
        holder.artist_search = view.findViewById(R.id.artist_search);

        holder.title_search.setText(songList.get(position).getTitle());
        holder.artist_search.setText(songList.get(position).getArtist());
        return view;
    }

    @Override
    public int getCount() {
        return songList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return songList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    private class Holder {
        private TextView title_search, artist_search;

    }

    @Override
    public Filter getFilter() {

        if (filter == null)
            filter = new DataFilter();
        return filter;
    }

    private class DataFilter extends Filter {

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            songList = (ArrayList<Song>) results.values;
            notifyDataSetChanged();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults r = new FilterResults();

            if (songValues == null) {

                synchronized (songList) {
                    songValues = new ArrayList<Song>(songList);
                }

            }

            if (constraint == null || constraint.length() == 0) {

                synchronized (songList) {
                    ArrayList<Song> list = new ArrayList<Song>(songValues);
                    r.values = list;
                    r.count = list.size();
                }

            } else {

                ArrayList<Song> values = (ArrayList<Song>) songValues;
                int count = values.size();
                ArrayList<Song> list = new ArrayList<Song>();
                String prefix = constraint.toString().toLowerCase();

                for (int i = 0; i < count; i++) {

                    if (values.get(i).getTitle().toLowerCase().contains(prefix)) {
                        list.add(songList.get(i));
                    }

                }

                r.values = list;
                r.count = list.size();

            }

            return r;

        }
    }
}