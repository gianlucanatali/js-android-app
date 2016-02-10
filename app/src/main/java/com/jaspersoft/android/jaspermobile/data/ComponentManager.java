package com.jaspersoft.android.jaspermobile.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.jaspersoft.android.jaspermobile.GraphObject;
import com.jaspersoft.android.jaspermobile.data.cache.profile.ActiveProfileCache;
import com.jaspersoft.android.jaspermobile.data.cache.profile.PreferencesActiveProfileCache;
import com.jaspersoft.android.jaspermobile.domain.Profile;

/**
 * @author Tom Koptel
 * @since 2.3
 */
public interface ComponentManager {
    void setupProfileComponent(@Nullable Callback callback);

    void setupActiveProfile(Profile profile);

    class Factory {
        public static ComponentManager from(@NonNull Context context) {
            ActiveProfileCache activeProfileCache = new PreferencesActiveProfileCache(context);
            GraphObject graphObject = GraphObject.Factory.from(context);

            return new ComponentManagerImpl(activeProfileCache, graphObject);
        }
    }

    interface Callback {
        Callback EMPTY = new Callback() {
            @Override
            public void onActiveProfileMissing() {
            }

            @Override
            public void onSetupComplete() {
            }
        };
        void onActiveProfileMissing();

        void onSetupComplete();
    }
}
