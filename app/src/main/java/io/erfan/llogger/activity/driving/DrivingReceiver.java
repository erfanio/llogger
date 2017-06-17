package io.erfan.llogger.activity.driving;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

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
