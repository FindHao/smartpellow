package small.find;

import java.io.IOException;

import android.media.MediaPlayer;

public class Alarm {
	public MediaPlayer mMediaPlayer=null;
	public Alarm() {
		mMediaPlayer=new MediaPlayer();
	}
	void play(){
		try {
			mMediaPlayer.reset();
			mMediaPlayer.setDataSource("a.mp3");
			mMediaPlayer.prepare();
			mMediaPlayer.start();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	void stop(){
		if(mMediaPlayer!=null){
			mMediaPlayer.stop();
			mMediaPlayer.release();
		}
	}
}
