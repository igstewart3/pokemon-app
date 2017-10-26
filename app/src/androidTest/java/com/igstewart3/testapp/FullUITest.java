package com.igstewart3.testapp;


import android.app.Instrumentation;
import android.provider.Settings;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class FullUITest {

    @Rule
    public ActivityTestRule<ListActivity> mActivityTestRule = new ActivityTestRule<>(ListActivity.class);

    @Test
    public void fullUITest() throws  InterruptedException {
        Instrumentation inst = InstrumentationRegistry.getInstrumentation();

        // Espresso does not wait for Volley async tasks, so this inelegant loop waits for UI element to appear.
        do {
            try {
                onView(withText("bulbasaur")).check(matches(isDisplayed()));
                break;
            } catch (NoMatchingViewException e) {
                // View not displayed yet, keep looping
            }
            Thread.sleep(100);
        }
        while(true);

        ViewInteraction appCompatTextView = onView(
                allOf(withId(android.R.id.text1), withText("bulbasaur"),
                        childAtPosition(
                                withId(R.id.pokemon_list_view),
                                0),
                        isDisplayed()));
        appCompatTextView.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.pokemon_name), withText("bulbasaur"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        textView.check(matches(withText("bulbasaur")));

        pressBack();

        ViewInteraction textView2 = onView(
                allOf(withText("Pokemon App"),
                        childAtPosition(
                                allOf(withId(R.id.action_bar),
                                        childAtPosition(
                                                withId(R.id.action_bar_container),
                                                0)),
                                0),
                        isDisplayed()));
        textView2.check(matches(withText("Pokemon App")));

        ViewInteraction textView3 = onView(
                allOf(withId(android.R.id.text1), withText("bulbasaur"),
                        childAtPosition(
                                allOf(withId(R.id.pokemon_list_view),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                                0)),
                                0),
                        isDisplayed()));
        textView3.check(matches(withText("bulbasaur")));

        ViewInteraction textView4 = onView(
                allOf(withId(android.R.id.text1), withText("squirtle"),
                        childAtPosition(
                                allOf(withId(R.id.pokemon_list_view),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                                0)),
                                6),
                        isDisplayed()));
        textView4.check(matches(withText("squirtle")));

        ViewInteraction textView5 = onView(
                allOf(withId(android.R.id.text1), withText("butterfree"),
                        childAtPosition(
                                allOf(withId(R.id.pokemon_list_view),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                                0)),
                                11),
                        isDisplayed()));
        textView5.check(matches(withText("butterfree")));

        ViewInteraction textView6 = onView(
                allOf(withId(android.R.id.text1), withText("butterfree"),
                        childAtPosition(
                                allOf(withId(R.id.pokemon_list_view),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                                0)),
                                11),
                        isDisplayed()));
        textView6.check(matches(withText("butterfree")));

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
