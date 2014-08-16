package gait.remi.android.view.demo;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import gait.remi.android.view.BaseDoubleSpinnerAdapter;

public class DoubleSpinnerAdapter extends BaseDoubleSpinnerAdapter {
    private List<String> primaryData;
    private List<List<String>> secondaryData;
    private SelectableListAdapter primaryAdapter;
    private List<SelectableListAdapter> secondaryAdapters;

    private Context context;
    public DoubleSpinnerAdapter(Context context, List<String> primaryData, List<List<String>> secondaryData) {
        this.context = context;
        if (primaryData == null && secondaryData == null) {
            primaryData = new ArrayList<String>();
            primaryData.add("first");
            primaryData.add("second");
            primaryData.add("third");

            secondaryData = new ArrayList<List<String>>();
            List<String> children1 = new ArrayList<String>();
            children1.add("children1-1");
            children1.add("children1-2");
            secondaryData.add(children1);
            List<String> children2 = new ArrayList<String>();
            children2.add("children2-1");
            children2.add("children2-2");
            secondaryData.add(children2);
            List<String> children3 = new ArrayList<String>();
            children3.add("children3-1");
            children3.add("children3-2");
            secondaryData.add(children3);
        }
        this.primaryData = primaryData;
        this.secondaryData = secondaryData;

        primaryAdapter = new SelectableListAdapter<String>(
                context,
                R.layout.double_spinner_primary_items_unselected,
                R.layout.double_spinner_primary_items_selected,
                primaryData);

        secondaryAdapters = new ArrayList<SelectableListAdapter>();
        for (int i = 0; i < getPrimaryCount(); i++) {
            secondaryAdapters.add(new SelectableListAdapter<String>(
                    context,
                    secondaryData.get(i)
            ));
        }
    }

    @Override
    public int getPrimaryCount() {
        return primaryData.size();
    }

    @Override
    protected SelectableListAdapter getSelectablePrimaryAdapter() {
        return primaryAdapter;
    }

    @Override
    protected SelectableListAdapter getSelectableSecondaryAdapter(int position) {
        return secondaryAdapters.get(position);
    }

}
