package com.example.unizuluitsenabler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity {
    Button registerbtn;
    EditText email, password, firstname, surname, phone, Identity, Passport, initials, studentno, employeeno, year;
    FirebaseAuth auth;
    ArrayAdapter<CharSequence> TittleAdapter, RegistrantAdapter, IdentificationAdapter, ResidentAdapter, DegreeTypeAdapter;
    private static final int CAMERA_PERMISSION_REQUEST = 101;
    private static final int CAMERA_REQUEST_CODE = 100;

    ProgressBar progressBar;
    ImageView profile;
    private LinearLayout student, employee;

    FirebaseFirestore firestore;
    String userId, SelectedTittle, SelectedRegistrant, SelectedIdentification, SelectedResident, SelectedDegree;
    Spinner SpinnerTittle, SpinnerIdentification, SpinnerRegistrant, SpinnerResident, SpinnerDegreeType;
    Matcher mobileMatcher;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        email = findViewById(R.id.editTextemail);
        firstname = findViewById(R.id.editTextfirstname);
        surname = findViewById(R.id.editTextsurname);
        initials = findViewById(R.id.editTextinitials);
        employeeno = findViewById(R.id.editTextemployeeno);
        studentno = findViewById(R.id.editTextstudentno);
        year = findViewById(R.id.editTextyear);
        password = findViewById(R.id.editTextpassword);
        Identity = findViewById(R.id.editTextId);
        Passport = findViewById(R.id.editTextPassport);
        student = findViewById(R.id.student);
        employee = findViewById(R.id.employee);
        SpinnerIdentification = findViewById(R.id.spinnerIdentification);
        SpinnerIdentification.setBackgroundResource(R.drawable.shape);
        SpinnerRegistrant = findViewById(R.id.spinnerRegistrant);
        SpinnerRegistrant.setBackgroundResource(R.drawable.shape);
        SpinnerResident = findViewById(R.id.spinnerRes);
        SpinnerResident.setBackgroundResource(R.drawable.shape);
        SpinnerDegreeType = findViewById(R.id.spinnerDegreetype);
        SpinnerDegreeType.setBackgroundResource(R.drawable.shape);
        profile = findViewById(R.id.image);
        SpinnerTittle = findViewById(R.id.spinnerTittle);
        SpinnerTittle.setBackgroundResource(R.drawable.shape);
        phone = findViewById(R.id.editTextphone);
        registerbtn = findViewById(R.id.registerbtn);
        progressBar = findViewById(R.id.progressBar);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();


        profile.setOnClickListener(view ->

        {
            if (ContextCompat.checkSelfPermission(RegistrationActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(RegistrationActivity.this, new String[]{android.Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
            } else {
                openCamera();
            }
        });

        Spinners();

        Map<String, Object> user = new HashMap<>();


        registerbtn.setOnClickListener(v -> {
            String Email = email.getText().toString();
            String Password = password.getText().toString();
            String Firstname = firstname.getText().toString();
            String Surname = surname.getText().toString();
            String Initials = initials.getText().toString();
            String Year = year.getText().toString();
            String Phone = phone.getText().toString();
            String phoneReguExp = "0[6-8][0-9]{8}";
            String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!*_])[a-zA-Z0-9@#$%^&+=!*_]{8,}$";
            String SelectedTittle = SpinnerTittle.getSelectedItem().toString();
            String SelectedIdentification = SpinnerIdentification.getSelectedItem().toString();
            String SelectedRegistrant = SpinnerRegistrant.getSelectedItem().toString();
            Pattern mobilepattern = Pattern.compile(phoneReguExp);
            mobileMatcher = mobilepattern.matcher(Phone);
            Pattern passwordPattern = Pattern.compile(passwordRegex);
            Matcher passwordMatcher = passwordPattern.matcher(Password);

            String identificationValue = "";



            if (SelectedIdentification.equals("Identity Number")) {
                identificationValue = Identity.getText().toString();
            } else if (SelectedIdentification.equals("Passport Number")) {
                identificationValue = Passport.getText().toString();
            }

            if (SelectedRegistrant.equals("Student")) {
                SelectedRegistrant = "isStudent";
                String Studentno = studentno.getText().toString();
                String SelectedResident = SpinnerResident.getSelectedItem().toString();
                String SelectedDegree = SpinnerDegreeType.getSelectedItem().toString();
                user.put("studentno", Studentno);
                user.put("res", SelectedResident);
                user.put("DegreeType", SelectedDegree);
                if (SelectedResident.equals("Select Your Resident")) {
                    Toast.makeText(RegistrationActivity.this, "Please select your resident", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            else if (SelectedRegistrant.equals("Employee")) {
                SelectedRegistrant = "isEmployee";
                String Employeeno = employeeno.getText().toString();
                user.put("employeeno", Employeeno);
            }
            if (SelectedTittle.equals("Select Your Identification Type")) {
                Toast.makeText(RegistrationActivity.this, "Please select your identity", Toast.LENGTH_SHORT).show();
                return;
            }
            if (SelectedRegistrant.equals("Select your category")) {
                Toast.makeText(RegistrationActivity.this, "Please select user type", Toast.LENGTH_SHORT).show();
                return;
            }

            if (Firstname.isEmpty()) {
                firstname.setError("name is required");
                firstname.requestFocus();
                return;
            }
            if (SelectedTittle.equals("Select Your Title")) {
                Toast.makeText(RegistrationActivity.this, "Please select a title", Toast.LENGTH_SHORT).show();
                return;
            }
            if (Surname.isEmpty()) {
                surname.setError("surname is required");
                surname.requestFocus();
                return;
            }
            if (Initials.isEmpty()) {
                initials.setError("initials is required");
                initials.requestFocus();
                return;
            }
            if (Email.isEmpty()) {
                email.setError("Enter an email");
                email.requestFocus();
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
                email.setError("Enter the valid email address");
                email.requestFocus();
                return;
            }
            if (Password.isEmpty()) {
                password.setError("create password");
                password.requestFocus();
                return;
            }
            if (!passwordMatcher.matches()) {
                password.setError("Weak password! Password should meet the criteria");
                password.requestFocus();
                return;
            }
            if (Phone.isEmpty()) {
                phone.setError("Contact_number is required");
                phone.requestFocus();
                return;
            }
            if (phone.length() != 10) {
                phone.setError("Contact_number should be 10 digits");
                phone.requestFocus();
                return;
            }
            if (!mobileMatcher.find()) {
                phone.setError("invalid phone number");
                phone.requestFocus();
                return;
            }
            if (Year.isEmpty()) {
                year.setError("year required");
                year.requestFocus();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            String finalIdentificationValue = identificationValue;
            String finalSelectedRegistrant = SelectedRegistrant;
            auth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(RegistrationActivity.this, "You are successfully Registered", Toast.LENGTH_SHORT).show();
                    userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DocumentReference documentReference = firestore.collection("users").document(userId);
                    user.put("firstname", Firstname);
                    user.put("surname", Surname);
                    user.put("initials", Initials);
                    user.put("tittle", SelectedTittle);
                    user.put("year", Year);
                    user.put("email", Email);
                    user.put("phone", Long.parseLong(Phone));
                    user.put(SelectedIdentification, finalIdentificationValue);
                    user.put(finalSelectedRegistrant, finalSelectedRegistrant);


                    ((DocumentReference) documentReference).set(user)
                            .addOnSuccessListener(aVoid -> {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(RegistrationActivity.this, "Registrant profile saved", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(RegistrationActivity.this, "Registrant profile not saved", Toast.LENGTH_SHORT).show();
                                Log.e("FirestoreError", "Error saving user details to Firestore", e);
                            });
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(RegistrationActivity.this, "User registration failed", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void Spinners() {
        TittleAdapter = ArrayAdapter.createFromResource(this, R.array.Tittle, R.layout.spinner_layout);
        IdentificationAdapter = ArrayAdapter.createFromResource(this, R.array.array_identification, R.layout.spinner_layout);
        RegistrantAdapter = ArrayAdapter.createFromResource(this, R.array.Registrant, R.layout.spinner_layout);
        ResidentAdapter = ArrayAdapter.createFromResource(this, R.array.Resident, R.layout.spinner_layout);
        DegreeTypeAdapter = ArrayAdapter.createFromResource(this, R.array.DegreeType, R.layout.spinner_layout);


        TittleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        IdentificationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        RegistrantAdapter = ArrayAdapter.createFromResource(this, R.array.Registrant, R.layout.spinner_layout);
        ResidentAdapter = ArrayAdapter.createFromResource(this, R.array.Resident, R.layout.spinner_layout);
        DegreeTypeAdapter = ArrayAdapter.createFromResource(this, R.array.DegreeType, R.layout.spinner_layout);


        SpinnerTittle.setAdapter(TittleAdapter);
        SpinnerIdentification.setAdapter(IdentificationAdapter);
        SpinnerRegistrant.setAdapter(RegistrantAdapter);
        SpinnerResident.setAdapter(ResidentAdapter);
        SpinnerDegreeType.setAdapter(DegreeTypeAdapter);


        SpinnerTittle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SelectedTittle = SpinnerTittle.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        SpinnerIdentification.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SelectedIdentification = SpinnerIdentification.getSelectedItem().toString();
                if (SelectedIdentification.equals("Identity Number")) {
                    Identity.setVisibility(View.VISIBLE);
                    Passport.setVisibility(View.GONE);
                } else if (SelectedIdentification.equals("Passport Number")) {
                    Identity.setVisibility(View.GONE);
                    Passport.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        SpinnerRegistrant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SelectedRegistrant = adapterView.getItemAtPosition(i).toString();
                Log.d("RegistrationActivity", "SelectedRegistrant: " + SelectedRegistrant);
                if (SelectedRegistrant.equals("Student")) {
                    student.setVisibility(View.VISIBLE);
                    studentno.setVisibility(View.VISIBLE);
                    SpinnerResident.setVisibility(View.VISIBLE);
                    SpinnerDegreeType.setVisibility(View.VISIBLE);
                    employee.setVisibility(View.GONE);
                } else if (SelectedRegistrant.equals("Employee")) {
                    employee.setVisibility(View.VISIBLE);
                    employeeno.setVisibility(View.VISIBLE);
                    student.setVisibility(View.GONE);
                } else {
                    student.setVisibility(View.GONE);
                    employee.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        SpinnerResident.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SelectedResident = SpinnerResident.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        SpinnerDegreeType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SelectedDegree = SpinnerDegreeType.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void openCamera() {
        Intent open_camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(open_camera, CAMERA_REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            profile.setImageBitmap(photo);

            byte[] byteArray = bitmapToByteArray(photo);
            Photo(byteArray);
        }

    }

    private void Photo(byte[] byteArray) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(RegistrationActivity.this, "No current user", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        DocumentReference userDocRef = firestore.collection("users").document(userId);

        String base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT);

        Map<String, Object> photoMap = new HashMap<>();
        photoMap.put("profilePhoto", base64Image);

        userDocRef.update(photoMap)
                .addOnSuccessListener(aVoid -> Toast.makeText(RegistrationActivity.this, "Photo uploaded successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> {
                    Toast.makeText(RegistrationActivity.this, "Failed to upload photo", Toast.LENGTH_SHORT).show();
                    Log.e("FirestoreError", "Error uploading photo to Firestore", e);
                });
    }

    private byte[] bitmapToByteArray(Bitmap photo) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
}