/**
 * ---------------------------------------------------------------------------
 * Massey University - 159.261 Games Programming
 * Assignment 2
 * ---------------------------------------------------------------------------
 * * [Dreamy Forest]
 * * Team Members:
 * - LIU ZIMO (ID:24009362)
 * - MIAO CHONG (ID: 24008986)
 * - SUN MINGYI (ID: 24009239)
 * - ZHOU XUAN (ID: 24009035)
 * ---------------------------------------------------------------------------
 **/
package core;
// 显式导入，确保可以正确引用到 GameEngine 内部的 AudioClip
import core.GameEngine.AudioClip;

public class AudioManager {
    private final GameInstance game;

    private AudioClip bgmGamePlaying;
    private AudioClip bgmMenu;
    private AudioClip bgmGameOver;
    private AudioClip bgmGameWin;
    private AudioClip currentMusic = null;
    private AudioClip sfxUseSkill;
    private AudioClip sfxItemPick;
    private AudioClip sfxItemPick_ADD;
    private AudioClip sfxItemPick_Poison;
    private AudioClip sfxHitEnemy;

    public AudioManager(GameInstance game) {
        this.game = game;
        initMusic();
    }

    private void initMusic() {
        bgmMenu = game.loadAudio("resource/music/bgm_menu.wav");
        bgmGameOver = game.loadAudio("resource/music/sfx_game_over.wav");
        bgmGameWin = game.loadAudio("resource/music/sfx_game_win.wav");
        bgmGamePlaying = game.loadAudio("resource/music/bgm_gameplay.wav");
        sfxUseSkill = game.loadAudio("resource/sfx/magic.wav");
        sfxItemPick = game.loadAudio("resource/sfx/sfx_Itempick.wav");
        sfxHitEnemy  = game.loadAudio("resource/sfx/sfx_hit.wav");
        sfxItemPick_ADD = game.loadAudio("resource/sfx/sfx_pointadd.wav");
        sfxItemPick_Poison = game.loadAudio("resource/sfx/sfx_Poison.wav");
    }

    public void playMenuBGM() { switchBGM(bgmMenu, -3f); }
    public void playGameplayBGM() { switchBGM(bgmGamePlaying, -13f); }
    public void playGameOverBGM() { switchBGM(bgmGameOver, -10f); }
    public void playGameWinBGM() { switchBGM(bgmGameWin, -10f); }
    public void playSkillSFX() {
        if (sfxUseSkill != null) {
            game.playAudio(sfxUseSkill, -10f);
        }
    }
    public void playItemPickSFX(){
        if (sfxItemPick != null) {
            game.playAudio(sfxItemPick, -10f);
        }
    }
    public void playItemPickPoison(){
        if (sfxItemPick_Poison != null) {
            game.playAudio(sfxItemPick_Poison, -10f);
        }
    }
    public void playItemPickAddSFX(){
        if (sfxItemPick_ADD != null) {
            game.playAudio(sfxItemPick_ADD, -10f);
        }
    }
    public void playHitEnemySFX(){
        if(sfxHitEnemy != null){
            game.playAudio(sfxHitEnemy, -10f);
        }
    }

    private void switchBGM(AudioClip newBGM, float volume) {
        // 如果新音频和当前正在播放的完全一致，则不需要重新处理，防止重叠
        if (currentMusic == newBGM && currentMusic != null) {
            return;
        }

        stopCurrentBGM();

        if (newBGM != null) {
            game.startAudioLoop(newBGM, volume);
        }
        currentMusic = newBGM;
    }

    public void stopCurrentBGM() {
        if (currentMusic != null) {
            game.stopAudioLoop(currentMusic);
            currentMusic = null; // 彻底清空标记
        }
    }
}