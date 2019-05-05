package com.dev.nanicki.artguide.fragment;

import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.dev.nanicki.artguide.ApplicationActivity;
import com.dev.nanicki.artguide.util.PermissionUtil;

public class PermissionFragment extends Fragment {
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionUtil.MAP_PERM_REQUEST_CODE) {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        onPermissionFailed();
                        return;
                    }
                }
                onPermissionGrunted();
            } else {
                onPermissionFailed();
            }
        }
    }

    private void onPermissionGrunted() {
        ApplicationActivity activity = (ApplicationActivity) getActivity();
        activity.startMapScreen(null);
    }

    private void onPermissionFailed() {

    }
}
