package orionhealth.app.fhir;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.resource.*;
import ca.uhn.fhir.rest.api.SummaryEnum;
import org.hl7.fhir.instance.model.api.IBaseResource;

import java.util.ArrayList;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.AllergyIntolerance;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.Condition;
import ca.uhn.fhir.model.dstu2.resource.Medication;
import ca.uhn.fhir.model.dstu2.resource.MedicationStatement;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.valueset.BundleTypeEnum;
import ca.uhn.fhir.model.dstu2.valueset.HTTPVerbEnum;
import ca.uhn.fhir.model.dstu2.valueset.MedicationStatementStatusEnum;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.IGenericClient;
import orionhealth.app.activities.adaptors.MedicationExpandableListAdapter;
import orionhealth.app.activities.main.AddDoctorActivity;
import orionhealth.app.activities.main.MainActivity;
import orionhealth.app.data.dataModels.MyAllergyIntolerance;
import orionhealth.app.data.dataModels.MyCondition;
import orionhealth.app.data.dataModels.MyMedication;
import orionhealth.app.data.medicationDatabase.AllergyTableOperations;
import orionhealth.app.data.medicationDatabase.CondTableOperations;
import orionhealth.app.data.medicationDatabase.DatabaseContract;
import orionhealth.app.data.medicationDatabase.MedTableOperations;
import orionhealth.app.services.UpdateUIService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bill on 1/05/16.
 */
public final class FhirServices {
	private static FhirServices sFhirServices;
	private FhirContext mFhirContext;
	private String mServerBase = "http://fhirtest.uhn.ca/baseDstu2";
	private Patient mPatient;


	private FhirServices(){
	}

	public static FhirServices getsFhirServices() {
		if (sFhirServices == null){
			sFhirServices = new FhirServices();
			return sFhirServices;
		}else{
			return sFhirServices;
		}
	}

	private FhirContext getFhirContextInstance() {
		if (mFhirContext == null) {
			mFhirContext = FhirContext.forDstu2();
			return mFhirContext;
		}else{
			return mFhirContext;
		}
	}

	public String toJsonString(IBaseResource resource){
		FhirContext fhirContext = getFhirContextInstance();
		return fhirContext.newJsonParser().encodeResourceToString(resource);
	}

	public IBaseResource toResource(String jsonString){
		FhirContext fhirContext = getFhirContextInstance();
		return fhirContext.newJsonParser().parseResource(jsonString);
	}

//	Fhir medication service started

	public void sendMedicationToServer(MyMedication resource, Context context){
		SendMedicationToServerTask task = new SendMedicationToServerTask(context);
		task.execute(resource);
	}

	public ArrayList<String> searchMedication(CharSequence constraint) {
		FhirContext fhirContext = getFhirContextInstance();
		IGenericClient client = fhirContext.newRestfulGenericClient(mServerBase);
		ArrayList<String> result = new ArrayList<>();

		Bundle response = client
		  .search()
		  .forResource(Medication.class)
		  .where(Medication.CODE.isMissing(false))
		  .count(50)
		  .returnBundle(Bundle.class)
		  .execute();

		while ((response.getLink(Bundle.LINK_NEXT) != null)) {

			List<Bundle.Entry> entryList = response.getEntry();
			for (int i = 0; i < entryList.size(); i++) {
				Bundle.Entry entry = entryList.get(i);
				Medication med = (Medication) entry.getResource();

				List<CodingDt> list = med.getCode().getCoding();

				if (!list.isEmpty()) {
					CodingDt code = list.get(0);

					String medName = code.getDisplay();

					if (medName != null && medName.toLowerCase().contains(constraint.toString().toLowerCase())) {
						result.add(medName);
					}

					if (result.size() == 5) {
						return result;
					}
				}
			}

			response = client.loadPage().next(response).execute();
		}
		return result;
	}

	public void doctorSendMedicationToServer(MedicationStatement resource, Context context){
		doctorSendMedicationToServerTask task = new doctorSendMedicationToServerTask(context);
		task.execute(resource);
	}

	public void updateMedicationServer(MyMedication resource, Context context){
		UpdateMedicationToServerTask task = new UpdateMedicationToServerTask(context);
		task.execute(resource);
	}

