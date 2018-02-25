package production.k7.anbu.webview;

import android.support.annotation.NonNull;

public class history_data_structure implements Comparable<history_data_structure>
{

    private String DandT;
    private String Link;
    private String Title;

    public String getDandT() {
        return DandT;
    }

    public void setDandT(String dandT) {
        DandT = dandT;
    }
    public String getLink() {
        return Link;
    }

    public void setLink(String link) {
        Link = link;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }


    @Override
    public int compareTo(@NonNull history_data_structure o) {
        return DandT.compareTo(o.DandT);
    }
}
