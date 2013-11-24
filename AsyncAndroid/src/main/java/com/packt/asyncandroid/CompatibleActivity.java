package com.packt.asyncandroid;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * Adds support for isChangingConfigurations when minSdkVersion
 * targets api-levels below 11.
 *
 * Unfortunately onSaveInstanceState is invoked AFTER onPause,
 * so on platforms below 11 isChangingConfigurations will only
 * report correctly in onStop() or onDestroy().
 */
public class CompatibleActivity extends FragmentActivity {

    private boolean isConfigChange;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        isConfigChange = true;
    }

    @Override
    public boolean isChangingConfigurations() {
        if (android.os.Build.VERSION.SDK_INT >= 11)
            return super.isChangingConfigurations();
        else
            return isConfigChange;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
