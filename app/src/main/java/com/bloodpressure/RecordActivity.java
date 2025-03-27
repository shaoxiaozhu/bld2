package com.bloodpressure;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bloodpressure.model.Measurement;
import com.bloodpressure.viewmodel.MeasurementViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RecordActivity extends AppCompatActivity {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault());

    private MeasurementViewModel measurementViewModel;
    private TextInputEditText etSystolic, etDiastolic, etHeartRate, etNotes;
    private CheckBox cbFeelingSick, cbAfterMeal, cbAfterMedication, cbStressed;
    private TextView tvPeriodTitle, tvDate;
    private int periodType;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        
        // Show back button in action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initializeViews();
        initializeViewModel();
        getPeriodTypeFromIntent();
        updatePeriodTitle();
        setupSaveButton();
    }

    private void initializeViews() {
        tvPeriodTitle = findViewById(R.id.tvPeriodTitle);
        tvDate = findViewById(R.id.tvDate);
        etSystolic = findViewById(R.id.etSystolic);
        etDiastolic = findViewById(R.id.etDiastolic);
        etHeartRate = findViewById(R.id.etHeartRate);
        etNotes = findViewById(R.id.etNotes);
        cbFeelingSick = findViewById(R.id.cbFeelingSick);
        cbAfterMeal = findViewById(R.id.cbAfterMeal);
        cbAfterMedication = findViewById(R.id.cbAfterMedication);
        cbStressed = findViewById(R.id.cbStressed);

        // Set current date
        tvDate.setText("Today, " + DATE_FORMAT.format(new Date()));
    }

    private void initializeViewModel() {
        measurementViewModel = new ViewModelProvider(this).get(MeasurementViewModel.class);
    }

    private void getPeriodTypeFromIntent() {
        if (getIntent() != null) {
            periodType = getIntent().getIntExtra("PERIOD_TYPE", 0);
            userId = getIntent().getLongExtra("USER_ID", 1);
        }
    }

    private void updatePeriodTitle() {
        String periodTitle;
        switch (periodType) {
            case 1:
                periodTitle = "Noon Reading";
                break;
            case 2:
                periodTitle = "Afternoon Reading";
                break;
            case 3:
                periodTitle = "Bedtime Reading";
                break;
            default:
                periodTitle = "Morning Reading";
                break;
        }
        tvPeriodTitle.setText(periodTitle);
    }

    private void setupSaveButton() {
        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> saveReading());
    }

    private void saveReading() {
        String systolicStr = etSystolic.getText() != null ? etSystolic.getText().toString().trim() : "";
        String diastolicStr = etDiastolic.getText() != null ? etDiastolic.getText().toString().trim() : "";
        String heartRateStr = etHeartRate.getText() != null ? etHeartRate.getText().toString().trim() : "";
        
        // Validate inputs
        if (TextUtils.isEmpty(systolicStr)) {
            etSystolic.setError("Please enter systolic pressure");
            return;
        }
        
        if (TextUtils.isEmpty(diastolicStr)) {
            etDiastolic.setError("Please enter diastolic pressure");
            return;
        }
        
        if (TextUtils.isEmpty(heartRateStr)) {
            etHeartRate.setError("Please enter heart rate");
            return;
        }
        
        int systolic = Integer.parseInt(systolicStr);
        int diastolic = Integer.parseInt(diastolicStr);
        int heartRate = Integer.parseInt(heartRateStr);
        
        // Validate ranges
        if (systolic < 60 || systolic > 250) {
            etSystolic.setError("Systolic value should be between 60-250");
            return;
        }
        
        if (diastolic < 40 || diastolic > 150) {
            etDiastolic.setError("Diastolic value should be between 40-150");
            return;
        }
        
        if (heartRate < 40 || heartRate > 200) {
            etHeartRate.setError("Heart rate should be between 40-200");
            return;
        }
        
        // Build notes
        StringBuilder notesBuilder = new StringBuilder();
        if (etNotes.getText() != null && !TextUtils.isEmpty(etNotes.getText().toString())) {
            notesBuilder.append(etNotes.getText().toString().trim());
        }
        
        // Add checkboxes info to notes
        if (cbFeelingSick.isChecked() || cbAfterMeal.isChecked() || cbAfterMedication.isChecked() || cbStressed.isChecked()) {
            if (notesBuilder.length() > 0) {
                notesBuilder.append(", ");
            }
            notesBuilder.append("Conditions: ");
            
            if (cbFeelingSick.isChecked()) {
                notesBuilder.append("Feeling sick, ");
            }
            
            if (cbAfterMeal.isChecked()) {
                notesBuilder.append("After meal, ");
            }
            
            if (cbAfterMedication.isChecked()) {
                notesBuilder.append("After medication, ");
            }
            
            if (cbStressed.isChecked()) {
                notesBuilder.append("Stressed, ");
            }
            
            // Remove trailing comma and space
            if (notesBuilder.toString().endsWith(", ")) {
                notesBuilder.setLength(notesBuilder.length() - 2);
            }
        }
        
        String notes = notesBuilder.toString();
        
        // Create new measurement
        Measurement measurement = new Measurement(
                userId,
                Calendar.getInstance().getTimeInMillis(),
                periodType,
                systolic,
                diastolic,
                heartRate,
                notes
        );
        
        // Save to database
        measurementViewModel.insert(measurement);
        
        Toast.makeText(this, "Reading saved successfully", Toast.LENGTH_SHORT).show();
        finish(); // Close activity and return to main
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}