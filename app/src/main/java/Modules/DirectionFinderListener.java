package Modules;

import java.io.UnsupportedEncodingException;

public interface DirectionFinderListener {
    void execute(boolean drawable, boolean doAnalysis) throws UnsupportedEncodingException;
    void onDirectionFinderStart(String noti);
    void onDirectionFinderSuccess();
    int getDistance();
    int getDuration();
    String getDistanceText();
    String getDurationText();
}
