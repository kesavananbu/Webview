package production.k7.anbu.webview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class historyadapterview extends ArrayAdapter<history_data_structure>{
    public historyadapterview(Context context, ArrayList<history_data_structure> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        history_data_structure user = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.history_support, parent, false);
        }

        TextView DandT = (TextView) convertView.findViewById(R.id.dt);
        TextView Link = (TextView) convertView.findViewById(R.id.link);
        TextView Title = (TextView) convertView.findViewById(R.id.title);
        // Populate the bookmark_data_structure into the template view using the bookmark_data_structure object
        DandT.setText(user.getDandT());
        Link.setText(user.getLink());
        Title.setText(user.getTitle());
        // Return the completed view to render on screen
        return convertView;
    }
}
