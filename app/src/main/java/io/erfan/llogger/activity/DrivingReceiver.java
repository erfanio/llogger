package io.erfan.llogger.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class DrivingReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        DrivingService service = ((DrivingService.DrivingBinder) peekService(context, new Intent(context, DrivingService.class))).getService();
        switch (intent.getAction()) {
            case DrivingService.PAUSE:
                service.pause();
                break;
            case DrivingService.RESUME:
                service.resume();
                break;
        }
    }
}
