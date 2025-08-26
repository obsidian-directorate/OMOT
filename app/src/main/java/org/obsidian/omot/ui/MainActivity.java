package org.obsidian.omot.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.obsidian.omot.R;
import org.obsidian.omot.core.util.Logs;
import org.obsidian.omot.ui.fragments.CommsFragment;
import org.obsidian.omot.ui.fragments.ConsoleFragment;
import org.obsidian.omot.ui.fragments.DossiersFragment;
import org.obsidian.omot.ui.fragments.HomeFragment;
import org.obsidian.omot.ui.fragments.MissionsFragment;
import org.obsidian.omot.ui.fragments.ToolsFragment;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView nav;
    private String agentId;
    private String clearance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nav = findViewById(R.id.bottom_nav);

        // Gate access by clearance
        applyClearanceRules(nav, clearance);

        // Tab navigation
        nav.setOnItemSelectedListener(item -> {
            Fragment frag = null;
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                frag = new HomeFragment();
            } else if (id == R.id.nav_missions) {
                frag = new MissionsFragment();
            } else if (id == R.id.nav_dossiers) {
                frag = new DossiersFragment();
            } else if (id == R.id.nav_comms) {
                frag = new CommsFragment();
            } else if (id == R.id.nav_tools) {
                frag = new ToolsFragment();
            }

            if (frag != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, frag)
                        .commit();
                // Log navigation
                Logs.write(agentId, "NAVIGATE", "Opened " + getResources().getResourceEntryName(id));
                return true;
            }
            return false;
        });

        // Load default fragment
        nav.setSelectedItemId(R.id.nav_home);

        // Toolbar console menu (OMEGA only)
        MaterialToolbar toolbar = findViewById(R.id.top_toolbar);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_console) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, new ConsoleFragment())
                        .addToBackStack(null)
                        .commit();
                Logs.write(agentId, "CONSOLE_ACCESS", "Console fragment opened");
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_nav_menu, menu);
        // Hide console by default
        menu.findItem(R.id.action_console).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_console) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, new ConsoleFragment())
                    .addToBackStack(null)
                    .commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void applyClearanceRules(BottomNavigationView nav, String clearance) {
        // Hide everything first
        nav.getMenu().findItem(R.id.nav_dossiers).setVisible(false);
        nav.getMenu().findItem(R.id.nav_comms).setVisible(false);
        nav.getMenu().findItem(R.id.nav_tools).setVisible(false);

        switch (clearance) {
            case "BETA":
                // Only Home + Missions visible
                break;
            case "ALPHA":
                nav.getMenu().findItem(R.id.nav_dossiers).setVisible(true);
                break;
            case "OMEGA":
                nav.getMenu().findItem(R.id.nav_dossiers).setVisible(true);
                nav.getMenu().findItem(R.id.nav_comms).setVisible(true);
                nav.getMenu().findItem(R.id.nav_tools).setVisible(true);

                // Console appears via toolbar menu
                MaterialToolbar toolbar = findViewById(R.id.top_toolbar);
                toolbar.getMenu().findItem(R.id.action_console).setVisible(true);
                break;
        }
    }
}