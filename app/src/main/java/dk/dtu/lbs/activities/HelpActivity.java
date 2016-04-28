package dk.dtu.lbs.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import dk.dtu.lbs.utils.AppUtil;
import dk.dtu.lbs.activities.R;

public class HelpActivity extends BaseActivity {
    private WebView browser=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_webview_help);
        browser=(WebView)findViewById(R.id.helpWebView);
        browser.loadUrl("file:///android_asset/html/help.html");
    }
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.removeItem(R.id.action_help);
        return super.onPrepareOptionsMenu(menu);
    }

}
