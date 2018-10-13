package com.example.admin.myapplicationcxwy_bmw;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.example.admin.myapplicationcxwy_bmw.model.Album;
import com.example.admin.myapplicationcxwy_bmw.model.Artist;
import com.example.admin.myapplicationcxwy_bmw.model.FolderInfo;
import com.example.admin.myapplicationcxwy_bmw.model.Song;
import com.example.mylibrary.LayoutManager;

import org.reactivestreams.Subscriber;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends Activity {
    public final static String MCU_MSG_CAN_ALL_INFO = "com.choiceway.eventcenter.EventUtils.MCU_MSG_CAN_ALL_INFO";
    public final static String CAR_AIR_DATA = "EventUtils.CAR_AIR_DATA";


    private static Cursor makeArtistCursor(Context context, String selection, String[] paramArrayOfString) {
        final String artistSortOrder = PreferencesUtility.getInstance(context).getArtistSortOrder();
        return context.getContentResolver().query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, new String[]{"_id", "artist", "number_of_albums", "number_of_tracks"}, selection, paramArrayOfString, artistSortOrder);
    }

    public static Observable<List<Artist>> getAllArtists(Context context) {
        return getArtistsForCursor(makeArtistCursor(context, null, null));
    }

    private static Observable<List<Artist>> getArtistsForCursor(final Cursor cursor) {
        return Observable.create(new ObservableOnSubscribe<List<Artist>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Artist>> e) throws Exception {
                List<Artist> arrayList = new ArrayList<Artist>();
                if ((cursor != null) && (cursor.moveToFirst()))
                    do {
                        arrayList.add(new Artist(cursor.getLong(0), cursor.getString(1), cursor.getInt(2), cursor.getInt(3)));
                    }
                    while (cursor.moveToNext());
                if (cursor != null) {
                    cursor.close();
                }
                e.onNext(arrayList);
                e.onComplete();
            }


        });
    }


    /**
     * 检索包含音频文件的文件夹, 并统计该文件夹下的歌曲数目
     *
     * @return
     */
    public static Observable<List<FolderInfo>> getFoldersWithSong(final Context context) {
        final List<FolderInfo> folderInfos = new ArrayList<FolderInfo>();
        final String num_of_songs = "num_of_songs";
        final String[] projection = new String[]{MediaStore.Files.FileColumns.DATA,
                "count(" + MediaStore.Files.FileColumns.PARENT + ") as " + num_of_songs};

        final String selection = " is_music=1 AND title != '' " + " ) " + " group by ( "
                + MediaStore.Files.FileColumns.PARENT;

        return Observable.create(new ObservableOnSubscribe<List<FolderInfo>>() {
            @Override
            public void subscribe(ObservableEmitter<List<FolderInfo>> e) throws Exception {
                Cursor cursor = context.getContentResolver().query(
                        MediaStore.Files.getContentUri("external"), projection, selection, null, null);

                if (cursor != null) {
                    int index_data = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
                    int index_num_of_songs = cursor.getColumnIndex(num_of_songs);

                    while (cursor.moveToNext()) {

                        // 获取每个目录下的歌曲数量
                        int songCount = cursor.getInt(index_num_of_songs);

                        // 获取文件的路径，如/storage/sdcard0/MIUI/music/Baby.mp3
                        String filepath = cursor.getString(index_data);

                        // 获取文件所属文件夹的路径，如/storage/sdcard0/MIUI/music
                        String folderpath = filepath.substring(0, filepath.lastIndexOf(File.separator));

                        // 获取文件所属文件夹的名称，如music
                        String foldername = folderpath.substring(folderpath.lastIndexOf(File.separator) + 1);

                        folderInfos.add(new FolderInfo(foldername, folderpath, songCount));
                    }
                }

                if (cursor != null) {
                    cursor.close();
                }
                e.onNext(folderInfos);
            }


        });
    }

    public Cursor makeSongCursor(Context context, String selection, String[] paramArrayOfString) {
        final String songSortOrder = PreferencesUtility.getInstance(context).getSongSortOrder();
        return makeSongCursor(context, selection, paramArrayOfString, songSortOrder);
    }

    private static Cursor makeSongCursor(Context context, String selection, String[] paramArrayOfString, String sortOrder) {
        String selectionStatement = "is_music=1 AND title != ''";

        if (!TextUtils.isEmpty(selection)) {
            selectionStatement = selectionStatement + " AND " + selection;
        }
        return context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{"_id", "title", "artist", "album", "duration", "track", "artist_id", "album_id", MediaStore.Audio.Media.DATA},
                selectionStatement, paramArrayOfString, sortOrder);

    }

    public static Observable<List<Song>> getSongsForCursor(final Cursor cursor) {
        return Observable.create(new ObservableOnSubscribe<List<Song>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Song>> e) throws Exception {
                List<Song> arrayList = new ArrayList<Song>();
                if ((cursor != null) && (cursor.moveToFirst()))
                    do {
                        long id = cursor.getLong(0);
                        String title = cursor.getString(1);
                        String artist = cursor.getString(2);
                        String album = cursor.getString(3);
                        int duration = cursor.getInt(4);
                        int trackNumber = cursor.getInt(5);
                        long artistId = cursor.getInt(6);
                        long albumId = cursor.getLong(7);
                        String path = cursor.getString(8);

                        arrayList.add(new Song(id, albumId, artistId, title, artist, album, duration, trackNumber, path));
                    }
                    while (cursor.moveToNext());
                if (cursor != null) {
                    cursor.close();
                }
                e.onNext(arrayList);
                e.onComplete();
            }


        });
    }

    private Cursor makeAlbumCursor(Context context, String selection, String[] paramArrayOfString) {
        final String albumSortOrder = PreferencesUtility.getInstance(context).getAlbumSortOrder();

        return context.getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, new String[]{"_id", "album", "artist", "artist_id", "numsongs", "minyear"}, selection, paramArrayOfString, albumSortOrder);
    }

    private Observable<List<Album>> getAlbum(final Cursor cursor) {
        return Observable.create(new ObservableOnSubscribe<List<Album>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Album>> e) throws Exception {
                List<Album> albumList = new ArrayList<Album>();
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        Album album = new Album(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getLong(3), cursor.getInt(4), cursor.getInt(5));
                        albumList.add(album);
                    }
                }
                if (cursor != null) {
                    cursor.close();
                }
                e.onNext(albumList);
                e.onComplete();
            }


        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        getAllArtists(this)/*获取歌手信息*/
