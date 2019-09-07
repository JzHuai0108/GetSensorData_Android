package es.csic.getsensordata

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log

class LaunchActivity : AppCompatActivity() {

    companion object {
        private val TAG = LaunchActivity::class.qualifiedName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)

        var areThereMissingPermissions = false

        for (requiredPermission in PermissionsActivity.requiredPermissions) {
            if (ActivityCompat.checkSelfPermission(this, requiredPermission) != PackageManager.PERMISSION_GRANTED) {
                areThereMissingPermissions = true
                break
            }
        }
        val intent = if (areThereMissingPermissions) {
            Log.d(TAG, "there are missing permissions")
            Intent(this, PermissionsActivity::class.java)
        } else {
            Log.d(TAG, "there are not missing permissions")
            Intent(this, MainActivity::class.java)
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        finish()
        startActivity(intent)
    }
}
