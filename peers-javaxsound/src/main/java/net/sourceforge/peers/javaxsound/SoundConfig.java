package net.sourceforge.peers.javaxsound;

import javax.sound.sampled.AudioFormat;

public interface SoundConfig {

    AudioFormat.Encoding getEncoding();

    float getSampleRate();

    int getSampleSizeInBits();

    int getChannels();

    int getFrameSize();

    float getFrameRate();

    boolean getBigEndian();

}
