package com.owlab.callblocker;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Switch;

import com.owlab.callblocker.fragment.AddByManualDialogFragment;
import com.owlab.callblocker.fragment.AddFromCallLogFragment;
import com.owlab.callblocker.fragment.AddFromContactsFragment;
import com.owlab.callblocker.fragment.SettingsFragment;
import com.owlab.callblocker.fragment.ViewPagerContainerFragment;

/**
 * Top most setting element is "SERVICE ON/OFF"
 * - If service off then no filtering activity occurs and no notification icon of this app enabled
 * - If service on then filtering occurs
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize app
        FUNS.initializeApp(this);


        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(PermissionChecker.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                FUNS.showMessageWithOKCancel(
                        this,
                        "This App need REAT CONTACTS permission to view blocked calls",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, CONS.REQUEST_CODE_ASK_PERMISSION_FOR_READ_BLOCKED_CALLS);
                            }
                        },
                        null);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, CONS.REQUEST_CODE_ASK_PERMISSION_FOR_READ_BLOCKED_CALLS);
            }
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ViewPagerContainerFragment(), CONS.FRAGMENT_VIEW_PAGER_CONTAINER).commit();
        }
    }

    private Menu menu;

    public Menu getMenu() {
        return this.menu;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        MenuItem mainOnOffSwitchLayout = menu.findItem(R.id.menuitem_main_onoff_switch_layout);
        Switch mainOnOffSwitch = (Switch) mainOnOffSwitchLayout.getActionView().findViewById(R.id.action_main_onoff_switch);

        mainOnOffSwitch.setChecked(sharedPreferences.getBoolean(getString(R.string.pref_key_blocking_on), false));
        mainOnOffSwitch.setOnCheckedChangeListener(new FUNS.BlockingSwitchChangeListener(this));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.menuitem_settings:
                SettingsFragment settingsFragment = (SettingsFragment) getSupportFragmentManager().findFragmentByTag(CONS.FRAGMENT_SETTINGS);
                if (settingsFragment == null) {
                    settingsFragment = new SettingsFragment();

                }
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, settingsFragment, CONS.FRAGMENT_SETTINGS)
                        .addToBackStack(null)
                        .commit();
                return true;
            case android.R.id.home:
                getSupportFragmentManager().popBackStack();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //delegate
        FUNS.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, ">>>>> back pressed");

        super.onBackPressed();
    }

    public void setMainOnOffSwitch(boolean checked) {
        MenuItem mainOnOffSwitchLayout = menu.findItem(R.id.menuitem_main_onoff_switch_layout);
        Switch mainOnOffSwitch = (Switch) mainOnOffSwitchLayout.getActionView().findViewById(R.id.action_main_onoff_switch);

        mainOnOffSwitch.setChecked(checked);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CONS.REQUEST_CODE_ADD_SOURCE_SELECTION) {
            if(resultCode == RESULT_OK) {
                Log.d(TAG, ">>>>> result ok received from add source selection activity");
                String targetFragment = data.getStringExtra(CONS.INTENT_KEY_TARGET_FRAGMENT);
                Log.d(TAG, ">>>>> target fragment: " + targetFragment);
                if(CONS.FRAGMENT_CALL_LOG.equals(targetFragment)) {
                    getSupportFragmentManager().beginTransaction()
                            .addToBackStack(CONS.FRAGMENT_VIEW_PAGER_CONTAINER)
                            //.replace(R.id.fragment_container, new AddFromCallLogFragment(), CONS.FRAGMENT_CALL_LOG).commitAllowingStateLoss();
                            .replace(R.id.fragment_container, new AddFromCallLogFragment(), CONS.FRAGMENT_CALL_LOG).commit();
                } else if(CONS.FRAGMENT_CONTACTS.equals(targetFragment)) {
                    getSupportFragmentManager().beginTransaction()
                            .addToBackStack(CONS.FRAGMENT_VIEW_PAGER_CONTAINER)
                            //.replace(R.id.fragment_container, new AddFromContactsFragment(), CONS.FRAGMENT_CONTACTS).commitAllowingStateLoss();
                            .replace(R.id.fragment_container, new AddFromContactsFragment(), CONS.FRAGMENT_CONTACTS).commit();
                } else if(CONS.FRAGMENT_ADD_BY_MANUAL.equals(targetFragment)) {
                    Fragment viewPagerContainer = getSupportFragmentManager().findFragmentByTag(CONS.FRAGMENT_VIEW_PAGER_CONTAINER);
                    Fragment currentPageFragment = ((ViewPagerContainerFragment)viewPagerContainer).getCurrentPageFragment();
                    Log.d(TAG, ">>> fragment found: " + (viewPagerContainer == null ? null : viewPagerContainer.toString()));
                    DialogFragment addByManualDialogFragment = new AddByManualDialogFragment();
                    //addByManualDialogFragment.setTargetFragment(viewPagerContainer, 0);
                    addByManualDialogFragment.setTargetFragment(currentPageFragment != null ? currentPageFragment: viewPagerContainer, 0);
                    addByManualDialogFragment.show(getSupportFragmentManager(), "ADD_BY_MANUAL_DIALOG");
                }
            } else if(resultCode == RESULT_CANCELED) {
                Log.d(TAG, ">>>>> result canceled received");

            } else if(resultCode == RESULT_FIRST_USER) {
                //TODO what is this?
            }
        }
    }

    /**
     * Because of bug in support package
     * Without this override, the above onActivityResult will result in exceptions!
     * http://stackoverflow.com/questions/7575921/illegalstateexception-can-not-perform-this-action-after-onsaveinstancestate-wit
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        //super.onSaveInstanceState(outState);
    }

}
