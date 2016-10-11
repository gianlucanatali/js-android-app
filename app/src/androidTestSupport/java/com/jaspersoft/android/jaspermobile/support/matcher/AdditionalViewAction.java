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

package com.jaspersoft.android.jaspermobile.support.matcher;

import android.graphics.Bitmap;
import android.support.test.espresso.PerformException;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.GeneralSwipeAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Swipe;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.espresso.util.HumanReadables;
import android.support.test.espresso.web.webdriver.Locator;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.widget.NumberPicker;

import com.jaspersoft.android.jaspermobile.support.BitmapWrapper;

import junit.framework.AssertionFailedError;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.Date;
import java.util.concurrent.TimeoutException;

import static android.support.test.espresso.web.assertion.WebViewAssertions.webMatches;
import static android.support.test.espresso.web.sugar.Web.onWebView;
import static android.support.test.espresso.web.webdriver.DriverAtoms.findElement;
import static android.support.test.espresso.web.webdriver.DriverAtoms.getText;

/**
 * @author Andrew Tivodar
 * @since 2.5
 */
public class AdditionalViewAction {

    public static ViewAction scrollToPage(final int page) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return new BoundedMatcher<View, NumberPicker>(NumberPicker.class) {
                    @Override
                    public void describeTo(Description description) {

                    }

                    @Override
                    protected boolean matchesSafely(NumberPicker item) {
                        return true;
                    }
                };
            }

            @Override
            public String getDescription() {
                return "scroll number picker to " + page + "page";
            }

            @Override
            public void perform(UiController uiController, View view) {
                ((NumberPicker) view).setValue(page);
            }
        };
    }

    public static ViewAction swipeFromLeftEdge() {
        return new GeneralSwipeAction(Swipe.FAST, GeneralLocation.CENTER_LEFT,
                GeneralLocation.CENTER_RIGHT, Press.FINGER);
    }

    public static ViewAction swipeFromRightEdge() {
        return new GeneralSwipeAction(Swipe.FAST, GeneralLocation.CENTER_RIGHT,
                GeneralLocation.CENTER_LEFT, Press.FINGER);
    }

    public static ViewAction getImage(final BitmapWrapper bitmapWrapper) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return new BoundedMatcher<View, View>(View.class) {
                    @Override
                    public void describeTo(Description description) {

                    }

                    @Override
                    protected boolean matchesSafely(View item) {
                        return true;
                    }
                };
            }

            @Override
            public String getDescription() {
                return null;
            }

            @Override
            public void perform(UiController uiController, View view) {
                view.setDrawingCacheEnabled(true);
                view.buildDrawingCache();
                bitmapWrapper.setBitmap(Bitmap.createBitmap(view.getDrawingCache()));
                view.destroyDrawingCache();
            }
        };
    }

    public static ViewAction zoomIn() {
        return new ViewAction() {

            @Override
            public Matcher<View> getConstraints() {
                return new BoundedMatcher<View, WebView>(WebView.class) {

                    @Override
                    public void describeTo(Description description) {

                    }

                    @Override
                    protected boolean matchesSafely(WebView item) {
                        return true;
                    }
                };
            }

            @Override
            public String getDescription() {
                return "zoom in webView.";
            }

            @Override
            public void perform(UiController uiController, View view) {
                ((WebView) view).zoomIn();
                view.scrollTo(0, 0);
                uiController.loopMainThreadForAtLeast(500);
            }
        };
    }

    public static ViewAction zoomOut() {
        return new ViewAction() {

            @Override
            public Matcher<View> getConstraints() {
                return new BoundedMatcher<View, WebView>(WebView.class) {

                    @Override
                    public void describeTo(Description description) {

                    }

                    @Override
                    protected boolean matchesSafely(WebView item) {
                        return true;
                    }
                };
            }

            @Override
            public String getDescription() {
                return "zoom out webView.";
            }

            @Override
            public void perform(UiController uiController, View view) {
                while (((WebView) view).zoomOut()) ;
                view.scrollTo(0, 0);
                uiController.loopMainThreadForAtLeast(500);
            }
        };
    }

    public static ViewAction openOverflowMenu() {
        return new OpenOverflowViewAction();
    }

    public static ViewAction watch(Matcher<? super View> viewMatcher, long millis) {
        return new WaitViewAction(millis, viewMatcher);
    }

    public static void waitForTextInDashboard(Matcher<String> keyMatcher, long delay) {
        long currentTime = new Date().getTime();
        long endTime = currentTime + delay;

        do {
            try {
                onWebView()
                        .withElement(findElement(Locator.ID, "container"))
                        .check(webMatches(getText(), keyMatcher));
                return;
            } catch (AssertionFailedError error) {
                try {
                    currentTime = new Date().getTime();
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                }
            }
        } while (endTime > currentTime);
        throw new PerformException.Builder()
                .withActionDescription("Wait for a specific view matcher during " + delay + " millis.")
                .withViewDescription("webView")
                .withCause(new TimeoutException())
                .build();
    }

    private static class WaitViewAction implements ViewAction {
        private long mMillis;
        protected Matcher<? super View> mViewMather;

        public WaitViewAction(long millis, Matcher<? super View> view) {
            mMillis = millis;
            mViewMather = view;
        }

        @Override
        public Matcher<View> getConstraints() {
            return new BaseMatcher<View>() {
                @Override
                public boolean matches(Object item) {
                    return true;
                }

                @Override
                public void describeTo(Description description) {

                }
            };
        }

        @Override
        public String getDescription() {
            return "Wait for a specific view matcher during " + mMillis + " millis.";
        }

        @Override
        public void perform(UiController uiController, View view) {
            uiController.loopMainThreadUntilIdle();

            long startTime = System.currentTimeMillis();
            long endTime = startTime + mMillis;

            while (System.currentTimeMillis() < endTime) {
                if (mViewMather.matches(view)) return;
                uiController.loopMainThreadForAtLeast(50);
            }

            throw new PerformException.Builder()
                    .withActionDescription(this.getDescription())
                    .withViewDescription(HumanReadables.describe(view))
                    .withCause(new TimeoutException())
                    .build();
        }
    }

    private static class OpenOverflowViewAction implements ViewAction {

        @Override
        public Matcher<View> getConstraints() {
            return new BaseMatcher<View>() {
                @Override
                public boolean matches(Object item) {
                    return item instanceof Toolbar;
                }

                @Override
                public void describeTo(Description description) {
                    description.appendText("with toolbar: ");
                }
            };
        }

        @Override
        public String getDescription() {
            return "Open toolbar overflow menu if it is available.";
        }

        @Override
        public void perform(UiController uiController, View view) {
            Toolbar toolbar = (Toolbar) view;

            if (toolbar.canShowOverflowMenu() && !toolbar.isOverflowMenuShowing()) {
                toolbar.showOverflowMenu();
            }
        }
    }
}
