package edu.utep.cs5381.platformer;

import android.content.Context;
import android.media.SoundPool;

public class SoundManager {
    public enum Sound {
        COIN(R.raw.coin_pickup),
        EXPLODE(R.raw.explode),
        EXTRA_LIFE(R.raw.extra_life),
        GUN_UPGRADE(R.raw.gun_upgrade),
        HIT_GUARD(R.raw.hit_guard),
        JUMP(R.raw.jump),
        PLAYER_BURN(R.raw.player_burn),
        RICOCHET(R.raw.ricochet),
        SHOOT(R.raw.shoot),
        TELEPORT(R.raw.teleport);

        public final int resourceId;
        private int soundId;

        Sound(int resourceId) {
            this.resourceId = resourceId;
        }
    }
    private SoundPool soundPool;

    public SoundManager(Context context) {
        soundPool = new SoundPool.Builder().setMaxStreams(Sound.values().length).build();
        for (Sound sound: Sound.values())
            sound.soundId = soundPool.load(context, sound.resourceId, 1);
    }

    public void play(Sound sound) {
        soundPool.play(sound.soundId, 1, 1, 0, 0, 1);
    }
}
