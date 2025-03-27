package com.bloodpressure.utils;

import com.bloodpressure.model.Measurement;
import com.bloodpressure.model.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportGenerator {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    
    /**
     * Generate a comprehensive health report based on blood pressure measurements
     * @param user User profile
     * @param measurements List of blood pressure measurements
     * @return Report text
     */
    public static String generateReport(User user, List<Measurement> measurements) {
        if (measurements == null || measurements.isEmpty()) {
            return "No data available for analysis. Please record your blood pressure readings regularly.";
        }

        StringBuilder report = new StringBuilder();
        report.append("Blood Pressure Analysis Report\n");
        report.append("----------------------------\n\n");
        report.append("Report Date: ").append(DATE_FORMAT.format(new Date())).append("\n\n");
        
        // User information
        report.append("User Information:\n");
        report.append("Age: ").append(user.getAge()).append(" years\n");
        report.append("Gender: ").append(user.isMale() ? "Male" : "Female").append("\n");
        if (user.getHealthHistory() != null && !user.getHealthHistory().isEmpty()) {
            report.append("Health History: ").append(user.getHealthHistory()).append("\n");
        }
        if (user.getMedications() != null && !user.getMedications().isEmpty()) {
            report.append("Medications: ").append(user.getMedications()).append("\n");
        }
        report.append("\n");

        // Calculate averages
        float avgSystolic = calculateAverage(measurements, true);
        float avgDiastolic = calculateAverage(measurements, false);
        float avgHeartRate = calculateAverageHeartRate(measurements);
        
        report.append("Summary Statistics:\n");
        report.append("Average Systolic: ").append(String.format("%.1f", avgSystolic)).append(" mmHg\n");
        report.append("Average Diastolic: ").append(String.format("%.1f", avgDiastolic)).append(" mmHg\n");
        report.append("Average Heart Rate: ").append(String.format("%.1f", avgHeartRate)).append(" bpm\n\n");
        
        // Blood pressure category analysis
        report.append("Blood Pressure Category: ").append(determineBPCategory(avgSystolic, avgDiastolic)).append("\n\n");
        
        // Recommendations
        report.append("Recommendations:\n");
        generateRecommendations(report, avgSystolic, avgDiastolic, user);
        
        return report.toString();
    }
    
    /**
     * Calculate average systolic or diastolic pressure
     */
    private static float calculateAverage(List<Measurement> measurements, boolean systolic) {
        float sum = 0;
        for (Measurement m : measurements) {
            sum += (systolic ? m.getSystolic() : m.getDiastolic());
        }
        return sum / measurements.size();
    }
    
    /**
     * Calculate average heart rate
     */
    private static float calculateAverageHeartRate(List<Measurement> measurements) {
        float sum = 0;
        for (Measurement m : measurements) {
            sum += m.getHeartRate();
        }
        return sum / measurements.size();
    }
    
    /**
     * Determine blood pressure category based on JNC 8 guidelines
     */
    private static String determineBPCategory(float systolic, float diastolic) {
        if (systolic < 120 && diastolic < 80) {
            return "Normal";
        } else if ((systolic >= 120 && systolic <= 129) && diastolic < 80) {
            return "Elevated";
        } else if ((systolic >= 130 && systolic <= 139) || (diastolic >= 80 && diastolic <= 89)) {
            return "Stage 1 Hypertension";
        } else if (systolic >= 140 || diastolic >= 90) {
            return "Stage 2 Hypertension";
        } else if (systolic > 180 || diastolic > 120) {
            return "Hypertensive Crisis";
        }
        return "Unknown";
    }
    
    /**
     * Generate personalized recommendations based on blood pressure readings and user profile
     */
    private static void generateRecommendations(StringBuilder report, float systolic, float diastolic, User user) {
        String category = determineBPCategory(systolic, diastolic);
        
        // General recommendations for everyone
        report.append("• Maintain regular blood pressure monitoring\n");
        report.append("• Aim for a healthy diet low in salt and rich in fruits and vegetables\n");
        report.append("• Engage in regular physical activity (at least 150 minutes per week)\n");
        report.append("• Limit alcohol consumption\n");
        report.append("• Avoid tobacco use\n");
        
        // Specific recommendations based on BP category
        if (category.equals("Normal")) {
            report.append("• Continue your healthy lifestyle to maintain normal blood pressure\n");
        } else if (category.equals("Elevated")) {
            report.append("• Consider lifestyle modifications to prevent progression to hypertension\n");
            report.append("• Reduce sodium intake to less than 2,300mg per day\n");
        } else if (category.equals("Stage 1 Hypertension")) {
            report.append("• Consult with your healthcare provider about treatment options\n");
            report.append("• Limit sodium intake to 1,500-2,300mg per day\n");
            report.append("• Follow the DASH diet plan\n");
        } else if (category.equals("Stage 2 Hypertension") || category.equals("Hypertensive Crisis")) {
            report.append("• ⚠️ Seek medical attention promptly\n");
            report.append("• Take prescribed medications regularly\n");
            report.append("• Monitor your blood pressure daily\n");
            report.append("• Strictly adhere to sodium restriction (less than 1,500mg per day)\n");
        }
        
        // Age-specific recommendations
        if (user.getAge() >= 65) {
            report.append("• Consider consulting with a specialist in geriatric medicine\n");
            report.append("• Be cautious about orthostatic hypotension (dizziness when standing)\n");
        }
        
        // Final note
        report.append("\nThis report is for informational purposes only and does not replace professional medical advice.");
    }
} 