	public void inactiveMedication(MedicationStatement resource, Context context){
		inactiveMedicationToServerTask task = new inactiveMedicationToServerTask(context);
		task.execute(resource);
	}

	//	Fhir condition service started
	public void  sendConditionToServer(MyCondition resource, Context context){
		SendConditionToServerTask task = new SendConditionToServerTask(context);
		task.execute(resource);
	}

	public void  updateConditionServer(MyCondition resource, Context context){
		UpdateConditionToServerTask task = new UpdateConditionToServerTask(context);
		task.execute(resource);
	}

	//	Fhir AllergyIntolerance service started
	public void  sendAllergyToServer(MyAllergyIntolerance resource, Context context){
		SendAllergyToServerTask task = new SendAllergyToServerTask(context);
		task.execute(resource);
	}

//	Fhir medication service finished

	public void  updateAllergyServer(MyAllergyIntolerance resource, Context context){
		UpdateAllergyToServerTask task = new UpdateAllergyToServerTask(context);
		task.execute(resource);
	}

	//	Fhir sync 1 - search whether all the local medication resources has been sent to the server, if not then send.
	public void PushLocalToServer(Context context) {
		//query from database of all the med resources
		Cursor cursor = MedTableOperations.getInstance().getAllRows(context);
		ArrayList<MyMedication> unsentMeds = new ArrayList<MyMedication>();

		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {

			String jsonMedString = cursor.getString(cursor.getColumnIndex(DatabaseContract.MedTableInfo.COLUMN_NAME_JSON_STRING));
			int medID = cursor.getInt(0);
			MedicationStatement medStatement = (MedicationStatement) toResource(jsonMedString);

			if (medStatement.getId().isEmpty()){
				// set alarm boolean as false for convenience
				unsentMeds.add(new MyMedication(medID, medStatement, false));
			}
			cursor.moveToNext();
		}
		System.out.println("The amount of unsent data" + unsentMeds.size());

		String outcomeMessage;
		try {
			for (int j = 0; j < unsentMeds.size(); j++) {
				sendMedicationToServer(unsentMeds.get(j), context);
			}
			outcomeMessage = "All local data is synced.";

		} catch (Exception e) {
			e.printStackTrace();
			outcomeMessage = "Failed to Push";
		}

		Toast.makeText(context,outcomeMessage, Toast.LENGTH_LONG).show();
	}

	//	Fhir sync 2 - search the medication code "Test" in the server
	public void PullServerToLocal(Context context){
		PullServerToLocalTask task = new PullServerToLocalTask(context);
		task.execute();
	}

	private class PullServerToLocalTask extends AsyncTask<Object[], Integer, Void> {

		private Context context;
		private String outcomeMessage;

		private PullServerToLocalTask(Context context) {
			this.context = context;
		}

		@Override
		protected Void doInBackground(Object[]... params) {
			FhirContext fhirContext = getFhirContextInstance();
			IGenericClient client = fhirContext.newRestfulGenericClient(mServerBase);
			ArrayList<MedicationStatement> newFoundMeds = new ArrayList<MedicationStatement>();
			ArrayList<String> newFoundMedIds = new ArrayList<>();
			ArrayList<String> existingMedIds = new ArrayList<>();

			try {
				// Perform a search
				System.out.println("performing search");
				Bundle results = client
						.search()
						.forResource(MedicationStatement.class)
						.where(MedicationStatement.PATIENT.hasChainedProperty(Patient.FAMILY.matches().value("Maryjane")))
				        .and(MedicationStatement.STATUS.exactly().code("active"))
						.returnBundle(ca.uhn.fhir.model.dstu2.resource.Bundle.class)
						.execute();
				// read found results to return the objects
				for (int i = 0;i<results.getEntry().size();i++) {
//					System.out.println(results.getEntry().get(i).getFullUrl());
					MedicationStatement newMed =
					  		(MedicationStatement) results.getEntry().get(i).getResource();
					newFoundMedIds.add(newMed.getId().getIdPart());
					newFoundMeds.add(newMed);
				}
				// check if the found objects already exists
				Cursor cursor = MedTableOperations.getInstance().getAllRows(context);

				while (cursor.moveToNext()) {
					String jsonMedString = cursor.getString(cursor.getColumnIndex(DatabaseContract.MedTableInfo.COLUMN_NAME_JSON_STRING));
					MedicationStatement medStatement = (MedicationStatement) toResource(jsonMedString);
					existingMedIds.add(medStatement.getId().getIdPart());
				}

				for (int j = 0;j < newFoundMeds.size();j++) {
					if (!existingMedIds.contains(newFoundMedIds.get(j))) {
						MyMedication myMedication = new MyMedication();
						myMedication.setFhirMedStatement(newFoundMeds.get(j));
						MedTableOperations.getInstance().addToMedTable(context, myMedication);
					}
				}
				System.out.println("Found " + newFoundMeds.size() + " new resources");
				outcomeMessage = "Found " + newFoundMeds.size() + " new resources";
			} catch (Exception e) {
				e.printStackTrace();
				outcomeMessage = "Search failed";

			}
			return null;
		}

