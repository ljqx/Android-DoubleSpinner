package gait.remi.android.view;

import android.content.Context;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.List;

public abstract class BaseDoubleSpinnerAdapter {
    public static int DISABLED_POSITION = -1;
    private int secondarySelectedPrimaryPosition = -1;
    private final DataSetObservable dataSetObservable = new DataSetObservable();
    public abstract int getPrimaryCount();
    public ListAdapter getPrimaryAdapter() {
        return getSelectablePrimaryAdapter();
    }
    protected abstract SelectableListAdapter getSelectablePrimaryAdapter();

    public ListAdapter getCurrentSecondaryAdapter() {
        return getCurrentSelectableSecondaryAdapter();
    }

    protected SelectableListAdapter getCurrentSelectableSecondaryAdapter() {
        return getSelectableSecondaryAdapterSafely(getPrimarySelected());
    }

    protected abstract SelectableListAdapter getSelectableSecondaryAdapter(int position);

    private SelectableListAdapter getSelectableSecondaryAdapterSafely(int position) {
        if (position >= 0 && position < getPrimaryCount()) {
            return getSelectableSecondaryAdapter(position);
        } else
            return null;
    }

    public void registerDataSetObserver(DataSetObserver observer) {
        dataSetObservable.registerObserver(observer);
    }
    public void unregisterDataSetObserver(DataSetObserver observer) {
        dataSetObservable.unregisterObserver(observer);
    }
    public void notifyDataSetChanged() {
        dataSetObservable.notifyChanged();
    }

    public void setPrimarySelected(int position) {
        getSelectablePrimaryAdapter().setSelectedPosition(position);
        notifyDataSetChanged();
    }

    public void setSecondarySelected(int position) {
        SelectableListAdapter adapter = getSelectableSecondaryAdapterSafely(secondarySelectedPrimaryPosition);
        if (adapter != null)
            adapter.setSelectedPosition(DISABLED_POSITION);
        getCurrentSelectableSecondaryAdapter().setSelectedPosition(position);
        secondarySelectedPrimaryPosition = getPrimarySelected();
    }

    public int getPrimarySelected() {
        return getSelectablePrimaryAdapter().getSelectedPosition();
    }

    public static class SelectableListAdapter<T> extends ArrayAdapter<T> {
        private int previousPosition = DISABLED_POSITION;
        private int selectedPosition = DISABLED_POSITION;
        private static final int VIEW_TYPE_COUNT = 2;
        private static final int ITEM_DEFAULT_LAYOUT_UNSELECTED = R.layout.double_spinner_items_unselected;
        private static final int ITEM_DEFAULT_LAYOUT_SELECTED = R.layout.double_spinner_items_selected;
        private static final int ITEM_LAYOUT_INDEX_UNSELECTED = 0;
        private static final int ITEM_LAYOUT_INDEX_SELECTED = 1;
        private int[] itemLayoutResources;
        private LayoutInflater inflater;
        public SelectableListAdapter(Context context, List<T> objects) {
            this(context, ITEM_DEFAULT_LAYOUT_UNSELECTED, ITEM_DEFAULT_LAYOUT_SELECTED, objects);
        }

        public SelectableListAdapter(Context context, int resourceUnselected, int resourceSelected, List<T> objects) {
            super(context, resourceUnselected, objects);
            inflater = LayoutInflater.from(context);
            itemLayoutResources = new int[2];
            itemLayoutResources[0] = resourceUnselected;
            itemLayoutResources[1] = resourceSelected;
        }

        public void setSelectedPosition(int position) {
            if (position < getCount()) {
                previousPosition = selectedPosition;
                selectedPosition = position;
                notifyDataSetChanged();
            }
        }

        public int getSelectedPosition() {
            return selectedPosition;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null || position == selectedPosition || position == previousPosition) {
                convertView = inflater.inflate(itemLayoutResources[getItemViewType(position)], null);
            }
            ((TextView) convertView).setText(getItem(position).toString());
            return convertView;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == selectedPosition) {
                return ITEM_LAYOUT_INDEX_SELECTED;
            }
            return ITEM_LAYOUT_INDEX_UNSELECTED;
        }

        @Override
        public int getViewTypeCount() {
            return VIEW_TYPE_COUNT;
        }
    }
}
