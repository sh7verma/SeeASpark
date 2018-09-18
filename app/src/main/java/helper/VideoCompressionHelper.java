package helper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

import com.netcompss.ffmpeg4android.CommandValidationException;
import com.netcompss.ffmpeg4android.GeneralUtils;
import com.netcompss.ffmpeg4android.Prefs;
import com.netcompss.ffmpeg4android.ProgressCalculator;
import com.netcompss.loader.LoadJNI;
import com.seeaspark.AttachVideoActivity;
import com.seeaspark.R;

public class VideoCompressionHelper {

	static VideoCompressionHelper mInstance;
	Context mContext;

	public static VideoCompressionHelper initCompressor() {
		if (mInstance == null) {
			mInstance = new VideoCompressionHelper();
		}
		return mInstance;
	}

	public ProgressDialog progressBar;

	String workFolder = null;
	String vkLogPath = null;
	LoadJNI vk;
	private final int STOP_TRANSCODING_MSG = -1;
	private final int FINISHED_TRANSCODING_MSG = 0;
	private boolean commandValidationFailedFlag = false;
	String mSourcePath = "", mDestinationPath = "";

	public void startCompression(Context con, String source, String dest) {
		mContext = con;

		mSourcePath = source;
		mDestinationPath = dest;

		workFolder = mContext.getFilesDir() + "/";
		Log.i(Prefs.TAG, "workFolder (license and logs location) path: "
				+ workFolder);
		vkLogPath = workFolder + "vk.log";
		Log.i(Prefs.TAG, "vk log (native log) path: " + vkLogPath);
		GeneralUtils.copyLicenseFromAssetsToSDIfNeeded((Activity) mContext,
				workFolder);
		int rc = GeneralUtils.isLicenseValid(mContext, workFolder);
		Log.i(Prefs.TAG, "License check RC: " + rc);

		runTranscoding();
	}

	private void runTranscodingUsingLoader() {
		Log.i(Prefs.TAG, "runTranscodingUsingLoader started...");

		PowerManager powerManager = (PowerManager) mContext
				.getSystemService(Activity.POWER_SERVICE);
		WakeLock wakeLock = powerManager.newWakeLock(
				PowerManager.PARTIAL_WAKE_LOCK, "VK_LOCK");
		Log.d(Prefs.TAG, "Acquire wake lock");
		wakeLock.acquire();

		String[] complexCommand = { "ffmpeg", "-y", "-i", mSourcePath,
				"-strict", "experimental", "-s", "720x480", "-r", "25",
				"-vcodec", "mpeg4", "-b", "1000k", "-ab", "48000", "-ac", "2",
				"-ar", "22050", mDestinationPath };

//		String[] complexCommand = { "ffmpeg", "-y", "-i", mSourcePath,
//				"-strict", "experimental", "-s", "720x480", "-r", "30",
//				"-vcodec", "mpeg4", "-b", "1000k", "-ab", "48000", "-ac", "2",
//				"-ar", "48000", mDestinationPath };

//		"ffmpeg -y -i mSourcePath -strict experimental -r 30 -ab 48000 -ac 2 -ar 48000 -vcodec mpeg4 -b 2097152 mDestinationPath"

		vk = new LoadJNI();
		try {
			// running complex command with validation
			vk.run(complexCommand, workFolder, mContext);

			Log.i(Prefs.TAG, "vk.run finished.");
			// GeneralUtils.copyFileToFolder(vkLogPath, demoVideoFolder);

		} catch (CommandValidationException e) {
			Log.e(Prefs.TAG, "vk run exeption.", e);
			commandValidationFailedFlag = true;

		} catch (Throwable e) {
			Log.e(Prefs.TAG, "vk run exeption.", e);
		} finally {
			if (wakeLock.isHeld()) {
				wakeLock.release();
				Log.i(Prefs.TAG, "Wake lock released");
			} else {
				Log.i(Prefs.TAG, "Wake lock is already released, doing nothing");
			}
		}

		// finished Toast
		String rc = null;
		if (commandValidationFailedFlag) {
			rc = "Command Vaidation Failed";
		} else {
			rc = GeneralUtils.getReturnCodeFromLog(vkLogPath);
		}
		final String status = rc;
		((Activity) mContext).runOnUiThread(new Runnable() {
			public void run() {
				if (status.contains("Finished OK")) {
					((AttachVideoActivity) mContext).afterCompressionSuccesful(mDestinationPath);
				}
			}
		});
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Log.i(Prefs.TAG, "Handler got message");
			try {
				if (progressBar != null) {
					progressBar.dismiss();

					// stopping the transcoding native
					if (msg.what == STOP_TRANSCODING_MSG) {
						Log.i(Prefs.TAG, "Got cancel message, calling fexit");
						vk.fExit(mContext);

					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	public void runTranscoding() {
		progressBar = new ProgressDialog(mContext);
		progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressBar.setTitle(mContext.getResources().getString(R.string.compressing_video));
		progressBar.setMessage(mContext.getResources().getString(R.string.press_cancel));
		progressBar.setMax(100);
		progressBar.setProgress(0);

		progressBar.setCancelable(false);
		progressBar.setButton(DialogInterface.BUTTON_NEGATIVE, mContext.getResources().getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						handler.sendEmptyMessage(STOP_TRANSCODING_MSG);
					}
				});

		progressBar.show();

		new Thread() {
			public void run() {
				Log.d(Prefs.TAG, "Worker started");
				try {
					// sleep(5000);
					runTranscodingUsingLoader();
					handler.sendEmptyMessage(FINISHED_TRANSCODING_MSG);

				} catch (Exception e) {
					Log.e("threadmessage", e.getMessage());
				}
			}
		}.start();

		// Progress update thread
		new Thread() {
			ProgressCalculator pc = new ProgressCalculator(vkLogPath);

			public void run() {
				Log.d(Prefs.TAG, "Progress update started");
				int progress = -1;
				try {
					while (true) {
						sleep(300);
						progress = pc.calcProgress();
						if (progress != 0 && progress < 100) {
							progressBar.setProgress(progress);
						} else if (progress == 100) {
							Log.i(Prefs.TAG,
									"==== progress is 100, exiting Progress update thread");
							pc.initCalcParamsForNextInter();
							break;
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
}
