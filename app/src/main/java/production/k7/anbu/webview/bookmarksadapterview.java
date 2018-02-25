package production.k7.anbu.webview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class bookmarksadapterview extends ArrayAdapter<bookmark_data_structure>{
    public bookmarksadapterview(Context context, ArrayList<bookmark_data_structure> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the bookmark_data_structure item for this position
        bookmark_data_structure user = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.bookmark_support, parent, false);
        }
        // Lookup view for bookmark_data_structure population
        TextView Link = (TextView) convertView.findViewById(R.id.link);
        TextView Title = (TextView) convertView.findViewById(R.id.title);
        // Populate the bookmark_data_structure into the template view using the bookmark_data_structure object
        Link.setText(user.getLink());
        Title.setText(user.getTitle());
        // Return the completed view to render on screen
        return convertView;
    }
}