		protected void onPostExecute(Void v) {
			super.onPostExecute(v);
			Toast.makeText(context,outcomeMessage, Toast.LENGTH_LONG).show();
//			refreshing the list after async task(pull)
//			Intent intent = new Intent(context, MainActivity.class);
//			context.startActivity(intent);
			Intent intent = new Intent(context, UpdateUIService.class);
			context.startService(intent);
		}
	}

	private class doctorSendMedicationToServerTask extends AsyncTask<MedicationStatement, Integer, Void> {

		private Context context;
		private String outcomeMessage;

		private doctorSendMedicationToServerTask(Context context){
			this.context = context;
		}

		protected Void doInBackground(MedicationStatement... params) {
			FhirContext fhirContext = getFhirContextInstance();
			IGenericClient client = fhirContext.newRestfulGenericClient(mServerBase);

			try {
				// hardcoded patient, change value to reset the patient
				Patient patient = new Patient();
				patient.addIdentifier()
						.setSystem("http://acme.org/mrns")
						.setValue("34567");
				patient.addName()
						.addFamily("Maryjane");
				patient.setId(IdDt.newRandomUuid());

				// Create a bundle that will be used as a transaction
				Bundle bundle = new Bundle();
				bundle.setType(BundleTypeEnum.TRANSACTION);

				// Conditional create - it
				// will only be created if there isn't already a Patient with
				// the identifier 23456 with HTTP POST
				bundle.addEntry()
						.setFullUrl(patient.getId().getValue())
						.setResource(patient)
						.getRequest()
						.setUrl("Patient")
						.setIfNoneExist("Patient?identifier=http://acme.org/mrns|34567")
						.setMethod(HTTPVerbEnum.POST);


				for (int i = 0; i < params.length; i++) {
					MedicationStatement m = params[i];
					m.setPatient(new ResourceReferenceDt(patient.getId().getValue()));
					bundle.addEntry()
							.setResource(m)
							.getRequest()
							.setUrl("MedicationStatement")
							.setMethod(HTTPVerbEnum.POST);

					// Create a client and post the transaction to the server
					Bundle resp = client.transaction().withBundle(bundle).execute();
//
					// Log the response
//					System.out.println(fhirContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(resp));

//					mPatient.setId(resp.getEntry().get(0).getFullUrl());
					IdDt id = (IdDt) resp.getId();
					Log.d("SENT TO SERVER", "Got Id " + id);
					outcomeMessage = "Sent to Server " +id;
					return null;
				}

			} catch (Exception e) {
				e.printStackTrace();
				outcomeMessage = "Failed to Send to the Server";
			}
			return null;
		}

		protected void onPostExecute(Void v) {
			super.onPostExecute(v);
			Toast.makeText(context,outcomeMessage, Toast.LENGTH_LONG).show();
		}
	}

//FHIR medication task started
	private class SendMedicationToServerTask extends AsyncTask<MyMedication, Integer, Void> {

		private Context context;
		private String outcomeMessage;

		private SendMedicationToServerTask(Context context){
			this.context = context;
		}

		protected Void doInBackground(MyMedication... params) {
			FhirContext fhirContext = getFhirContextInstance();
			IGenericClient client = fhirContext.newRestfulGenericClient(mServerBase);

			try {
				for (int i = 0; i < params.length; i++) {
					MyMedication myMedication = params[i];
					MethodOutcome outcome = client.create().resource(myMedication.getFhirMedStatement()).prettyPrint().encodedJson().execute();
					IdDt id = (IdDt) outcome.getId();
					MedicationStatement medicationStatement = myMedication.getFhirMedStatement();
					medicationStatement.setId(id);
					MedTableOperations.getInstance().updateMedication(context, params[i].getLocalId(), myMedication);
					Log.d("SENT TO SERVER", "Got Id " + id);
					outcomeMessage = "Sent to Server " +id;
					return null;
				}

			} catch (Exception e) {
				e.printStackTrace();
				outcomeMessage = "Failed to Send to Server";
			}
			return null;
		}

