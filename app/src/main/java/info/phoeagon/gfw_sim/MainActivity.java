package info.phoeagon.gfw_sim;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.Uri;
import android.net.VpnService;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import com.shamanland.fab.FloatingActionButton;
import com.stericson.RootShell.execution.Command;
import com.stericson.RootTools.*;


import android.text.format.Formatter;

public class MainActivity extends Activity {

    final static String Q_DNS = "180.76.76.76";
    final static String LOG_TAG = "GFW_VPN";
    static GFWVpnService vpn = new GFWVpnService();
    FloatingActionButton btn = null;
    Context ctx = null;
    String dns = null;

    private void backupDNS() {
        DhcpInfo i = ((WifiManager) getSystemService(Context.WIFI_SERVICE)).getDhcpInfo();
        dns = Formatter.formatIpAddress(i.dns1);
    }
    private void configureDNS(final String _dns) {
        if (RootTools.isAccessGiven()) {
            Command command = new Command(0, "setprop net.dns1 " + _dns);
            try {
                RootTools.getShell(true).add(command);
            } catch (Exception e) {
                Log.d(LOG_TAG, "DNS Configuration Failed", e);
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        setContentView(R.layout.activity_main);
        // Set button listners
        View.OnClickListener listener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String url = (String)v.getTag();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        };
        Button b1 = (Button) this.findViewById(R.id.website_button);
        b1.setTag(getResources().getString(R.string.website));
        b1.setOnClickListener(listener);
        b1 = (Button) this.findViewById(R.id.email_button);
        b1.setTag(getResources().getString(R.string.mailto));
        b1.setOnClickListener(listener);
        // Set floating action button
        btn = (FloatingActionButton)this.findViewById(R.id.add_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vpn.isRunning()) {
                    //if ( dns != null )
                    //    configureDNS(dns);
                    vpn.onRevoke();
                    Toast.makeText(getApplicationContext(), "GFW Vpn Stopped", Toast.LENGTH_LONG).show();
                    btn.setColor(getResources().getColor(R.color.accent));
                }else {
                    //backupDNS();
                    //configureDNS(Q_DNS);
                    Intent i = VpnService.prepare(getApplicationContext());
                    if (i == null) {
                        onActivityResult(0, RESULT_OK, null);
                    }else {
                        startActivityForResult(i, 0);
                    }
                    Toast.makeText(getApplicationContext(), "GFW Vpn Started", Toast.LENGTH_LONG).show();
                    btn.setColor(getResources().getColor(R.color.accent_high));
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int request, int result, Intent data){
        if (result == RESULT_OK) {
            String prefix = getPackageName();
            final Intent i = new Intent(this, GFWVpnService.class);
            Thread t = new Thread() {
                @Override public void run(){
                    startService(i);
                }
            };
            t.start();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
