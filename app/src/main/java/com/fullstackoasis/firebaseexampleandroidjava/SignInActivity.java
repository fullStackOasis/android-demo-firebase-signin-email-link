package com.fullstackoasis.firebaseexampleandroidjava;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import androidx.annotation.NonNull;

public class SignInActivity  extends SharedPreferencesActivity implements View.OnClickListener {
    private static String TAG = SignInActivity.class.getCanonicalName();
    private FirebaseAuth firebaseAuth;
    // DEEP_LNK is a "dynamic link"
    // In durablelinks panel, it says Firebase links will start with
    // https://rvintheusa.web.app/firebasedude
    private static String DEEP_LINK = "https://rvintheusa.web.app/firebasedude";
    private Drawable puzzledPig = this.getDrawable(R.drawable.puzzled_pig);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);
        Button b = findViewById(R.id.button);
        b.setOnClickListener(this);
        firebaseAuth = FirebaseAuth.getInstance();
        callSetUpDynamicLink();

    }

    private void callSetUpDynamicLink() {
        Log.d(TAG, "callSetUpDynamicLink");
        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(getIntent())
            .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                Log.d(TAG, "callGetDynamicLink, success");
                    // Get deep link from result (may be null if no link is found)
                    Uri deepLink = null;
                    if (pendingDynamicLinkData != null) {
                        deepLink = pendingDynamicLinkData.getLink();
                    }
                    Log.d(TAG, "callGetDynamicLink, deepLink " + deepLink);
                    // The deeplink is crazy long, like this:
                    //  https://rvintheusa.firebaseapp.com/__/auth/action?
                        //  ...&mode=signIn&oobCode..&continueUrl=https://rvintheusa.web
                        //  .app/firebasedude&lang=en
                        handleSuccess();
                    }
                })
            .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "getDynamicLink:onFailure", e);
                        handleError(e.getMessage());
                    }
                }
            );
    }

    private void switchToMainActivity() {
        Log.d(TAG, "switchToMainActivity");
        Intent i = new Intent(this, MainActivity.class);
        this.startActivity(i);
    }

    private void handleError(String errorMessage) {
        Log.d(TAG, "handleError");
        setImageOnError();
        setTextOnError(errorMessage);
    }

    private void handleSuccess() {
        Log.d(TAG, "handleSuccess");
        FirebaseAuth auth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        if (intent == null || intent.getData() == null) return;
        String emailLink = intent.getData().toString();

        // Confirm the link is a sign-in with email link.
        if (auth.isSignInWithEmailLink(emailLink)) {
            // Retrieve this from wherever you stored it. Do not use the one that is sent, as
            // this could lead to session stealing.
            String email = getTemporarilySavedEmail();

            // The client SDK will parse the code from the link for you.
            auth.signInWithEmailLink(email, emailLink)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Successfully signed in with email link!");
                                AuthResult result = task.getResult();
                                // You can access the new user via result.getUser()
                                // Additional user info profile *not* available via:
                                // result.getAdditionalUserInfo().getProfile() == null
                                // You can check if the user is new or existing:
                                // result.getAdditionalUserInfo().isNewUser()
                                //setUISignedIn(result.getUser());
                                switchToMainActivity();
                            } else {
                                handleError("Could not sign in");
                                Log.e(TAG, "Error signing in with email link", task.getException());
                            }
                        }
                    });
        }
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick");
        EditText et = findViewById(R.id.editText);
        final String email = et.getText().toString();
        temporarilySaveEmail(email);
        Log.d(TAG, "onClick email? " + email);
        Log.d(TAG, "A new message to make sure recompiled");
        ActionCodeSettings actionCodeSettings = ActionCodeSettings.newBuilder()
                // URL you want to redirect back to. The domain (www.example.com) for this
                // URL must be whitelisted in the Firebase Console.
                // This MUST be called prior to use.
                .setUrl(DEEP_LINK)
                // This must be true
                .setHandleCodeInApp(true)
                //.setIOSBundleId("com.example.ios")
                .setAndroidPackageName(
                        "com.fullstackoasis.firebaseexampleandroidjava",
                        true, /* installIfNotAvailable */
                        "1"    /* minimumVersion */)
                .setDynamicLinkDomain("firebaseexampleandroidjava.page.link")
                .build();

        firebaseAuth.sendSignInLinkToEmail(email, actionCodeSettings)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "email? " + email);
                        //Void v = task.getResult();
                        //Log.d(TAG, "void is " + v);
                        boolean emailWasSent = task.isSuccessful();
                        if (emailWasSent) {
                            Log.d(TAG, "Email sent.");
                        } else {
                            Exception e = task.getException();
                            Log.d(TAG, "There was a problem");
                            Log.e(TAG, "msg ", e);
                        }
                        setText(emailWasSent);
                    }
                });

    }

    protected void setText(boolean success) {
        Log.d(TAG, "setText");
        TextView tv = findViewById(R.id.tvEmailResults);
        if (success) {
            tv.setText(getText(R.string.success_email_sent));
        } else {
            tv.setText(getText(R.string.failed_email_not_sent));
        }
    }

    protected void setImageOnError() {
        Log.d(TAG, "setImageOnError");
        ImageView iv = findViewById(R.id.imageView);
        iv.setImageDrawable(puzzledPig);
    }

    protected void setTextOnError(String errorMessage) {
        Log.d(TAG, "setImageOnError");
        TextView tv = findViewById(R.id.tvEmailResults);
        tv.setText(errorMessage);
    }

}
