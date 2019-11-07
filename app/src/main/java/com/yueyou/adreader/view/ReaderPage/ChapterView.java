package com.yueyou.adreader.view.ReaderPage;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yueyou.adreader.R;
import com.yueyou.adreader.activity.YueYouApplication;
import com.yueyou.adreader.service.Action;
import com.yueyou.adreader.service.model.BookShelfItem;
import com.yueyou.adreader.service.model.ChapterInfo;
import com.yueyou.adreader.view.Event.ReadViewEvent;

import java.util.ArrayList;
import java.util.List;

public class ChapterView extends LinearLayout {
    private ListView mList;
    private List<ChapterInfo> mChpaters;
    private int mBookId;
    private int mChapterId;
    private int mSelection;
    private int mDownloadChapterId;

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public ChapterView(final Context context, int bookId, int chapterId) {
        super(context);
        ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.chapter_list, (ViewGroup) this);
        mChpaters = new ArrayList<>();
        mList = findViewById(R.id.list);
        mList.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            downloadChapter(mChpaters.get(position).getChapterID());
        });
        mList.setAdapter(new ListAdapter(this.getContext()));
        mBookId = bookId;
        mChapterId = chapterId;
        downloadChapterList();
    }

    public void buyBook() {
        downloadChapter(mDownloadChapterId);
    }

    private void downloadChapter(int chapterId) {
        mDownloadChapterId = chapterId;
        new Thread(() -> {
            Looper.prepare();
            BookShelfItem bookShelfItem = ((YueYouApplication)getContext().getApplicationContext()).getMainActivity().bookshelfFrament().getBook(mBookId);
            int result = Action.getInstance().downloadChapter(getContext(), mBookId, bookShelfItem.getBookName(), chapterId, false);
            ((Activity) getContext()).runOnUiThread(() -> {
                if (result == 0) {
                    Toast.makeText(getContext(), "获取数据失败", Toast.LENGTH_SHORT).show();
                } else if (result == 1) {
                    try {
                        ReadViewEvent.eventListener().gotoChapter(chapterId);
                        ((Activity) getContext()).finish();
                    } catch (Exception e) {

                    }
                }
            });
        }).start();
    }

    private void downloadChapterList() {
        new Thread(() -> {
            Looper.prepare();

            List<ChapterInfo> chpaters = Action.getInstance().downloadChapterList(this.getContext(), mBookId);
            if (chpaters != null) {
                mChpaters = chpaters;
                ((Activity) getContext()).runOnUiThread(() -> {
                    for (int i = 0; i < mChpaters.size(); i++) {
                        if (mChpaters.get(i).getChapterID() == mChapterId) {
                            mSelection = i;
                            mList.setSelection(i);
                        }
                    }
                    ((ListAdapter) mList.getAdapter()).notifyDataSetChanged();
                });
            }
            // Looper.loop();
        }).start();
    }

    private class ListAdapter extends BaseAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return mChpaters.size();
        }

        @Override
        public Object getItem(int position) {
            return mChpaters.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.chapter_list_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.title = (TextView) convertView.findViewById(R.id.title);
                viewHolder.describe = (TextView) convertView.findViewById(R.id.describe);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.title.setText(mChpaters.get(position).getChapterName());
            if (!mChpaters.get(position).isVipChapter()) {
                viewHolder.describe.setText("免费");
            } else {
                viewHolder.describe.setText("");
            }
            if (position == mSelection) {
                viewHolder.title.setTextColor(0xffc6a39c);
            } else {
                viewHolder.title.setTextColor(0xff333333);
            }
            return convertView;
        }

        class ViewHolder {
            TextView title;
            TextView describe;
        }
    }
}
