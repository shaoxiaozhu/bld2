# Blood Pressure Manager

A comprehensive Android application for tracking and analyzing blood pressure measurements.

## Features

- **Easy Data Entry**: Record blood pressure readings at different times of the day (morning, noon, afternoon, and bedtime)
- **Visual Analytics**: View blood pressure trends with interactive charts
- **Detailed Reports**: Generate comprehensive health reports based on your readings
- **Data Management**: Export your blood pressure data as CSV files for sharing with healthcare providers
- **User Profiles**: Create and manage multiple user profiles with health history information

## Technical Details

- **Architecture**: MVVM (Model-View-ViewModel)
- **Database**: Room + SQLite for efficient local data storage
- **Charts**: MPAndroidChart for interactive blood pressure visualization
- **Minimum Android Version**: Android 5.0 (Lollipop)

## Getting Started

### Prerequisites

- Android Studio 4.0+
- Android SDK with minimum API level 21

### Installation

1. Clone the repository
```
git clone https://github.com/yourusername/blood-pressure-manager.git
```

2. Open the project in Android Studio

3. Build and run the application on your device or emulator

## Usage

1. **Create Profile**: Set up your profile with basic health information
2. **Record Measurements**: Tap on the time period buttons to record your blood pressure
3. **View Trends**: Check the graph to see how your blood pressure changes over time
4. **Export Data**: Use the Export button to save your data as a CSV file

## Screenshots

[Screenshots will be added here]

## Data Privacy

Blood Pressure Manager stores all your health data locally on your device. No data is sent to external servers or shared with third parties.

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart) for the chart library
- [Apache Commons CSV](https://commons.apache.org/proper/commons-csv/) for CSV export functionality