package production.k7.anbu.webview;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class bookmarks extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);
        final ListView list = (ListView) findViewById(R.id.list);
        if(getIntent().getExtras() != null) {
            Intent in=new Intent(bookmarks.this,MainActivity.class);
            in.putExtra("url","http://google.co.in");
            in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(in);
        }
            SharedPreferences pref = getApplicationContext().getSharedPreferences("bookmark", 0); // 0 - for private mode
            SharedPreferences.Editor editor = pref.edit();
            ArrayList<bookmark_data_structure> dat = new ArrayList<bookmark_data_structure>();
            Map values = pref.getAll();
                Set keySet = values.keySet();
                Iterator iterator = keySet.iterator();
                    while (iterator.hasNext()) {
                    bookmark_data_structure d = new bookmark_data_structure();
                    String key = (String) iterator.next();
                    String value= (String) values.get(key);
                    d.setLink(key);
                    d.setTitle(value);
                    dat.add(d);
                }
        bookmarksadapterview adapter = new bookmarksadapterview(this,dat);
                    list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(getBaseContext(),"Clicked",Toast.LENGTH_SHORT).show();
                TextView t=(TextView)view.findViewById(R.id.link);
                String s= (String) t.getText();
                Intent in=new Intent(bookmarks.this,MainActivity.class);
                in.putExtra("url",s);
                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(in);
            }
        });
    }
}
