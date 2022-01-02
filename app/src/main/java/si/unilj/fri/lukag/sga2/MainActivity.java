package si.unilj.fri.lukag.sga2;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import si.unilj.fri.lukag.sga2.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static final int PERMISSION_REQUEST_ID = 100;
    private static final String sp_key = "permission_denial";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (askForPermissions()) {
            setUpViews();
        }
    }

    private void setUpViews() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    private boolean askForPermissions() {
        // Check for permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {
                // User has already denied the request
                showReasonForPerms("Reason because you denied", "Just to show how to handle permission requests");
            } else {
                // If shared preferences contain this key, user had previously denied request
                if (this.getPreferences(MODE_PRIVATE).contains(sp_key)) {
                    Snackbar.make(
                            findViewById(android.R.id.content),
                            "Denied permission. Change that in app info.",
                            Snackbar.LENGTH_INDEFINITE)
                            .setAction("App info", view -> {
                                        Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        i.addCategory(Intent.CATEGORY_DEFAULT);
                                        i.setData(Uri.parse("package:" + getPackageName()));
                                        startActivity(i);
                                    }
                            ).show();
                } else {
                    // Asking for the first time
                    showReasonForPerms("Hello!", "We need RECORD_AUDIO permission to be able to analyze audio");
                }
            }
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_ID) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission has been granted", Toast.LENGTH_SHORT).show();
                setUpViews();
            } else {
                // Permission denied
                // Disable the functionality that depends on this permission
                getPreferences(MODE_PRIVATE).edit().putBoolean(sp_key, true).apply();
                Toast.makeText(this, "Request denied. No fun for you", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showReasonForPerms(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, (DialogInterface dialog, int which) -> {
                    // Ask for permission
                    requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_REQUEST_ID);
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}