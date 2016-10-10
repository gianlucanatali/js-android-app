/*
 * Copyright © 2015 TIBCO Software, Inc. All rights reserved.
 * http://community.jaspersoft.com/project/jaspermobile-android
 *
 * Unless you have purchased a commercial license agreement from TIBCO Jaspersoft,
 * the following license terms apply:
 *
 * This program is part of TIBCO Jaspersoft Mobile for Android.
 *
 * TIBCO Jaspersoft Mobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TIBCO Jaspersoft Mobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with TIBCO Jaspersoft Mobile for Android. If not, see
 * <http://www.gnu.org/licenses/lgpl>.
 */

package com.jaspersoft.android.jaspermobile.support.rule;

import android.os.IBinder;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author Andrew Tivodar
 * @since 2.6
 */
public class DisableAnimationsRule implements TestRule {
    private Method setAnimationScalesMethod;
    private Method getAnimationScalesMethod;
    private Object windowManagerObject;

    public DisableAnimationsRule() {
        try {
            init();
        } catch (Exception e) {
            throw new RuntimeException("Failed to access animation methods", e);
        }
    }

    private void init() throws Exception {
        Class<?> windowManagerStubClazz = Class.forName("android.view.IWindowManager$Stub");
        Method asInterface = windowManagerStubClazz.getDeclaredMethod("asInterface", IBinder.class);
        Class<?> serviceManagerClazz = Class.forName("android.os.ServiceManager");
        Method getService = serviceManagerClazz.getDeclaredMethod("getService", String.class);
        Class<?> windowManagerClazz = Class.forName("android.view.IWindowManager");
        setAnimationScalesMethod = windowManagerClazz.getDeclaredMethod("setAnimationScales", float[].class);
        getAnimationScalesMethod = windowManagerClazz.getDeclaredMethod("getAnimationScales");
        IBinder windowManagerBinder = (IBinder) getService.invoke(null, "window");
        windowManagerObject = asInterface.invoke(null, windowManagerBinder);
    }

    @Override
    public Statement apply(final Statement statement, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try {
                    setAnimationScaleFactors(0.0f);
                } finally {
                    statement.evaluate();
                }
            }
        };
    }

    private void setAnimationScaleFactors(float scaleFactor) throws Exception {
        float[] scaleFactors = (float[]) getAnimationScalesMethod.invoke(windowManagerObject);
        Arrays.fill(scaleFactors, scaleFactor);
        setAnimationScalesMethod.invoke(windowManagerObject, scaleFactors);
    }
}
