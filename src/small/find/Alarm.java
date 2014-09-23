package small.find;

import java.io.IOException;

import android.content.Context;
import android.media.MediaPlayer;

public class Alarm {
	Context parent;
	public MediaPlayer mMediaPlayer=null;
	public Alarm(Context parent) {
		mMediaPlayer=new MediaPlayer();
		this.parent=parent;
	}
	void play(){
		try {
			mMediaPlayer=MediaPlayer.create(parent, R.raw.qinghuaci);
			if (mMediaPlayer != null) { 
		        mMediaPlayer.stop();
		       }
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
