package orionhealth.app;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import orionhealth.app.activities.main.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by bill on 13/06/16.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AddMedicationTest {

	private String mValidName1;
	private String mInValidName1;
	private String mValidDosage1;
	private String mInvalidDosage1;
	private int[] mEditTextIds;


	@Rule
	public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
	  MainActivity.class);

	public void navigateToAddMed(){
		onView(withId(R.id.button_add))
		  .perform(click());

		// Check fragment is displayed
		onView(withId(R.id.fragment_medication_details))
		  .check(matches(isDisplayed()));

		// Check that all text fields are empty.
		onView(withId(R.id.edit_text_name))
		  .check(matches(withText("")));

		onView(withId(R.id.edit_text_dosage))
		  .check(matches(withText("")));

		onView(withId(R.id.edit_text_effectiveStart))
		  .check(matches(withText("")));

		onView(withId(R.id.edit_text_effectiveEnd))
		  .check(matches(withText("")));

		onView(withId(R.id.edit_text_notes))
		  .check(matches(withText("")));

		onView(withId(R.id.edit_text_reasonForUse))
		  .check(matches(withText("")));
	}

	public void typeMedicationDetails(String... args){
		for (int i = 0; i < args.length; i++){
			onView(withId(mEditTextIds[i]))
			  .perform(typeText(args[i]), closeSoftKeyboard());
		}
	}

	@Before
	public void initNames() {
		mValidName1 = "Paracetamol";
		mInValidName1 = "";
	}

	@Before
	public void initDosages() {
		mValidDosage1 = "12";
		mInvalidDosage1 = "t";
	}

	@Before
	public void initEditTextIds() {
		mEditTextIds = new int[]{R.id.edit_text_name, R.id.edit_text_dosage,
		                         R.id.edit_text_reasonForUse, R.id.edit_text_notes};
	}

	@Test
	public void addValidMedication() {
		navigateToAddMed();
		typeMedicationDetails(mValidName1, mValidDosage1, "trouble");
		onView(withId(R.id.button_add))
		  .perform(click());

		onView(withId(R.id.fragment_medication_details))
		  .check(doesNotExist());

		onView(withId(R.id.medication_list))
		  .check(matches(isDisplayed()));

	}

	@Test
	public void addInvalidMedication() {
		navigateToAddMed();
		typeMedicationDetails(mInValidName1, mValidDosage1, "hello", "");
		onView(withId(R.id.button_add))
		  .perform(click());

		onView(withId(R.id.fragment_medication_details))
		  .check(matches(isDisplayed()));

		onView(withId(R.id.medication_list))
		  .check(doesNotExist());
	}

	@Test
	public void addInvalidMedication2() {
		navigateToAddMed();
		typeMedicationDetails(mValidName1, mInvalidDosage1);
		onView(withId(R.id.button_add))
		  .perform(click());

		onView(withId(R.id.fragment_medication_details))
		  .check(matches(isDisplayed()));

		onView(withId(R.id.medication_list))
		  .check(doesNotExist());
	}

	@Test
	public void addInvalidMedication3() {
		navigateToAddMed();
		typeMedicationDetails(mInValidName1, mInvalidDosage1, "dfas", "4566");
		onView(withId(R.id.button_add))
		  .perform(click());

		onView(withId(R.id.fragment_medication_details))
		  .check(matches(isDisplayed()));

		onView(withId(R.id.medication_list))
		  .check(doesNotExist());
	}
}


