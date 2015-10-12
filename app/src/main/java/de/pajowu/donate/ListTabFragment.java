package de.pajowu.donate;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.support.v7.widget.RecyclerView;
import java.util.ArrayList;
import android.support.v7.widget.LinearLayoutManager;
/**
 * A simple {@link Fragment} subclass.
 */
public class ListTabFragment extends Fragment{
    public ArrayList<ListItem> arrayList;
    public long primaryKeyValue;
    public String title;
    public ListTabFragment(ArrayList<ListItem> arli, String ttl) {
        arrayList = arli;
        title = ttl;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onPause() {
        super.onPause();
        Log.d("MainActivity","onPause");
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d("MainActivity","onResume");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("MainActivity","onCreateView ListTabFragment");
        // Inflate the layout for this fragment
        View viewRoot = inflater.inflate(R.layout.fragment_faqfragment, container, false);

        RecyclerView listView = (RecyclerView) viewRoot.findViewById(R.id.cardList);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        listView.setLayoutManager(llm);

        ListAdapter listAdapter = new ListAdapter(getActivity(), R.layout.list_layout, this.arrayList);

        Log.d("R.layout.list_layout = ", "" + R.layout.list_layout);
        Log.d("R.layout.list_layout = ", "" + R.layout.list_layout);

        listView.setAdapter(listAdapter);
        listView.setHasFixedSize(true);

        return viewRoot;
    }

    public String getTitle() {
        return title;
    }
}
