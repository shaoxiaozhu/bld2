package com.bloodpressure.utils;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.util.Log;

import com.bloodpressure.model.Measurement;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Utility class for exporting blood pressure data to CSV files
 */
public class CsvExporter {
    private static final String TAG = "CsvExporter";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private static final SimpleDateFormat FILE_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());

    /**
     * Export blood pressure measurements to CSV file
     * @param context Application context
     * @param measurements List of measurements to export
     * @return Path to the exported file or null if export failed
     */
    public static String exportToCSV(Context context, List<Measurement> measurements) {
        if (measurements == null || measurements.isEmpty()) {
            return null;
        }

        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!downloadsDir.exists()) {
            downloadsDir.mkdirs();
        }

        String fileName = "blood_pressure_" + FILE_DATE_FORMAT.format(new Date()) + ".csv";
        File csvFile = new File(downloadsDir, fileName);

        try (FileWriter fileWriter = new FileWriter(csvFile);
             CSVPrinter csvPrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT
                     .withHeader("Date", "Time", "Period", "Systolic (mmHg)", "Diastolic (mmHg)", "Heart Rate (bpm)", "Notes"))) {

            for (Measurement m : measurements) {
                Date recordDate = new Date(m.getRecordTime());
                String periodType = getPeriodString(m.getPeriodType());
                
                csvPrinter.printRecord(
                        DATE_FORMAT.format(recordDate),
                        TIME_FORMAT.format(recordDate),
                        periodType,
                        m.getSystolic(),
                        m.getDiastolic(),
                        m.getHeartRate(),
                        m.getNote() == null ? "" : m.getNote()
                );
            }
            
            csvPrinter.flush();

            // Tell the media scanner about the new file so it's visible in file browsers
            MediaScannerConnection.scanFile(context,
                    new String[]{csvFile.toString()},
                    null,
                    null);

            return csvFile.getAbsolutePath();
        } catch (IOException e) {
            Log.e(TAG, "Error exporting data to CSV: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Convert period type code to readable string
     */
    private static String getPeriodString(int periodType) {
        switch (periodType) {
            case 0:
                return "Morning";
            case 1:
                return "Noon";
            case 2:
                return "Afternoon";
            case 3:
                return "Bedtime";
            default:
                return "Unknown";
        }
    }
} 