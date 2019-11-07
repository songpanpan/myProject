package com.yueyou.adreader.view.ReaderPage;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.yueyou.adreader.R;
import com.yueyou.adreader.service.BookMarkEngine;
import com.yueyou.adreader.view.Event.ReadViewEvent;
import com.yueyou.adreader.view.MenuListWindow;

public class MarkView extends LinearLayout {
    private ListView mList;
    private BookMarkEngine mBookMarkEngine;

    public MarkView(final Context context) {
        super(context);
        ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.chapter_list, (ViewGroup) this);
        mList = findViewById(R.id.list);
        mList.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            ReadViewEvent.eventListener().gotoMark(position);
            ((Activity) getContext()).finish();
        });
        mList.setOnItemLongClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            MenuListWindow.show((Activity) getContext(), null, "删除书签&清空书签", "", (String title) -> {
                if (title == null) {

                } else if (title.equals("删除书签")) {
                    mBookMarkEngine.deleteMark(getContext(), position);
                    ((ListAdapter) mList.getAdapter()).notifyDataSetChanged();
                } else if (title.equals("清空书签")) {
                    mBookMarkEngine.clear(getContext());
                    ((ListAdapter) mList.getAdapter()).notifyDataSetChanged();
                }
            });
            return true;
        });
        if (ReadViewEvent.eventListener() != null) {
            mBookMarkEngine = ReadViewEvent.eventListener().getMarkEngine();
        }
        mList.setAdapter(new ListAdapter(this.getContext()));
    }

    private class ListAdapter extends BaseAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public int getCount() {
            if (mBookMarkEngine == null) return 0;
            return mBookMarkEngine.size();
        }

        @Override
        public Object getItem(int position) {
            return mBookMarkEngine.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.mark_list_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.title = (TextView) convertView.findViewById(R.id.title);
                viewHolder.describe = (TextView) convertView.findViewById(R.id.describe);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.title.setText(mBookMarkEngine.get(position).getChapterName());
            viewHolder.describe.setText(mBookMarkEngine.get(position).getMarkName());
            return convertView;
        }

        class ViewHolder {
            TextView title;
            TextView describe;
        }
    }
}
