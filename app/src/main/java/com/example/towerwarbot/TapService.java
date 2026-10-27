
package com.example.towerwarbot;

import android.os.Binder;
import android.os.Parcel;
import android.os.RemoteException;

public class TapService extends Binder {

    public static final int TAP = 1;

    public TapService() {
    }

    @Override
    protected boolean onTransact(
            int code,
            Parcel data,
            Parcel reply,
            int flags) throws RemoteException {

        if (code == TAP) {
            int x = data.readInt();
            int y = data.readInt();

            try {
                Process process = new ProcessBuilder(
                    "input", "tap",
                    String.valueOf(x),
                    String.valueOf(y)
                ).start();

                int result = process.waitFor();
                reply.writeNoException();
                reply.writeInt(result);

            } catch (Exception e) {
                reply.writeException(
                    new IllegalStateException(e.getMessage())
                );
            }
            return true;
        }

        return super.onTransact(
            code, data, reply, flags
        );
    }

    public void destroy() {
    }
}
