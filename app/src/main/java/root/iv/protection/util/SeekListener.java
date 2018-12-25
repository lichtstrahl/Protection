package root.iv.protection.util;

import android.widget.SeekBar;

import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import root.iv.protection.App;

public class SeekListener implements SeekBar.OnSeekBarChangeListener, Signed<Action1<Integer>> {
    private PublishSubject<Integer> subject;
    private Disposable disposable;

    public SeekListener(SeekBar bar) {
        bar.setOnSeekBarChangeListener(this);
        subject = PublishSubject.create();
    }

    @Override
    public void subscribe(Action1<Integer> action) {
        disposable = subject.subscribe(
                action::run,
                e -> App.logE(e.getMessage())
                );
    }

    @Override
    public void unsubscribe() {
        disposable.dispose();
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        subject.onNext(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // Не используется
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // Не используется
    }
}
