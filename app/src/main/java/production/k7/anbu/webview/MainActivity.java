package production.k7.anbu.webview;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    WebView webview;
    ProgressBar PB;
    Menu m;
    Boolean clearHistory=false;
    final Activity MyActivity = this;
    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 100;
   private static final int REQUEST_PERMISSION_SETTING = 101;
   private boolean sentToSettings = false;
   private SharedPreferences permissionStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webview = (WebView) findViewById(R.id.webview);
        PB=(ProgressBar)findViewById(R.id.progressbar);
        permissionStatus = getSharedPreferences("permissionStatus",MODE_PRIVATE);
        getSupportActionBar().setTitle("");
       if (savedInstanceState != null) {
            webview.restoreState(savedInstanceState);
        }
        else {
            String url = "http://www.google.com";
            if (getIntent().getExtras() != null) {
                url = getIntent().getStringExtra("url");
            }
            webview.loadUrl(url);
        }
        webviewinit();

        webview.setDownloadListener(new DownloadListener()
       {

           @Override
           public void onDownloadStart(String url, String userAgent,
                                       String contentDisposition, String mimeType,
                                       long contentLength) {
               try {
                   getper();
                   DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                   request.setMimeType(mimeType);
                   String cookies = CookieManager.getInstance().getCookie(url);
                   request.addRequestHeader("cookie", cookies);
                   request.addRequestHeader("User-Agent", userAgent);
                   request.setDescription("Downloading file...");
                   request.setTitle(URLUtil.guessFileName(url, contentDisposition,
                           mimeType));
                   request.allowScanningByMediaScanner();
                   request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                   request.setDestinationInExternalPublicDir(
                           Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(
                                   url, contentDisposition, mimeType));
                   DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                   dm.enqueue(request);
                   Toast.makeText(getApplicationContext(), "Downloading File",
                           Toast.LENGTH_LONG).show();
               }
               catch(Exception e)
                   {
                       Toast.makeText(getBaseContext(), "Storage Permission Not Granted So Downloading is Cancelled", Toast.LENGTH_SHORT).show();
                   }
           }});

   }
   public void getper()
   {

       if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
           if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
               //Show Information about why you need the permission
               AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
               builder.setTitle("Need Storage Permission");
               builder.setMessage("This app needs storage permission.");
               builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       dialog.cancel();
                       ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                   }
               });
               builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       dialog.cancel();
                   }
               });
               builder.show();
           } else if (permissionStatus.getBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE,false)) {
               //Previously Permission Request was cancelled with 'Dont Ask Again',
               // Redirect to Settings after showing Information about why you need the permission
               AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
               builder.setTitle("Need Storage Permission");
               builder.setMessage("This app needs storage permission.");
               builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       dialog.cancel();
                       sentToSettings = true;
                       Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                       Uri uri = Uri.fromParts("package", getPackageName(), null);
                       intent.setData(uri);
                       startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                       Toast.makeText(getBaseContext(), "Go to Permissions to Grant Storage", Toast.LENGTH_LONG).show();
                   }
               });
               builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       dialog.cancel();
                   }
               });
               builder.show();
           } else {
               //just request the permission
               ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
           }

           SharedPreferences.Editor editor = permissionStatus.edit();
           editor.putBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE,true);
           editor.commit();


       } else {

       }
   }
   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       super.onActivityResult(requestCode, resultCode, data);
       if (requestCode == REQUEST_PERMISSION_SETTING) {
           if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
           }
       }
   }
   private boolean isNetworkAvailable() {
       ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService( CONNECTIVITY_SERVICE );
       NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
       return activeNetworkInfo != null && activeNetworkInfo.isConnected();
   }
    public void history_add()
    {
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        String date = df.format(Calendar.getInstance().getTime());
        SharedPreferences pre = getSharedPreferences("history", 0); // 0 - for private mode
        SharedPreferences.Editor edit = pre.edit();
        Set<String> hashSet=new HashSet<>();
        hashSet.add(webview.getTitle());
        hashSet.add(webview.getUrl());
        edit.putStringSet(date,hashSet);
        edit.commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webview.saveState(outState);
    }

    public void webviewinit()
    {

        PB.setVisibility(View.VISIBLE);
        webview.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                setActionIcon(true);
                PB.setVisibility(View.VISIBLE);
                webview.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                int i = webview.getProgress();
                if (i == 100) {
                    setActionIcon(false);
                    PB.setVisibility(View.GONE);
                    history_add();
                    invalidateOptionsMenu();
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                PB.setVisibility(View.GONE);
                setActionIcon(false);
                invalidateOptionsMenu();
            }
        });
        WebSettings websettings= webview.getSettings();
        websettings.setJavaScriptEnabled(true);
        websettings.setSupportZoom(true);
        websettings.setBuiltInZoomControls(true);
        websettings.setDisplayZoomControls(false);
        websettings.setLoadWithOverviewMode(true);
        websettings.setUseWideViewPort(true);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        if (Utils.isBookmarked(this, webview.getUrl())) {
            // change icon color
            Utils.tintMenuIcon(getApplicationContext(), menu.getItem(3), R.color.colorAccent);
        } else {
            Utils.tintMenuIcon(getApplicationContext(), menu.getItem(3), android.R.color.white);
        }
        m=menu;
        return super.onCreateOptionsMenu(menu);
    }


    private void setActionIcon(boolean f)
    {
        MenuItem item=m.findItem(R.id.refresh);
        if(f) {
            item.setIcon(R.drawable.cancel);
        }
        else {
            item.setIcon(R.drawable.refresh);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (!webview.canGoBack()) {
            menu.getItem(4).setEnabled(false);
            menu.getItem(4).getIcon().setAlpha(130);
        } else {
            menu.getItem(4).setEnabled(true);
            menu.getItem(4).getIcon().setAlpha(255);
        }

        if (!webview.canGoForward()) {
            menu.getItem(5).setEnabled(false);
            menu.getItem(5).getIcon().setAlpha(130);
        } else {
            menu.getItem(5).setEnabled(true);
            menu.getItem(5).getIcon().setAlpha(255);
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        if (webview.canGoBack()) {
            webview.goBack();
        }
        else {

            exit();
        }
    }

    public void exit()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure to exit ?");
                alertDialogBuilder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                webview.clearCache(true);
                                webview.clearHistory();
                                System.exit(0);
                            }
                        });

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        if (item.getItemId() == R.id.action_bookmark) {
            // bookmark / unbookmark the url
            Utils.bookmarkUrl(this,webview.getUrl(),webview.getTitle().trim());
            //Utils.bookmarkUrl(this, webview.getUrl());

            String msg = Utils.isBookmarked(this, webview.getUrl()) ?
                    webview.getTitle() + " is Bookmarked!" :
                    webview.getTitle() + " removed!";
            Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
            // refresh the toolbar icons, so that bookmark icon color changes
            // depending on bookmark status
            invalidateOptionsMenu();
        }
        if (item.getItemId() == R.id.action_back) {
            back();
        }

        if (item.getItemId() == R.id.action_forward) {
            forward();
        }
        if(item.getItemId()==R.id.exit)
        {
            exit();
        }
        if(item.getItemId()==R.id.print)
        {
            createWebPrintJob(webview);
        }
        if(item.getItemId()==R.id.show_bookmark)
        {
            startActivity(new Intent(this,bookmarks.class));
        }
        if(item.getItemId()==R.id.history)
        {
            startActivity(new Intent(this,history.class));
        }
        if(item.getItemId()==R.id.home)
        {
            Intent in=new Intent(MainActivity.this,bookmarks.class);
            in.putExtra("url","http://google.co.in");
            in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(in);
        }
        if(item.getItemId()==R.id.refresh) {
                Drawable d=item.getIcon();
                Drawable c=m.getItem(2).getIcon();
            if (d==c){
                webview.reload();
                PB.setVisibility(View.VISIBLE);
            } else
            {
                webview.stopLoading();
                setActionIcon(true);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void back() {
        if (webview.canGoBack()) {
            webview.goBack();
        }
    }

    private void forward() {
        if (webview.canGoForward()) {
            webview.goForward();
        }
    }
    private void createWebPrintJob(WebView webView) {

        PrintManager printManager = (PrintManager) this
                .getSystemService(Context.PRINT_SERVICE);
        PrintDocumentAdapter printAdapter =
                webView.createPrintDocumentAdapter();
        String jobName = getString(R.string.app_name) +
                " Print Test";
        printManager.print(jobName, printAdapter,
                new PrintAttributes.Builder().build());
    }


}
