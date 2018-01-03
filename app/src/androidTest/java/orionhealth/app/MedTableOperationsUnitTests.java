package orionhealth.app;

import android.content.Context;
import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.RenamingDelegatingContext;
import android.test.suitebuilder.annotation.SmallTest;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.MedicationStatement;
import ca.uhn.fhir.model.dstu2.valueset.MedicationStatementStatusEnum;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import orionhealth.app.data.medicationDatabase.DatabaseContract;
import orionhealth.app.data.medicationDatabase.MedTableOperations;
import orionhealth.app.fhir.FhirServices;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created by bill on 16/06/16.
 */

@RunWith(AndroidJUnit4.class)
@SmallTest
public class MedTableOperationsUnitTests {
	private MedTableOperations medTableOperations;
	private Context context;

	@Before
	public void setup() {
		 medTableOperations = MedTableOperations.getInstance();
		 context =
		   		new RenamingDelegatingContext(InstrumentationRegistry
				                              .getInstrumentation()
				                              .getTargetContext(), "test_");
	}

	@Test
	public void MedTableOperations_add() {
		medTableOperations.clearMedTable(context);
		Cursor cursor = medTableOperations.getAllRows(context);
		assertThat(cursor.getCount(), is(0));

		MedicationStatement medicationStatement = new MedicationStatement();
		CodeableConceptDt codeableConceptDt = new CodeableConceptDt().setText("test1");
		medicationStatement.setMedication(codeableConceptDt);
		medicationStatement.setStatus(MedicationStatementStatusEnum.ACTIVE);
		ResourceReferenceDt patientRef = new ResourceReferenceDt().setDisplay("LOCAL");
		medicationStatement.setPatient(patientRef);
		medicationStatement.setReasonForUse(new CodeableConceptDt().setText("test"));
		medicationStatement.setNote("test");


		medTableOperations.addToMedTable(context, medicationStatement);
		cursor = medTableOperations.getAllRows(context);
		assertThat(cursor.getCount(), is(1));
		cursor.moveToPosition(0);
		String jsonString =
		  		cursor.getString(cursor.getColumnIndex(DatabaseContract.MedTableInfo.COLUMN_NAME_JSON_STRING));
		MedicationStatement medicationStatement1 =
		  		(MedicationStatement) FhirServices.getsFhirServices().toResource(jsonString);
		testIfTwoMedicationEqual(medicationStatement, medicationStatement1);

		medicationStatement = new MedicationStatement();
		codeableConceptDt = new CodeableConceptDt().setText("parectamol");
		medicationStatement.setMedication(codeableConceptDt);
		patientRef = new ResourceReferenceDt().setDisplay("LOCAL");
		medicationStatement.setPatient(patientRef);
		medicationStatement.setReasonForUse(new CodeableConceptDt().setText("headache and sore throat"));

		medTableOperations.addToMedTable(context, medicationStatement);
		cursor = medTableOperations.getAllRows(context);
		assertThat(cursor.getCount(), is(2));
		cursor.moveToPosition(1);
		jsonString =
		  cursor.getString(cursor.getColumnIndex(DatabaseContract.MedTableInfo.COLUMN_NAME_JSON_STRING));
		medicationStatement1 =
		  (MedicationStatement) FhirServices.getsFhirServices().toResource(jsonString);
		testIfTwoMedicationEqual(medicationStatement, medicationStatement1);

		medicationStatement = new MedicationStatement();
		codeableConceptDt = new CodeableConceptDt().setText("27e023847230");
		medicationStatement.setMedication(codeableConceptDt);
		medicationStatement.setNote("remember to take out the trash");

		medTableOperations.addToMedTable(context, medicationStatement);
		cursor = medTableOperations.getAllRows(context);
		assertThat(cursor.getCount(), is(3));
		cursor.moveToPosition(2);
		jsonString =
		  cursor.getString(cursor.getColumnIndex(DatabaseContract.MedTableInfo.COLUMN_NAME_JSON_STRING));
		medicationStatement1 =
		  (MedicationStatement) FhirServices.getsFhirServices().toResource(jsonString);
		testIfTwoMedicationEqual(medicationStatement, medicationStatement1);

		medTableOperations.clearMedTable(context);
	}

	public void testIfTwoMedicationEqual(MedicationStatement medicationStatement1,
										 MedicationStatement medicationStatement2) {
		assertThat(medicationStatement1.getStatus(), is(medicationStatement2.getStatus()));
		assertThat(medicationStatement1.getNote(), is(medicationStatement2.getNote()));
		assertThat(medicationStatement1.getResourceName(), is(medicationStatement2.getResourceName()));

		CodeableConceptDt codeableConceptDt1;
		CodeableConceptDt codeableConceptDt12;

		ResourceReferenceDt resourceReferenceDt1;
		ResourceReferenceDt resourceReferenceDt2;

		if (medicationStatement1.getMedication() != null) {
			codeableConceptDt1 = (CodeableConceptDt) medicationStatement1.getMedication();
			codeableConceptDt12 = (CodeableConceptDt) medicationStatement2.getMedication();
			assertThat(codeableConceptDt1.getText(), is(codeableConceptDt12.getText()));
		}

		if (medicationStatement1.getReasonForUse() != null) {
			codeableConceptDt1 = (CodeableConceptDt) medicationStatement1.getReasonForUse();
			codeableConceptDt12 = (CodeableConceptDt) medicationStatement2.getReasonForUse();
			assertThat(codeableConceptDt1.getText(), is(codeableConceptDt12.getText()));
		}

		if (medicationStatement1.getPatient() != null) {
			resourceReferenceDt1 = medicationStatement1.getPatient();
			resourceReferenceDt2 = medicationStatement2.getPatient();
			assertThat(resourceReferenceDt1.getDisplay(), is(resourceReferenceDt2.getDisplay()));
		}

	}
}


