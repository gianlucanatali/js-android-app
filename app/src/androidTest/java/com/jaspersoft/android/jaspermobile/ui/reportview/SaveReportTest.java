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

package com.jaspersoft.android.jaspermobile.ui.reportview;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.save.SaveReportActivity_;
import com.jaspersoft.android.jaspermobile.support.page.SaveReportPageObject;
import com.jaspersoft.android.jaspermobile.support.rule.ActivityWithLoginRule;
import com.jaspersoft.android.jaspermobile.support.rule.DisableAnimationsRule;
import com.jaspersoft.android.jaspermobile.support.rule.SavePermissionRule;
import com.jaspersoft.android.jaspermobile.ui.view.activity.NavigationActivity_;
import com.jaspersoft.android.sdk.client.oxm.resource.ResourceLookup;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

import java.math.BigInteger;
import java.security.SecureRandom;

import static org.hamcrest.Matchers.startsWith;

/**
 * @author Andrew Tivodar
 * @since 2.5
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class SaveReportTest {
    private SaveReportPageObject saveReportPageObject;
    private String fileName;

    @ClassRule
    public static TestRule disableAnimationsRule = new DisableAnimationsRule();

    @ClassRule
    public static TestRule savePermissionRule = new SavePermissionRule();

    @Rule
    public ActivityTestRule<NavigationActivity_> init = new ActivityWithLoginRule<>(NavigationActivity_.class);

    @Rule
    public ActivityTestRule<SaveReportActivity_> page = new ActivityTestRule<>(SaveReportActivity_.class, false, false);

    @Rule
    public ActivityTestRule<NavigationActivity_> si = new ActivityTestRule<>(NavigationActivity_.class, false, false);

    @Before
    public void init() {
        saveReportPageObject = new SaveReportPageObject();
        fileName = nextFileName();

        launchReportSaveActivity();
    }

    @After
    public void tearDown() throws Exception {
        saveReportPageObject.waitForToastDisappear();
    }

    private void launchReportSaveActivity() {
        Intent startIntent = new Intent();
        startIntent.putExtra(SaveReportActivity_.RESOURCE_EXTRA, createStoreSegmentResourceLookup());
        page.launchActivity(startIntent);
    }

    private void launchSavedItems() {
        Intent repoIntent = new Intent();
        repoIntent.putExtra("currentSelection", R.id.vg_saved_items);
        si.launchActivity(repoIntent);
    }

    private ResourceLookup createStoreSegmentResourceLookup() {
        ResourceLookup resourceLookup = new ResourceLookup();
        resourceLookup.setLabel("03. Store Segment Performance Report");
        resourceLookup.setDescription("Sample OLAP chart with HTML5 Grouped Bar chart and Filter. Created from an Ad Hoc View.");
        resourceLookup.setUri("/public/Samples/Reports/03._Store_Segment_Performance_Report");
        resourceLookup.setResourceType("reportUnit");
        return resourceLookup;
    }

    private String nextFileName() {
        return new BigInteger(130, new SecureRandom()).toString(24);
    }

    @Test
    public void savePageAppear() {
        saveReportPageObject.titleMatches(startsWith("Save Report"));
    }

    @Test
    public void saveWithEmptyName() {
        saveReportPageObject.typeFileName("");
        saveReportPageObject.clickSave();
        saveReportPageObject.fileNameErrorMatches("This field is required.");
    }

    @Test
    public void saveWithSpacedName() {
        saveReportPageObject.typeFileName("   ");
        saveReportPageObject.clickSave();
        saveReportPageObject.fileNameErrorMatches("This field is required.");
    }

    @Test
    public void saveInHtml() {
        saveReportPageObject.typeFileName(fileName);
        saveReportPageObject.selectFormat("HTML");
        saveReportPageObject.clickSave();
        try {
            saveReportPageObject.assertToastMessage("Failed to create local file storage");
        } catch (Exception ex) {
            launchSavedItems();
            saveReportPageObject.savedItemMatches(fileName,  R.drawable.ic_file_html);
        }
        // test is success if there is no access to SD card too (expected behavior)
    }

    @Test
    public void saveInPdf() {
        saveReportPageObject.typeFileName(fileName);
        saveReportPageObject.selectFormat("PDF");
        saveReportPageObject.clickSave();

        try {
            saveReportPageObject.assertToastMessage("Failed to create local file storage");
        } catch (Exception ex) {
            launchSavedItems();
            saveReportPageObject.savedItemMatches(fileName,  R.drawable.ic_file_pdf);
        }
        // test is success if there is no access to SD card too (expected behavior)
    }

    @Test
    public void saveInXls() {
        saveReportPageObject.typeFileName(fileName);
        saveReportPageObject.selectFormat("XLS");
        saveReportPageObject.clickSave();

        try {
            saveReportPageObject.assertToastMessage("Failed to create local file storage");
        } catch (Exception ex) {
            launchSavedItems();
            saveReportPageObject.savedItemMatches(fileName,  R.drawable.ic_file_xls);
        }
        // test is success if there is no access to SD card too (expected behavior)
    }

    @Test
    public void saveDuplication() {
        saveReportPageObject.typeFileName(fileName);
        saveReportPageObject.clickSave();

        launchReportSaveActivity();

        saveReportPageObject.typeFileName(fileName);
        saveReportPageObject.clickSave();
        saveReportPageObject.fileNameErrorMatches("A file with this name already exists.");
    }

    @Test
    public void saveInDifferentFormat() {
        // NOTE:
        // test is success if there is no access to SD card too (expected behavior)

        saveReportPageObject.typeFileName(fileName);
        saveReportPageObject.clickSave();

        try {
            saveReportPageObject.assertToastMessage("Failed to create local file storage");
        } catch (Exception ex) {
            launchReportSaveActivity();
        } finally {
            saveReportPageObject.waitForToastDisappear();
        }

        saveReportPageObject.selectFormat("PDF");
        saveReportPageObject.typeFileName(fileName);
        saveReportPageObject.clickSave();

        try {
            saveReportPageObject.assertToastMessage("Failed to create local file storage");
        } catch (Exception ex) {
            launchReportSaveActivity();
        } finally {
            saveReportPageObject.waitForToastDisappear();
        }

        saveReportPageObject.selectFormat("XLS");
        saveReportPageObject.typeFileName(fileName);
        saveReportPageObject.clickSave();

        try {
            saveReportPageObject.assertToastMessage("Failed to create local file storage");
        } catch (Exception ex) {
            launchSavedItems();

            saveReportPageObject.savedItemMatches(fileName, R.drawable.ic_file_html);
            saveReportPageObject.savedItemMatches(fileName, R.drawable.ic_file_pdf);
            saveReportPageObject.savedItemMatches(fileName, R.drawable.ic_file_html);
        } finally {
            saveReportPageObject.waitForToastDisappear();
        }
    }
}