		protected void onPostExecute(Void v) {
			super.onPostExecute(v);
			Toast.makeText(context,outcomeMessage, Toast.LENGTH_LONG).show();
		}
	}

	private class UpdateMedicationToServerTask extends AsyncTask<MyMedication, Integer, Void> {

		private Context context;
		private String outcomeMessage;

		private UpdateMedicationToServerTask(Context context){
			this.context = context;
		}

		protected Void doInBackground(MyMedication... params) {
			FhirContext fhirContext = getFhirContextInstance();
			IGenericClient client = fhirContext.newRestfulGenericClient(mServerBase);

			try {
				for (int i = 0; i < params.length; i++) {
					MyMedication myMedication = params[i];
					MethodOutcome outcome = client.update().resource(myMedication.getFhirMedStatement())
							.execute();
					IdDt id = (IdDt) outcome.getId();
					myMedication.getFhirMedStatement().setId(id);
					MedTableOperations.getInstance().updateMedication(context, myMedication.getLocalId(), myMedication);
					Log.d("UPDATE SERVER", "Got Id " + id);
					outcomeMessage = "Update the Server " +id;
					return null;
				}

			} catch (Exception e) {
				e.printStackTrace();
				outcomeMessage = "Failed to Update to Server";
			}
			return null;
		}

		protected void onPostExecute(Void v) {
			super.onPostExecute(v);
			Toast.makeText(context,outcomeMessage, Toast.LENGTH_LONG).show();
		}
	}

	private class inactiveMedicationToServerTask extends AsyncTask<MedicationStatement, Integer, Void> {

		private Context context;
		private String outcomeMessage;

		private inactiveMedicationToServerTask(Context context){
			this.context = context;
		}

		protected Void doInBackground(MedicationStatement... params) {
			FhirContext fhirContext = getFhirContextInstance();
			IGenericClient client = fhirContext.newRestfulGenericClient(mServerBase);

			try {
				for (int i = 0; i < params.length; i++) {
					params[i].setStatus(MedicationStatementStatusEnum.COMPLETED);
					MethodOutcome outcome = client.update().resource(params[i])
							.execute();
					IdDt id = (IdDt) outcome.getId();
					Log.d("INACTIVE", "Got Id " + id);
					outcomeMessage = "Disable the Medication " +id + "In Fhir server";
					return null;
				}

			} catch (Exception e) {
				e.printStackTrace();
				outcomeMessage = "Failed to Update to Server";
			}
			return null;
		}

		protected void onPostExecute(Void v) {
			super.onPostExecute(v);
			Toast.makeText(context,outcomeMessage, Toast.LENGTH_LONG).show();
		}
	}
//	FHIR medication tasks stopped

	private class SendConditionToServerTask extends AsyncTask<MyCondition, Integer, Void> {

		private Context context;
		private String outcomeMessage;

		private SendConditionToServerTask(Context context){
			this.context = context;
		}

		protected Void doInBackground(MyCondition... params) {
			FhirContext fhirContext = getFhirContextInstance();
			IGenericClient client = fhirContext.newRestfulGenericClient(mServerBase);

			try {
				for (int i = 0; i < params.length; i++) {
					MethodOutcome outcome = client.create().resource(params[i].getFhirCondition()).prettyPrint().encodedJson().execute();
					IdDt id = (IdDt) outcome.getId();
					Condition m = CondTableOperations.getInstance().getCondition(context, params[i].getLocalId());
					m.setId(id);
					CondTableOperations.getInstance().updateCondition(context, params[i].getLocalId(), m);
					Log.d("SENT TO SERVER", "Got Id " + id);
					outcomeMessage = "Sent to Server " +id;
					return null;
				}

			} catch (Exception e) {
				e.printStackTrace();
				outcomeMessage = "Failed to Send to Server";
			}
			return null;
		}