//                .observeOn(Schedulers.io())
//                .subscribeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<List<Artist>>() {
//                    @Override
//                    public void accept(List<Artist> artists) throws Exception {
//                        for (Artist a :
//                                artists) {
//                         Log.e(TAG,"=====accept==Artist======"+a.name);
//                         Log.e(TAG,"=====accept==Artist======"+a.albumCount);
//                         Log.e(TAG,"=====accept==Artist======"+a.songCount);
//                         Log.e(TAG,"=====accept==Artist======"+a.id);
//                        }
//                    }
//                });


//    getFoldersWithSong(this)/*获取有歌曲的文件夹*/
//            .observeOn(Schedulers.io())
//            .subscribeOn(AndroidSchedulers.mainThread())
//             .subscribe(new Consumer<List<FolderInfo>>() {
//                 @Override
//                 public void accept(List<FolderInfo> values) throws Exception {
//                                     for (int j = 0; j < values.size(); j++) {
//                   FolderInfo value= values.get(j);
//
//                    Log.e(TAG,"====accept=====FolderInfo====="+value.folderName);
//                    Log.e(TAG,"====accept=====FolderInfo====="+value.folderPath);
//                    Log.e(TAG,"====accept=====FolderInfo====="+value.songCount);
//                }
//                 }
//             });

        getSongsForCursor(makeSongCursor(this,null,null))/*获取歌曲列表*/
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Song>>() {


                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<Song> values) {
                for (int j = 0; j < values.size(); j++) {
                   Song value= values.get(j);

                    Log.e(TAG,"====onNext=====Song====="+value.artistName);
                    Log.e(TAG,"====onNext=====Song====="+value.title);
                    Log.e(TAG,"====onNext=====Song====="+value.artistId);
                    Log.e(TAG,"====onNext=====Song====="+value.path);
                    Log.e(TAG,"====onNext=====Song====="+value.duration);
                }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });


