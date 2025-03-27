package com.bloodpressure;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bloodpressure.model.User;
import com.bloodpressure.viewmodel.UserViewModel;
import com.google.android.material.textfield.TextInputEditText;

public class ProfileActivity extends AppCompatActivity {
    private UserViewModel userViewModel;
    private TextInputEditText etName, etAge, etHealthHistory, etMedications;
    private RadioGroup rgGender;
    private RadioButton rbMale, rbFemale;
    private CheckBox cbHypertension;
    private long userId;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        
        // Show back button in action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Profile");
        }

        initializeViews();
        initializeViewModel();
        getUserIdFromIntent();
        setupSaveButton();
    }

    private void initializeViews() {
        etName = findViewById(R.id.etName);
        etAge = findViewById(R.id.etAge);
        rgGender = findViewById(R.id.rgGender);
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);
        etHealthHistory = findViewById(R.id.etHealthHistory);
        etMedications = findViewById(R.id.etMedications);
        cbHypertension = findViewById(R.id.cbHypertension);
    }

    private void initializeViewModel() {
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        
        // Observe user data changes
        userViewModel.getCurrentUser().observe(this, user -> {
            if (user != null) {
                currentUser = user;
                populateUserData(user);
            }
        });
    }

    private void getUserIdFromIntent() {
        if (getIntent() != null) {
            userId = getIntent().getLongExtra("USER_ID", -1);
            if (userId != -1) {
                userViewModel.loadUserById(userId);
            } else {
                userViewModel.loadDefaultUser();
            }
        } else {
            userViewModel.loadDefaultUser();
        }
    }

    private void populateUserData(User user) {
        etName.setText(user.getName());
        etAge.setText(String.valueOf(user.getAge()));
        
        if (user.isMale()) {
            rbMale.setChecked(true);
        } else {
            rbFemale.setChecked(true);
        }
        
        etHealthHistory.setText(user.getHealthHistory());
        etMedications.setText(user.getMedications());
        cbHypertension.setChecked(user.isHasHypertension());
    }

    private void setupSaveButton() {
        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void saveProfile() {
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String ageStr = etAge.getText() != null ? etAge.getText().toString().trim() : "";
        String healthHistory = etHealthHistory.getText() != null ? etHealthHistory.getText().toString().trim() : "";
        String medications = etMedications.getText() != null ? etMedications.getText().toString().trim() : "";
        boolean hasHypertension = cbHypertension.isChecked();
        
        // Validate inputs
        if (TextUtils.isEmpty(name)) {
            etName.setError("Please enter your name");
            return;
        }
        
        if (TextUtils.isEmpty(ageStr)) {
            etAge.setError("Please enter your age");
            return;
        }
        
        int age;
        try {
            age = Integer.parseInt(ageStr);
            if (age < 1 || age > 120) {
                etAge.setError("Please enter a valid age between 1-120");
                return;
            }
        } catch (NumberFormatException e) {
            etAge.setError("Please enter a valid number");
            return;
        }
        
        boolean isMale = rgGender.getCheckedRadioButtonId() == R.id.rbMale;
        
        if (currentUser == null) {
            // Create new user
            User newUser = new User(name, age, isMale, healthHistory, medications, hasHypertension);
            userViewModel.insert(newUser);
        } else {
            // Update existing user
            currentUser.setName(name);
            currentUser.setAge(age);
            currentUser.setMale(isMale);
            currentUser.setHealthHistory(healthHistory);
            currentUser.setMedications(medications);
            currentUser.setHasHypertension(hasHypertension);
            userViewModel.update(currentUser);
        }
        
        Toast.makeText(this, "Profile saved successfully", Toast.LENGTH_SHORT).show();
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