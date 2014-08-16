package gait.remi.android.view;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class DoubleSpinner extends Button {
    Context context;
    DoubleListView doubleListView;
    PopupWindow popup;
    OnSelectedListener onSelectedListener = null;
    public DoubleSpinner(Context context) {
        super(context);
        init(context);
    }

    public DoubleSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DoubleSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        doubleListView = new DoubleListView(DoubleSpinner.this.context);
        //doubleListView.setAdapter(new DoubleSpinnerAdapter(DoubleSpinner.this.context, null, null));
        popup = new PopupWindow(doubleListView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popup.setTouchable(true);
        popup.setOutsideTouchable(true);
        popup.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.showAsDropDown(v);
            }
        });
    }

    public void setAdapter(BaseDoubleSpinnerAdapter adapter) {
        doubleListView.setAdapter(adapter);
    }

    public void setOnSelectedListener(OnSelectedListener listener) {
        this.onSelectedListener = listener;
    }

    public static interface OnSelectedListener {
        abstract void onSelected(View parent, int primarySelected, long primaryId, int secondarySelected, long secondaryId);
    }

    private class DoubleListView extends FrameLayout  implements AdapterView.OnItemClickListener {
        BaseDoubleSpinnerAdapter adapter;
        Context context;
        ListView groupView;
        ListView childView;
        DataSetObserver dataSetObserver = null;

        public DoubleListView(Context context) {
            super(context);
            this.context = context;
            addView(LayoutInflater.from(context).inflate(R.layout.double_spinner_list, null));

            groupView = (ListView) findViewById(R.id.double_spinner_primary_list);
            childView = (ListView) findViewById(R.id.double_spinner_secondary_list);
        }

        public void setAdapter(BaseDoubleSpinnerAdapter adapter) {
            if (adapter != null) {
                if (dataSetObserver != null) {
                    this.adapter.unregisterDataSetObserver(dataSetObserver);
                }
                this.adapter = adapter;
                dataSetObserver = new DoubleListDataSetObserver();
                adapter.registerDataSetObserver(dataSetObserver);
                groupView.setAdapter(adapter.getPrimaryAdapter());

                if (adapter.getPrimaryCount() > 0) {
                    adapter.setPrimarySelected(0);
                } else {
                    childView.setAdapter(null);
                }
                groupView.setOnItemClickListener(this);
            }
        }

        private class DoubleListDataSetObserver extends DataSetObserver {
            @Override
            public void onChanged() {
                childView.setAdapter(adapter.getCurrentSecondaryAdapter());
                childView.setOnItemClickListener(DoubleListView.this);
            }
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (parent == groupView) {
                adapter.setPrimarySelected(position);
            } else if (parent == childView) {
                adapter.setSecondarySelected(position);
                popup.dismiss();
                DoubleSpinner.this.setText(((TextView) view).getText().toString());
                int primarySelected = adapter.getPrimarySelected();
                long primarySelectedId = adapter.getPrimaryAdapter().getItemId(primarySelected);
                if (onSelectedListener != null) {
                    onSelectedListener.onSelected(DoubleSpinner.this, primarySelected, primarySelectedId, position, id);
                }
            }
        }
    }
}
