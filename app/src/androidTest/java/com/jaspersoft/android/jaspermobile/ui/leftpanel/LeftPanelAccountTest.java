package com.jaspersoft.android.jaspermobile.ui.leftpanel;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.jaspersoft.android.jaspermobile.support.AccountUrlProvider;
import com.jaspersoft.android.jaspermobile.support.matcher.AdditionalViewAssertion;
import com.jaspersoft.android.jaspermobile.support.page.LeftPanelPageObject;
import com.jaspersoft.android.jaspermobile.support.page.LoginPageObject;
import com.jaspersoft.android.jaspermobile.support.rule.ActivityWithLoginRule;
import com.jaspersoft.android.jaspermobile.support.rule.DisableAnimationsRule;
import com.jaspersoft.android.jaspermobile.ui.view.activity.NavigationActivity_;

import junit.framework.AssertionFailedError;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * @author Tom Koptel
 * @since 2.6
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class LeftPanelAccountTest {
    private LeftPanelPageObject leftPanelPageObject = new LeftPanelPageObject();
    private LoginPageObject loginPageObject = new LoginPageObject();

    @ClassRule
    public static DisableAnimationsRule disableAnimationsRule = new DisableAnimationsRule();

    @Rule
    public ActivityTestRule page = new ActivityWithLoginRule<>(NavigationActivity_.class);

    @Before
    public void init() {
        selectTestAccount();
        addAdditionalAccount();
        selectTestAccount();
    }

    private void selectTestAccount(){
        leftPanelPageObject.swipeToOpenMenu();
        try {
            leftPanelPageObject.profileMatches(withText("accountUnderTest"));
            leftPanelPageObject.swipeToCloseMenu();
        } catch (AssertionFailedError error) {
            leftPanelPageObject.clickAccountsButton();
            leftPanelPageObject.selectAccount("accountUnderTest");
        }
    }

    private void addAdditionalAccount(){
        leftPanelPageObject.swipeToOpenMenu();
        leftPanelPageObject.clickAccountsButton();
        try {
            leftPanelPageObject.accountsMatches(AdditionalViewAssertion.hasText("additionalAcc"));
            leftPanelPageObject.swipeToCloseMenu();
        } catch (AssertionFailedError error) {
            leftPanelPageObject.clickAddAccountButton();
            loginPageObject.typeAlias("additionalAcc");
            loginPageObject.typeUserName("joeuser");
            loginPageObject.typePassword("joeuser");
            loginPageObject.typeUrl(AccountUrlProvider.provide());
            loginPageObject.clickLoginButton();
        }
    }

    @Test
    public void addAccount() {
        leftPanelPageObject.swipeToOpenMenu();
        leftPanelPageObject.clickAccountsButton();
        leftPanelPageObject.clickAddAccountButton();
        loginPageObject.loginButtonMatches(isDisplayed());
    }

    @Test
    public void listAccounts() {
        leftPanelPageObject.swipeToOpenMenu();
        leftPanelPageObject.clickAccountsButton();
        leftPanelPageObject.accountsMatches(isDisplayed());
    }

    @Test
    public void switchAccount() {
        leftPanelPageObject.swipeToOpenMenu();
        leftPanelPageObject.clickAccountsButton();
        leftPanelPageObject.selectAccount("additionalAcc");

        leftPanelPageObject.clickBurgerButton();
        leftPanelPageObject.profileMatches(withText("additionalAcc"));

        leftPanelPageObject.clickAccountsButton();
        leftPanelPageObject.selectAccount("accountUnderTest");

        leftPanelPageObject.clickBurgerButton();
        leftPanelPageObject.profileMatches(withText("accountUnderTest"));
    }

    @Test
    public void testAccountSelected() {
        leftPanelPageObject.clickBurgerButton();
        leftPanelPageObject.clickAccountsButton();
        leftPanelPageObject.selectAccount("additionalAcc");

        leftPanelPageObject.clickBurgerButton();
        leftPanelPageObject.profileMatches(withText("additionalAcc"));
    }
}