		protected void onPostExecute(Void v) {
			super.onPostExecute(v);
			Toast.makeText(context,outcomeMessage, Toast.LENGTH_LONG).show();
		}
	}

	private class UpdateConditionToServerTask extends AsyncTask<MyCondition, Integer, Void> {

		private Context context;
		private String outcomeMessage;

		private UpdateConditionToServerTask(Context context){
			this.context = context;
		}

		protected Void doInBackground(MyCondition... params) {
			FhirContext fhirContext = getFhirContextInstance();
			IGenericClient client = fhirContext.newRestfulGenericClient(mServerBase);

			try {
				for (int i = 0; i < params.length; i++) {
					MethodOutcome outcome = client.update().resource(params[i].getFhirCondition())
							.execute();
					IdDt id = (IdDt) outcome.getId();
					Condition m = CondTableOperations.getInstance().getCondition(context, params[i].getLocalId());
					m.setId(id);
					CondTableOperations.getInstance().updateCondition(context, params[i].getLocalId(), m);
					Log.d("UPDATE SERVER", "Got Id " + id);
					outcomeMessage = "Update the Server " +id;
					return null;
				}

			} catch (Exception e) {
				e.printStackTrace();
				outcomeMessage = "Failed to Update to Server";
			}
			return null;
		}

		protected void onPostExecute(Void v) {
			super.onPostExecute(v);
			Toast.makeText(context,outcomeMessage, Toast.LENGTH_LONG).show();
		}
	}
//	Fhir Allergy service finished

	private class SendAllergyToServerTask extends AsyncTask<MyAllergyIntolerance, Integer, Void> {

		private Context context;
		private String outcomeMessage;

		private SendAllergyToServerTask(Context context){
			this.context = context;
		}

		protected Void doInBackground(MyAllergyIntolerance... params) {
			FhirContext fhirContext = getFhirContextInstance();
			IGenericClient client = fhirContext.newRestfulGenericClient(mServerBase);

			try {
				for (int i = 0; i < params.length; i++) {
					MethodOutcome outcome = client.create().resource(params[i].getFhirAllergyIntolerance()).prettyPrint().encodedJson().execute();
					IdDt id = (IdDt) outcome.getId();
					AllergyIntolerance m = AllergyTableOperations.getInstance().getAllergyIntolerance(context, params[i].getLocalId());
					m.setId(id);
					AllergyTableOperations.getInstance().updateAllergy(context, params[i].getLocalId(), m);
					Log.d("SENT TO SERVER", "Got Id " + id);
					outcomeMessage = "Sent to Server " +id;
					return null;
				}

			} catch (Exception e) {
				e.printStackTrace();
				outcomeMessage = "Failed to Send to Server";
			}
			return null;
		}

		protected void onPostExecute(Void v) {
			super.onPostExecute(v);
			Toast.makeText(context,outcomeMessage, Toast.LENGTH_LONG).show();
		}
	}

	private class UpdateAllergyToServerTask extends AsyncTask<MyAllergyIntolerance, Integer, Void> {

		private Context context;
		private String outcomeMessage;

		private UpdateAllergyToServerTask(Context context){
			this.context = context;
		}

		protected Void doInBackground(MyAllergyIntolerance... params) {
			FhirContext fhirContext = getFhirContextInstance();
			IGenericClient client = fhirContext.newRestfulGenericClient(mServerBase);

			try {
				for (int i = 0; i < params.length; i++) {
					MethodOutcome outcome = client.update().resource(params[i].getFhirAllergyIntolerance())
							.execute();
					IdDt id = (IdDt) outcome.getId();
					AllergyIntolerance m = AllergyTableOperations.getInstance().getAllergyIntolerance(context, params[i].getLocalId());
					m.setId(id);
					AllergyTableOperations.getInstance().updateAllergy(context, params[i].getLocalId(), m);
					Log.d("UPDATE SERVER", "Got Id " + id);
					outcomeMessage = "Update the Server " +id;
					return null;
				}

			} catch (Exception e) {
				e.printStackTrace();
				outcomeMessage = "Failed to Update to Server";
			}
			return null;
		}

		protected void onPostExecute(Void v) {
			super.onPostExecute(v);
			Toast.makeText(context,outcomeMessage, Toast.LENGTH_LONG).show();
		}
	}


}
