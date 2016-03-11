package imagetools;

/**
 * Created by Noah on 2015-05-16.
 */
public class Timer {

    private long playTime; //last time played
    private long pauseTime; //last time paused
    private long duration;

    //if playing is false time
    boolean playing;

    /**
     * creates a stopped clock
     * @param duration the time the timer passes before it passes
     */
    public Timer(float duration){
        this.duration = (long) (duration*1000000000);
        reset();
    }

    /**
     * @param length the time the timer passes before it passes
     * @param play if it should play directly or not
     */
    public Timer(float length, boolean play){
        this.duration = (long) (length*1000000000);

        if(play)
            restart();
        else
            reset();

    }

    /**
     * Zeroes the clock and plays
     * @return if it was playing
     */
    public boolean restart(){
        playTime = System.nanoTime();
        pauseTime = playTime;
        if(playing){
            playing = true;
            return true;
        } else {
            playing = true;
            return false;
        }
    }
    /**
     * Zeroes the clock and pauses
     * @return if it was playing
     */
    public boolean reset(){
        playTime = System.nanoTime();
        pauseTime = playTime;
        if(playing){
            playing = false;
            return true;
        } else {
            playing = false;
            return false;
        }
    }

    /**
     * Sets start time exactly when it passed for avoiding loss of time.
     * Works when paused.
     * PS don't call anything else to start after this. It fucks it up.
     * @return it is playing
     */
    public boolean redo(){
        playTime += duration;
        pauseTime = playTime;
        return playing;
    }

    /**
     * if paused
     * Starts the clock so that the time left will be equal to as when it was paused.
     * else
     * dont do anything
     * @return if it was playing
     */
    public boolean play() {
        if (playing)
            return true;
        else {
            playTime = System.nanoTime() + playTime - pauseTime;
            playing = true;
            return false;
        }
    }
    /**
     * if playing
     * pauses the clock so when it plays again the time left will be equal to as when it was paused.
     * else
     * dont do anything
     * @return if it was playing
     */
    public boolean pause(){
        if(playing) {
            pauseTime = System.nanoTime();
            playing = false;
            return true;
        }
        else
            return false;
    }

    /**
     * if playing
     * pauses the clock so when it plays again the time left will be equal to as when it was paused.
     * else
     * Starts the clock so that the time left will be equal to as when it was paused.
     * @return if it was playing
     */
    public boolean tap(){
        if(playing) {
            pauseTime = System.nanoTime();
            playing = false;
            return true;
        }
        else {
            playTime = System.nanoTime() + playTime - pauseTime;
            playing = true;
            return false;
        }
    }

    /**
     * forces the pause time to be where it is now
     * @return if it was playing
     */
    public boolean stop(){
        pauseTime = System.nanoTime();
        if(playing) {
            playing = false;
            return true;
        }
        else
            return false;
    }

    /**
     * @return if it was playing
     */
    public boolean isPlaying(){
        return playing;
    }

    /**
     * @return if clock has passed its duration (works after pausing)
     */
    public boolean hasPassed(){
        if(playing)
            return System.nanoTime() >= playTime + duration;
        else // return if we paused after it completed
            return pauseTime - playTime >= duration;
    }

    public boolean hasPassed(float time){
        if(playing)
            return System.nanoTime() >= playTime + time*1000000000;
        else // return if we paused after it completed
            return pauseTime - playTime >= time*1000000000;
    }

    /**
     * @param duration the duration before the timer passes in seconds
     */
    public void setDuration(float duration){
        this.duration = (long) (duration*1000000000);
    }

    /**
     * @returnthe duration before the timer passes in seconds
     */
    public long getDuration() {
        return duration;
    }
}
