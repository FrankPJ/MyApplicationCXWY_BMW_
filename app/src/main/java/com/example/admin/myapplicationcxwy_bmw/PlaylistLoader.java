package com.example.admin.myapplicationcxwy_bmw;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;

import com.example.admin.myapplicationcxwy_bmw.model.Playlist;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;


/**
 * Created by hefuyi on 2016/11/4.
 */

public class PlaylistLoader {

    private static ArrayList<Playlist> mPlaylistList;

    public static Observable<List<Playlist>> getPlaylists(final Context context, final boolean defaultIncluded) {
        return Observable.create(new ObservableOnSubscribe<List<Playlist>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Playlist>> e) throws Exception {
                mPlaylistList = new ArrayList<>();

//                if (defaultIncluded)
//                    makeDefaultPlaylists(context);

                Cursor mCursor = makePlaylistCursor(context);

                if (mCursor != null && mCursor.moveToFirst()) {
                    do {

                        final long id = mCursor.getLong(0);

                        final String name = mCursor.getString(1);

                        final int songCount = getSongCountForPlaylist(context, id);

                        final Playlist playlist = new Playlist(id, name, songCount);

                        mPlaylistList.add(playlist);
                    } while (mCursor.moveToNext());
                }
                if (mCursor != null) {
                    mCursor.close();
                    mCursor = null;
                }
                e.onNext(mPlaylistList);
                e.onComplete();
            }


        });
    }

//    private static void makeDefaultPlaylists(Context context) {
//        final Resources resources = context.getResources();
//
//        final Playlist topTracks = new Playlist(ListenerUtil.PlaylistType.Favourate.mId,
//                resources.getString(ListenerUtil.PlaylistType.Favourate.mTitleId), -1);
//        mPlaylistList.add(topTracks);
//    }

    private static Cursor makePlaylistCursor(final Context context) {
        return context.getContentResolver().query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                new String[]{
                        BaseColumns._ID,
                        MediaStore.Audio.PlaylistsColumns.NAME
                }, null, null, MediaStore.Audio.Playlists.DEFAULT_SORT_ORDER);
    }
    public static final String MUSIC_ONLY_SELECTION = MediaStore.Audio.AudioColumns.IS_MUSIC + "=1"
            + " AND " + MediaStore.Audio.AudioColumns.TITLE + " != ''";
    private static int getSongCountForPlaylist(final Context context, final long playlistId) {
        Cursor c = context.getContentResolver().query(
                MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId),
                new String[]{BaseColumns._ID}, MUSIC_ONLY_SELECTION, null, null);

        if (c != null) {
            int count = 0;
            if (c.moveToFirst()) {
                count = c.getCount();
            }
            c.close();
            c = null;
            return count;
        }

        return 0;
    }

    public static void deletePlaylists(Context context, long playlistId) {
        Uri localUri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("_id IN (");
        localStringBuilder.append((playlistId));
        localStringBuilder.append(")");
        context.getContentResolver().delete(localUri, localStringBuilder.toString(), null);
    }

}
