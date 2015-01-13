package info.phoeagon.gfw_sim;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.VpnService;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;


public class MainActivity extends Activity {

    static GFWVpnService vpn = new GFWVpnService();
    Context ctx = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        setContentView(R.layout.activity_main);
        ImageButton btn = (ImageButton)this.findViewById(R.id.add_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vpn.isRunning()) {
                    vpn.onRevoke();
                    Toast.makeText(getApplicationContext(), "GFW Vpn Stopped", Toast.LENGTH_SHORT).show();
                }else {
                    Intent i = VpnService.prepare(getApplicationContext());
                    if (i == null) {
                        onActivityResult(0, RESULT_OK, null);
                    }else {
                        startActivityForResult(i, 0);
                    }
                    Toast.makeText(getApplicationContext(), "GFW Vpn Started", Toast.LENGTH_SHORT).show();
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