//        getAlbum(makeAlbumCursor(this,null,null)).observeOn(Schedulers.io())/*获取专辑*/
//        .subscribeOn(AndroidSchedulers.mainThread())
//        .subscribe(new Observer<List<Album>>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//
//            }
//
//            @Override
//            public void onNext(List<Album> values) {
//
//                for (int j = 0; j < values.size(); j++) {
//                   Album value= values.get(j);
//
//                    Log.e(TAG,"====onNext=====Album====="+value.artistName);
//                    Log.e(TAG,"====onNext=====Album====="+value.title);
//                    Log.e(TAG,"====onNext=====Album====="+value.artistId);
//                    Log.e(TAG,"====onNext=====Album====="+value.year);
//                    Log.e(TAG,"====onNext=====Album====="+value.songCount);
//                }
//
//            }
//
//            @Override
//            public void onError(Throwable e) {
//
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//        });

        ;


//    /*链式编程*/
//        Observable.create(new ObservableOnSubscribe<String>() {
//            @Override
//            public void subscribe(ObservableEmitter<String> e) throws Exception {/*泛型指定传递的数据类型*/
//                /*有动作时通知观察者*/
//                e.onNext("连载1");
//                e.onNext("连载2");
//                e.onNext("连载3");
//
////                e.onComplete();/*表示通知完毕*/
//
//                while (true){
//                    if (i==10)break;
//                    i++;
//                    e.onNext("连载1"+i);
//
//                    SystemClock.sleep(1000);
//                }
//
//            }
//        }).observeOn(AndroidSchedulers.mainThread())/*回调在主线程*/
//                .subscribeOn(Schedulers.io())
//                .distinct(new Function<String, String>() {
//                    @Override
//                    public String apply(String s) throws Exception {
//                        Log.e(TAG,"=distinct=====apply="+s);
//                        return s;
//                    }
//                })
//                .flatMap(new Function<String, ObservableSource<String>>() {
//                    @Override
//                    public ObservableSource<String> apply(String s) throws Exception {
//
//                        Log.e(TAG,"======apply="+s);
//                        return Observable.fromArray(s);
//                    }
//                })
//                .subscribe(new Observer<String>() {
//                    @Override
//                    public void onSubscribe(Disposable/*随意使用的*/ d) {/*建立订阅关系的时候调用*/
////                     d.dispose();/*取消订阅关系*/
//
//                        Log.e(TAG, "===onSubscribe=======");
//                    }
//
//                    @Override
//                    public void onNext(String value) {
//
//                        Log.e(TAG, "===onNext=======" + value);
//                        ((TextView)findViewById(R.id.tv_lll)).setText(value);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        Log.e(TAG, "===onComplete=======");
//                    }
//                });


    }

    @Override
    protected void onRestart() {
        super.onRestart();


        Log.e(TAG, "=======onRestart========");
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.e(TAG, "========onStop===========");
    }

    @Override
    protected void onStart() {
        super.onStart();


        Log.e(TAG, "============onStart==============");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.e(TAG, "=======onDestroy=======");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onApplyThemeResource(Resources.Theme theme, int resid, boolean first) {
        super.onApplyThemeResource(theme, resid, first);


    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();


        Log.e(TAG, "===========onResume==========");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return super.onTouchEvent(event);
    }

    /**/


    private String TAG = getClass().getSimpleName();

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.e(TAG, "=======onSaveInstanceState======");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.e(TAG, "=======onRestoreInstanceState======");


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e(TAG, "==========onKeyDown===================");


        return super.onKeyDown(keyCode, event);
    }


}
