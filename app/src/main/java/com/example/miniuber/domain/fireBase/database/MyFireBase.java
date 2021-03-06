
package com.example.miniuber.domain.fireBase.database;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.miniuber.app.features.driverFeatures.DriversMapsActivity;
import com.example.miniuber.app.features.employeeFeatures.EmployeeActivity;
import com.example.miniuber.app.features.riderFeatures.riderMapsActivity.RiderMapsActivity;

import com.example.miniuber.entities.ModuleOption;
import com.example.miniuber.entities.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyFireBase {
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private  PhoneAuthCredential credential;
    private final Context context;
    private final int moduleOption;

    public MyFireBase(PhoneAuthCredential credential, Context context, int moduleOption) {
        this.credential = credential;
        this.context = context;
        this.moduleOption = moduleOption;
    }


    public void signInUser(String phoneNo) {

        auth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                handleIntent(phoneNo);

            } else {
                //verification unsuccessful.. display an error message
                handleFailedVerification(task);
            }

        });
    }

    public void createUserAccount(User user) {

        auth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                addUserDataToDatabase(user);
                handleIntent(user.getPhoneNumber());

            } else {
                //verification unsuccessful.. display an error message

                handleFailedVerification(task);

            }

        });

    }

    public  void addUserDataToDatabase(User user) {

        DatabaseReference myRef =
                FirebaseDatabase.getInstance().getReference("Users").child(ModuleOption.getReferenceName(moduleOption));
        String userID = auth.getCurrentUser().getUid();
        //user.setUserId(userID);
        myRef.push().setValue(user);
    }

    private void handleIntent(String phoneNo) {

        if(moduleOption == ModuleOption.EMPLOYEE){
            Intent intent  = new Intent(context, EmployeeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        }
        else if(moduleOption == ModuleOption.RIDER){
            Intent intent  = new Intent(context, RiderMapsActivity.class);
            intent.putExtra("phoneNumber",phoneNo);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);

        }
        else if (moduleOption == ModuleOption.DRIVER){
            Intent intent  = new Intent(context, DriversMapsActivity.class);
            intent.putExtra("phoneNumber",phoneNo);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        }


    }

    private void handleFailedVerification(Task<AuthResult> task) {
        String message = "Something is wrong, we will fix it soon...";

        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
            message = "Invalid code entered...";
        }

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


}