package com.bloodpressure;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bloodpressure.model.Measurement;
import com.bloodpressure.utils.CsvExporter;
import com.bloodpressure.viewmodel.MeasurementViewModel;
import com.bloodpressure.viewmodel.UserViewModel;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int STORAGE_PERMISSION_CODE = 101;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault());
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("h:mm a", Locale.getDefault());

    private UserViewModel userViewModel;
    private MeasurementViewModel measurementViewModel;
    private LineChart chart;
    private Spinner spinnerPeriod;
    private TextView tvGreeting, tvDate;
    private TextView tvLatestSystolic, tvLatestDiastolic, tvLatestHeartRate, tvLatestTime;
    private long currentUserId = 1; // Default user ID
    private List<Measurement> allMeasurements = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupViewModels();
        setupSpinner();
        setupClickListeners();
        updateDateTime();
    }

    private void initializeViews() {
        tvGreeting = findViewById(R.id.tvGreeting);
        tvDate = findViewById(R.id.tvDate);
        chart = findViewById(R.id.chart);
        spinnerPeriod = findViewById(R.id.spinnerPeriod);
        tvLatestSystolic = findViewById(R.id.tvLatestSystolic);
        tvLatestDiastolic = findViewById(R.id.tvLatestDiastolic);
        tvLatestHeartRate = findViewById(R.id.tvLatestHeartRate);
        tvLatestTime = findViewById(R.id.tvLatestTime);

        setupChart();
    }

    private void setupViewModels() {
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        measurementViewModel = new ViewModelProvider(this).get(MeasurementViewModel.class);

        // Load default user
        userViewModel.loadDefaultUser();

        // Observe current user
        userViewModel.getCurrentUser().observe(this, user -> {
            if (user != null) {
                currentUserId = user.getId();
                loadData();
            }
        });

        // Observe latest measurement
        measurementViewModel.getLatestMeasurement().observe(this, this::updateLatestReading);

        // Observe period measurements for chart
        measurementViewModel.getPeriodMeasurements().observe(this, measurements -> {
            allMeasurements = measurements;
            updateChart(measurements);
        });
    }

    private void loadData() {
        // Load latest measurement
        measurementViewModel.loadLatestMeasurement(currentUserId);

        // Load measurements for last 7 days by default
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        long startTime = calendar.getTimeInMillis();
        measurementViewModel.loadMeasurementsByPeriod(currentUserId, startTime, endTime);
    }

    private void setupSpinner() {
        String[] periods = {"Last 7 Days", "Last 14 Days", "Last 30 Days", "Last 90 Days"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, periods);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPeriod.setAdapter(adapter);

        spinnerPeriod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateChartPeriod(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void updateChartPeriod(int position) {
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        int days;

        switch (position) {
            case 1:
                days = 14;
                break;
            case 2:
                days = 30;
                break;
            case 3:
                days = 90;
                break;
            default:
                days = 7;
                break;
        }

        calendar.add(Calendar.DAY_OF_MONTH, -days);
        long startTime = calendar.getTimeInMillis();
        measurementViewModel.loadMeasurementsByPeriod(currentUserId, startTime, endTime);
    }

    private void setupClickListeners() {
        Button btnMorning = findViewById(R.id.btnMorning);
        Button btnNoon = findViewById(R.id.btnNoon);
        Button btnAfternoon = findViewById(R.id.btnAfternoon);
        Button btnBedtime = findViewById(R.id.btnBedtime);
        Button btnViewAll = findViewById(R.id.btnViewAll);
        Button btnExport = findViewById(R.id.btnExport);
        Button btnProfile = findViewById(R.id.btnProfile);

        btnMorning.setOnClickListener(v -> openRecordActivity(0));
        btnNoon.setOnClickListener(v -> openRecordActivity(1));
        btnAfternoon.setOnClickListener(v -> openRecordActivity(2));
        btnBedtime.setOnClickListener(v -> openRecordActivity(3));

        btnViewAll.setOnClickListener(v -> {
            // TODO: Open history activity
            Toast.makeText(MainActivity.this, "View All Records", Toast.LENGTH_SHORT).show();
        });

        btnExport.setOnClickListener(v -> exportData());

        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            intent.putExtra("USER_ID", currentUserId);
            startActivity(intent);
        });
    }

    private void openRecordActivity(int periodType) {
        Intent intent = new Intent(MainActivity.this, RecordActivity.class);
        intent.putExtra("PERIOD_TYPE", periodType);
        intent.putExtra("USER_ID", currentUserId);
        startActivity(intent);
    }

    private void setupChart() {
        // Configure chart appearance
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);
        chart.setDrawGridBackground(false);
        chart.setHighlightPerTapEnabled(true);
        chart.getLegend().setEnabled(true);

        // Configure X axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        // Configure Y axis
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisMinimum(0f);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    private void updateChart(List<Measurement> measurements) {
        if (measurements == null || measurements.isEmpty()) {
            chart.setNoDataText("No blood pressure data available");
            chart.invalidate();
            return;
        }

        List<Entry> systolicEntries = new ArrayList<>();
        List<Entry> diastolicEntries = new ArrayList<>();
        List<String> xAxisLabels = new ArrayList<>();

        SimpleDateFormat labelFormat = new SimpleDateFormat("MM/dd", Locale.getDefault());
        
        for (int i = 0; i < measurements.size(); i++) {
            Measurement m = measurements.get(i);
            systolicEntries.add(new Entry(i, m.getSystolic()));
            diastolicEntries.add(new Entry(i, m.getDiastolic()));
            xAxisLabels.add(labelFormat.format(new Date(m.getRecordTime())));
        }

        // Create datasets
        LineDataSet systolicSet = new LineDataSet(systolicEntries, "Systolic");
        systolicSet.setColor(Color.RED);
        systolicSet.setCircleColor(Color.RED);
        systolicSet.setLineWidth(2f);
        systolicSet.setCircleRadius(3f);
        systolicSet.setDrawCircleHole(false);
        systolicSet.setValueTextSize(9f);
        systolicSet.setDrawFilled(true);
        systolicSet.setFillColor(Color.parseColor("#60FF0000"));

        LineDataSet diastolicSet = new LineDataSet(diastolicEntries, "Diastolic");
        diastolicSet.setColor(Color.BLUE);
        diastolicSet.setCircleColor(Color.BLUE);
        diastolicSet.setLineWidth(2f);
        diastolicSet.setCircleRadius(3f);
        diastolicSet.setDrawCircleHole(false);
        diastolicSet.setValueTextSize(9f);
        diastolicSet.setDrawFilled(true);
        diastolicSet.setFillColor(Color.parseColor("#600000FF"));

        LineData lineData = new LineData(systolicSet, diastolicSet);
        chart.setData(lineData);
        
        // Set x-axis labels
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisLabels));
        
        chart.invalidate();
    }

    private void updateLatestReading(Measurement measurement) {
        if (measurement == null) {
            tvLatestSystolic.setText("-");
            tvLatestDiastolic.setText("-");
            tvLatestHeartRate.setText("- bpm");
            tvLatestTime.setText("No readings yet");
            return;
        }

        tvLatestSystolic.setText(String.valueOf(measurement.getSystolic()));
        tvLatestDiastolic.setText(String.valueOf(measurement.getDiastolic()));
        tvLatestHeartRate.setText(measurement.getHeartRate() + " bpm");

        Date measurementDate = new Date(measurement.getRecordTime());
        tvLatestTime.setText(DATE_FORMAT.format(measurementDate) + ", " + TIME_FORMAT.format(measurementDate));
    }

    private void updateDateTime() {
        // Update greeting based on time of day
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        
        String greeting;
        if (hourOfDay < 12) {
            greeting = "Good Morning!";
        } else if (hourOfDay < 18) {
            greeting = "Good Afternoon!";
        } else {
            greeting = "Good Evening!";
        }
        
        tvGreeting.setText(greeting);
        tvDate.setText("Today, " + DATE_FORMAT.format(calendar.getTime()));
    }

    private void exportData() {
        if (checkStoragePermission()) {
            if (allMeasurements != null && !allMeasurements.isEmpty()) {
                String filePath = CsvExporter.exportToCSV(this, allMeasurements);
                if (filePath != null) {
                    Toast.makeText(this, "Data exported to " + filePath, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Failed to export data", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "No data to export", Toast.LENGTH_SHORT).show();
            }
        } else {
            requestStoragePermission();
        }
    }

    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) 
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                STORAGE_PERMISSION_CODE
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                exportData();
            } else {
                Toast.makeText(this, "Storage permission is required to export data", Toast.LENGTH_SHORT).show();
            }
        }
    }
} 