package com.tian.videomergedemo.view;


/**
 * timer for progress update
 */

public class RecordProgressTimer implements RecordProgressController.RecordingStateChanged {
    private static final String TAG = "RecordProgressTimer";
    private final ProgressTimerRunnable mTask;
    private final Thread mTaskThread;

    private boolean mIsRecording;
    private long mStartRecordingTime;
    private ProgressUpdateListener mProgressUpdateListener;

    public RecordProgressTimer() {
        mTask = new ProgressTimerRunnable();
        mTaskThread = new Thread(mTask);
    }

    public void stop() {
        mTaskThread.interrupt();
        mTask.run = false;
    }

    public void start() {
        mTaskThread.start();
    }

    public void setProgressUpdateListener(ProgressUpdateListener listener) {
        mProgressUpdateListener = listener;
    }

    @Override
    public void recordingStart(long startTime) {
        mIsRecording = true;
        mStartRecordingTime = startTime;
    }

    @Override
    public void recordingStop() {
        mIsRecording = false;
    }

    public class ProgressTimerRunnable implements Runnable {
        private volatile boolean run;
        private long interval;

        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_DISPLAY);
            while (run) {
                if (RecordProgressTimer.this.mIsRecording) {
                    interval = System.currentTimeMillis() - RecordProgressTimer.this.mStartRecordingTime;
                    if (interval > 0) {
                        mProgressUpdateListener.updateProgress(interval);
                    }
                }
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public ProgressTimerRunnable() {
            this.run = true;
            interval = -1;
        }
    }

    public interface ProgressUpdateListener {
        void updateProgress(long interval);
    }
}
