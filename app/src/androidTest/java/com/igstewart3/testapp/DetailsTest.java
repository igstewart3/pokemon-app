package com.igstewart3.testapp;


import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewInteraction;
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

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class DetailsTest {

    @Rule
    public ActivityTestRule<ListActivity> mActivityTestRule = new ActivityTestRule<>(ListActivity.class);

    @Test
    public void detailsTest() throws InterruptedException {

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
                allOf(withId(android.R.id.text1), withText("ivysaur"),
                        childAtPosition(
                                withId(R.id.pokemon_list_view),
                                1),
                        isDisplayed()));
        appCompatTextView.perform(click());

        // Espresso does not wait for Volley async tasks, so this inelegant loop waits for UI element to appear.
        do {
            try {
                onView(withText("Weight: 130")).check(matches(isDisplayed()));
                break;
            } catch (NoMatchingViewException e) {
                // View not displayed yet, keep looping
            }
            Thread.sleep(100);
        }
        while(true);

        ViewInteraction textView = onView(
                allOf(withId(R.id.pokemon_weight), withText("Weight: 130"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        textView.check(matches(withText("Weight: 130")));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.pokemon_height), withText("Height: 10"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        textView2.check(matches(withText("Height: 10")));

        ViewInteraction imageView = onView(
                allOf(withId(R.id.pokemon_image),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        imageView.check(matches(isDisplayed()));

        ViewInteraction textView3 = onView(
                allOf(withId(android.R.id.text1), withText("swords-dance"),
                        childAtPosition(
                                allOf(withId(R.id.pokemon_moves_list),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                                5)),
                                0),
                        isDisplayed()));
        textView3.check(matches(withText("swords-dance")));

        ViewInteraction textView4 = onView(
                allOf(withId(android.R.id.text1), withText("cut"),
                        childAtPosition(
                                allOf(withId(R.id.pokemon_moves_list),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                                5)),
                                1),
                        isDisplayed()));
        textView4.check(matches(withText("cut")));

        ViewInteraction textView5 = onView(
                allOf(withId(android.R.id.text1), withText("bind"),
                        childAtPosition(
                                allOf(withId(R.id.pokemon_moves_list),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                                5)),
                                2),
                        isDisplayed()));
        textView5.check(matches(withText("bind")));

        pressBack();

        ViewInteraction textView6 = onView(
                allOf(withText("Pokemon App"),
                        childAtPosition(
                                allOf(withId(R.id.action_bar),
                                        childAtPosition(
                                                withId(R.id.action_bar_container),
                                                0)),
                                0),
                        isDisplayed()));
        textView6.check(matches(withText("Pokemon App")));

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
