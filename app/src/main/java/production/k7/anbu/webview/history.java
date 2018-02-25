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
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class history extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        final ListView list = (ListView) findViewById(R.id.listhist);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("history", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        ArrayList<history_data_structure> dat = new ArrayList<history_data_structure>();
        Map values = pref.getAll();
        Set keySet = values.keySet();
        Iterator iterator = keySet.iterator();
        while (iterator.hasNext()){
            history_data_structure d = new history_data_structure();
            String key = (String) iterator.next();
            Set s= (Set) values.get(key);
            Object[] b = s.toArray();
            String url=String.valueOf(b[0]);
            String title=String.valueOf(b[1]);
            d.setDandT(key);
            if(url.contains("https://"))
            {
                d.setLink(url);
                d.setTitle(title);
            }
            else
            {
                d.setLink(title);
                d.setTitle(url);
            }
            dat.add(d);
        }
        Collections.sort(dat);
        Collections.reverse(dat);
        historyadapterview adapter = new historyadapterview(this,dat);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(getBaseContext(),"Clicked",Toast.LENGTH_SHORT).show();
                TextView t=(TextView)view.findViewById(R.id.link);
                String s= (String) t.getText();
                Intent in=new Intent(history.this,MainActivity.class);
                in.putExtra("url",s);
                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(in);
            }
        });

    }
}
