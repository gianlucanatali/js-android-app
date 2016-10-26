/*
 * Copyright � 2015 TIBCO Software, Inc. All rights reserved.
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

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.Root;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.espresso.util.TreeIterables;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaspersoft.android.jaspermobile.ui.view.activity.AuthenticatorActivity;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.Matchers.is;

/**
 * @author Andrew Tivodar
 * @since 2.5
 */
public class AdditionalViewAssertion {
    public static ViewAssertion exist() {
        return new ViewAssertion() {
            @Override
            public void check(View view, NoMatchingViewException noView) {
                if (view == null) {
                    assertThat("View is not present in the hierarchy: ", false,
                            is(true));
                }
            }
        };
    }

    public static Matcher<View> hasView(Matcher<? super View> viewMatcher) {
        return new HasViewMatcher(viewMatcher);
    }

    public static Matcher<View> hasItems() {
        return new HasItemMatcher();
    }

    public static Matcher<View> hasError() {
        return new HasErrorMatcher();
    }

    public static Matcher<View> isVisible() {
        return new VisibilityMatcher();
    }

    public static Matcher<View> isInAuthActivity() {
        return new BoundedMatcher<View, View>(View.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("is in finishing activity");
            }

            @Override
            protected boolean matchesSafely(View item) {
                Context context = item.getContext();
                return context instanceof AuthenticatorActivity;
            }
        };
    }

    public static Matcher<View> hasText(final String text) {
        return hasDescendant(withText(startsWith(text)));
    }

    public static Matcher<View> hasImage(final int imageResource) {
        return hasDescendant(withImageResource(imageResource));
    }

    public static Matcher<Bitmap> sameBitmapAs(final Bitmap imageResource) {
        return new BoundedMatcher<Bitmap, Bitmap>(Bitmap.class) {
            @Override
            protected boolean matchesSafely(Bitmap item) {
                return item.sameAs(imageResource);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("same with " + imageResource + " bitmap ");
            }
        };
    }

    public static Matcher<Root> isToast() {
        return new IsToastMatcher();
    }

    public static Matcher<View> isShown() {
        return new ShownMatcher();
    }

    public static Matcher<View> withPosition(final Matcher<View> viewMatcher, final int position) {
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("with " + position + " position: ");
                viewMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(RecyclerView list) {
                return viewMatcher.matches(list.getChildAt(position));
            }
        };
    }

    public static Matcher<View> withImageResource(int resourceId) {
        return new ImageResourceMatcher(ImageView.class, resourceId);
    }

    public static Matcher<View> withIconResource(int resourceId) {
        return new IconResourceMatcher(ActionMenuItemView.class, resourceId);
    }

    public static Matcher<View> withSearchViewHint(final Matcher<String> stringMatcher) {
        return new BoundedMatcher<View, TextView>(TextView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("with hint: ");
                stringMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(TextView textView) {
                return stringMatcher.matches(textView.getHint().toString());
            }
        };
    }

    private static class VisibilityMatcher extends BaseMatcher<View> {
        @Override public void describeTo(Description description) {
            description.appendText("View must be visible!");
        }

        @Override
        public boolean matches(Object item) {
            if (item == null) return false;

            if (!(item instanceof View)) throw new IllegalArgumentException("Object has to be instance of View instead of " + item);
            return ((View) item).getVisibility() == View.VISIBLE;
        }
    }

    private static class ShownMatcher extends BaseMatcher<View> {
        @Override public void describeTo(Description description) {
            description.appendText("View must be shown!");
        }

        @Override
        public boolean matches(Object item) {
            if (item == null) return false;

            if (!(item instanceof View)) throw new IllegalArgumentException("Object has to be instance of View instead of " + item);
            return ((View) item).isShown();
        }
    }

    private static class IsToastMatcher extends TypeSafeMatcher<Root> {
        @Override
        protected boolean matchesSafely(Root item) {
            int lpType = item.getWindowLayoutParams().get().type;
            if (lpType == WindowManager.LayoutParams.TYPE_TOAST) {
                IBinder windowToken = item.getDecorView().getWindowToken();
                IBinder appToken = item.getDecorView().getApplicationWindowToken();
                if (windowToken == appToken) return true;
            }
            return false;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("View must be toast!");
        }
    }

    private static class HasViewMatcher extends BaseMatcher<View> {
        private Matcher<? super View> mViewMatcher;

        public HasViewMatcher(Matcher<? super View> viewMatcher) {
            mViewMatcher = viewMatcher;
        }

        @Override public void describeTo(Description description) {
            description.appendText("View must be visible!");
        }

        @Override
        public boolean matches(Object item) {
            if (item == null) return false;

            if (!(item instanceof View)) throw new IllegalArgumentException("Object has to be instance of View instead of " + item);

            for (View childView : TreeIterables.breadthFirstViewTraversal((View) item)) {
                if (mViewMatcher.matches(childView)) {
                    return true;
                }
            }
            return false;
        }
    }

    private static class HasErrorMatcher extends BaseMatcher<View> {
        @Override public void describeTo(Description description) {
            description.appendText("EditText must have error!");
        }

        @Override
        public boolean matches(Object item) {
            if (item == null) return false;

            if (!(item instanceof EditText)) throw new IllegalArgumentException("Object has to be instance of EditText instead of " + item);

            return ((EditText) item).getError() != null;
        }
    }

    private static class HasItemMatcher extends BaseMatcher<View> {

        @Override public void describeTo(Description description) {
            description.appendText("has some items");
        }

        @Override
        public boolean matches(Object item) {
            if (item == null) return false;

            if (!(item instanceof RecyclerView)) throw new IllegalArgumentException("Object has to be instance of RecyclerView instead of " + item);

            return ((RecyclerView) item).getAdapter().getItemCount() != 0;
        }
    }

    private static class ImageResourceMatcher extends BoundedMatcher<View, ImageView> {

        private int mResourceId;

        public ImageResourceMatcher(Class<? extends ImageView> expectedType, int resourceId) {
            super(expectedType);
            mResourceId = resourceId;
        }

        @Override public void describeTo(Description description) {
            description.appendText("with image resource: ");
        }

        @Override
        protected boolean matchesSafely(ImageView item) {
            if (mResourceId < 0){
                return item.getDrawable() == null;
            }
            Resources resources = item.getContext().getResources();
            Drawable expectedDrawable = resources.getDrawable(mResourceId);
            if (expectedDrawable == null) return false;

            Drawable currentDrawable = item.getDrawable();
            if(!(currentDrawable instanceof BitmapDrawable)) return false;

            Bitmap currentBitmap = ((BitmapDrawable) currentDrawable).getBitmap();
            Bitmap expectedBitmap = ((BitmapDrawable) expectedDrawable).getBitmap();
            return currentBitmap.sameAs(expectedBitmap);
        }
    }

    private static class IconResourceMatcher extends BoundedMatcher<View, ActionMenuItemView> {

        private int mResourceId;

        public IconResourceMatcher(Class<? extends ActionMenuItemView> expectedType, int resourceId) {
            super(expectedType);
            mResourceId = resourceId;
        }

        @Override public void describeTo(Description description) {
            description.appendText("with image resource: ");
        }

        @Override
        protected boolean matchesSafely(ActionMenuItemView item) {
            if (mResourceId < 0){
                return item.getItemData().getIcon().getConstantState() == null;
            }
            Resources resources = item.getContext().getResources();
            Drawable expectedDrawable = resources.getDrawable(mResourceId);
            if (expectedDrawable == null) return false;

            Drawable.ConstantState expectedConstantState = expectedDrawable.getConstantState();
            Drawable.ConstantState currentConstantState = item.getItemData().getIcon().getConstantState();

            return currentConstantState.equals(expectedConstantState);
        }
    }
}
