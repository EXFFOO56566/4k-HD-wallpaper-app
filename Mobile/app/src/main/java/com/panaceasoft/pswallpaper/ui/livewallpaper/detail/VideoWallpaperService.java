
package com.panaceasoft.pswallpaper.ui.livewallpaper.detail;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.service.wallpaper.WallpaperService;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.panaceasoft.pswallpaper.utils.Utils;

import java.io.File;


public class VideoWallpaperService extends WallpaperService {

    public String videoUri = "";

    @Override
    public Engine onCreateEngine() {

//        try {
//            Movie movie = Movie.decodeStream(getResources().getAssets().open("http://www.panacea-soft.com/uploads/breloom.mp4"));
//            return new VideoWallpaperEngine(movie);
//        } catch (Exception e) {
//            return null;
//        }

        return new VideoWallpaperEngine();
    }

//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        if (intent != null && intent.getExtras() != null){
//            videoUri = intent.getStringExtra(Constants.VIDEO_URI);
//        }
//        return flags;
//    }

    private enum PlayerState {
        NONE, PREPARING, READY, PLAYING
    }

    class VideoWallpaperEngine extends WallpaperService.Engine {
        private MediaPlayer mp = null;
        private PlayerState playerState = PlayerState.NONE;
        private int width = 0;
        private int height = 0;
        private MySurfaceHolder mySurfaceHolder = null;


        VideoWallpaperEngine() {
            onStartCommand(new Intent(), 0,1);
            Utils.psLog("Init Engine");
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            Utils.psLog("on Surface Created.");

            super.onSurfaceCreated(holder);

            mp = new MediaPlayer();

            mySurfaceHolder = new MySurfaceHolder(holder);
//            mySurfaceHolder.setFixedSize(50, 200);
            mp.setDisplay(mySurfaceHolder);

            mp.setLooping(true);
            mp.setVolume(0.0f, 0.0f);

            mp.setOnPreparedListener(mp -> {
                playerState = PlayerState.READY;
                setPlay(true);
            });

            try {

                File path = Environment.getExternalStorageDirectory();
                videoUri = path + "/0_Live_Wallpapers/temp.mp4";

                mp.setDataSource(VideoWallpaperService.this, Uri.parse(videoUri));
//                mp!!.setDataSource(this@MovieLiveWallpaperService, Uri.parse("android.resource://" + packageName + "/" + R.raw.small))
            } catch (Exception e) {
                Utils.psErrorLog("", e);
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            super.onSurfaceChanged(holder, format, this.width, this.height);
            this.width = width;
            this.height = height;
            //setBufferGeometry(holder.getSurface(), this.width, this.height);
//            mySurfaceHolder.setFixedSize(this.width, this.height);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            if (mp == null) {
                return;
            }
            mp.stop();
            mp.release();
            playerState = PlayerState.NONE;
        }

        private void setPlay(boolean play) {
            if (mp == null)
                return;
            if (play == mp.isPlaying()) {
                return;
            }

            if(!play) {
                mp.pause();
                playerState = PlayerState.READY;

            }

            if(mp.isPlaying()) {
                return;
            }

            if(playerState == PlayerState.READY) {
                Utils.psLog( "ready, so starting to play");
                mp.start();
                playerState = PlayerState.PLAYING;
            }

            if(playerState == PlayerState.NONE) {
                Utils.psLog("not ready, so preparing");
                try {
                    mp.prepareAsync();
                }catch (Exception e){
                    Utils.psErrorLog("not ready, so preparing",e);
                }
                playerState = PlayerState.PREPARING;
            }
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            Utils.psLog( "onVisibilityChanged:$visible $playerState");
            if (mp == null)
                return;
            setPlay(visible);
        }
    }


    class MySurfaceHolder implements SurfaceHolder {

        private SurfaceHolder surfaceHolder;

        MySurfaceHolder(SurfaceHolder surfaceHolder) {
            this.surfaceHolder = surfaceHolder;
        }

        @Override
        public void addCallback(Callback callback) {
            surfaceHolder.addCallback(callback);
        }

        @Override
        public void removeCallback(Callback callback) {
            surfaceHolder.removeCallback(callback);
        }

        @Override
        public boolean isCreating() {
            return surfaceHolder.isCreating();
        }

        @Override
        public void setType(int type) {
            surfaceHolder.setType(type);
        }

        @Override
        public void setFixedSize(int width, int height) {
            surfaceHolder.setFixedSize(width, height);
        }

        @Override
        public void setSizeFromLayout() {
            surfaceHolder.setSizeFromLayout();

        }

        @Override
        public void setFormat(int format) {
            surfaceHolder.setFormat(format);
        }

        @Override
        public void setKeepScreenOn(boolean screenOn) {

        }

        @Override
        public Canvas lockCanvas() {
            return surfaceHolder.lockCanvas();
        }

        @Override
        public Canvas lockCanvas(Rect dirty) {
            return surfaceHolder.lockCanvas(dirty);
        }

        @Override
        public void unlockCanvasAndPost(Canvas canvas) {
            surfaceHolder.unlockCanvasAndPost(canvas);
        }

        @Override
        public Rect getSurfaceFrame() {
            return surfaceHolder.getSurfaceFrame();
        }

        @Override
        public Surface getSurface() {
            return surfaceHolder.getSurface();
        }

    }

}



