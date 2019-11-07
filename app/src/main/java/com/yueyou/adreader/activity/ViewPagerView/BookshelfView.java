package com.yueyou.adreader.activity.ViewPagerView;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.qq.e.comm.util.StringUtil;
import com.yueyou.adreader.R;
import com.yueyou.adreader.activity.ReadActivity;
import com.yueyou.adreader.activity.WebViewActivity;
import com.yueyou.adreader.service.Action;
import com.yueyou.adreader.service.BookShelfEngine;
import com.yueyou.adreader.service.advertisement.adObject.AdBookShelfIcon;
import com.yueyou.adreader.service.analytics.AnalyticsEngine;
import com.yueyou.adreader.service.analytics.ThirdAnalytics;
import com.yueyou.adreader.service.db.BookFileEngine;
import com.yueyou.adreader.service.db.DataSHP;
import com.yueyou.adreader.service.model.BookInfo;
import com.yueyou.adreader.service.model.BookShelfItem;
import com.yueyou.adreader.util.Utils;
import com.yueyou.adreader.util.Widget;
import com.yueyou.adreader.view.BookShelf;
import com.yueyou.adreader.view.CoverView;
import com.yueyou.adreader.view.HeaderGridView;

import java.util.ArrayList;
import java.util.List;

public class BookshelfView extends ViewPagerBase implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener  {
    private BookShelf mBookShelf;
    private HeaderGridView mGrideView;
    private GridAdapter mGridAdapter;
    private List<Integer> mCheckedBooks;
    private BookShelfEngine mBookShelfEngine = null;
    private View mEditMenu;
    private String mBuildinBookName;
    private String mBuildinBookId;
    private String mBuildinChapterId;
    private final int mGetBuildinBookWithCover = 0;
    private boolean mHaveGetBuildinBook;
    private boolean mNeedOpenBuildinBook;
    private boolean mMainPreViewFinished;
    private boolean mDefaultOpenFirstBook;
    private AdBookShelfIcon mAdbookShelfIcon = new AdBookShelfIcon();
    public void getBuildinBook(boolean flag, String bookName, String bookId, String chapterId) {
        mBuildinBookName = bookName;
        mBuildinBookId = bookId;
        mBuildinChapterId = chapterId;
        getBuildinBook();
    }
    public BookshelfView(final Context context) {
        super(context);
        ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.bookshelf_frament, (ViewGroup)this);
        mBookShelf = findViewById(R.id.book_shelf);
        mGrideView = findViewById(R.id.gridView);
        mGridAdapter = new GridAdapter(this.getContext());
        mGrideView.setAdapter(mGridAdapter);
        mGrideView.setOnItemClickListener(this);
        mGrideView.setOnItemLongClickListener(this);
        mEditMenu = findViewById(R.id.edit_menu);
        findViewById(R.id.edit_delete).setOnClickListener(mOnClickListener);
        findViewById(R.id.edit_all_select).setOnClickListener(mOnClickListener);
        findViewById(R.id.edit_all_unselect).setOnClickListener(mOnClickListener);
        findViewById(R.id.edit_cancel).setOnClickListener(mOnClickListener);
        loadBooks();
    }

    public void activite() {
        mBookShelfEngine.startCheckUpdate();
        mBookShelfEngine.startGetBookAd();
        new Thread(() -> {
            try {
                Looper.prepare();
                Action.getInstance().getShelfBookPull(getContext(), getBookIds());
            } catch (Exception e) {
                ThirdAnalytics.reportError(getContext(), "getShelfBookPull error : %s", e.getMessage());
            }
        }).start();
        mAdbookShelfIcon.load(findViewById(R.id.ad_bottom_icon));
    }

    @Override
    public boolean enter() {
        if(!super.enter())
            return false;
        Log.i("BookshelfView", "BookshelfView enter");
        mBookShelf.resume();
        mBookShelfEngine.sortBookShelf();
        mBookShelfEngine.checkUpdate();
        return true;
    }

    @Override
    public void onDetachedFromWindow() {
        if (mBookShelfEngine != null) {
            mBookShelfEngine.release();
        }
        super.onDetachedFromWindow();
    }

    public void mainPreViewFinished() {
        if (mNeedOpenBuildinBook) {
            openBuildinBook();
        }
        mMainPreViewFinished = true;
    }

    public void defaultOpenFirstBook() {
        mDefaultOpenFirstBook = true;
    }

    private synchronized void openBuildinBook() {
        if (!mNeedOpenBuildinBook)
            return;
        mNeedOpenBuildinBook = false;
        try {
            readBook(0);
//            MessageDlg.show(this.getContext(), mBookShelfEngine.get(0).getBookName() + "已加入书架\n是否立即阅读?",
//                    (boolean result) -> {
//                        if (result) {
//                            readBook(0);
//                        }
//                    }
//            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void getBuildinBook() {
        if (mHaveGetBuildinBook)
            return;
        mHaveGetBuildinBook = true;
        mDefaultOpenFirstBook = false;
        new Thread(() -> {
            Looper.prepare();
            if (Action.getInstance().getBuildingBookNew(this.getContext(), mBuildinBookName, mBuildinBookId, mBuildinChapterId, mGetBuildinBookWithCover)) {
                BookShelfItem book = getBookByIndex(0);
                if (book != null) {
                    if (mDefaultOpenFirstBook || (!Widget.isBlank(mBuildinBookName) && mBuildinBookName.equals(book.getBookName()))) {
                        try {
                            ((Activity) getContext()).runOnUiThread(() -> {
                                mNeedOpenBuildinBook = true;
                                if (mMainPreViewFinished) {
                                    openBuildinBook();
                                }
                            });
                        }catch (Exception e){

                        }
                    }
                }
                if (mGetBuildinBookWithCover == 0) {
                    Action.getInstance().getBookCover(this.getContext(), getBooks());
                    refreshView();
                }
            }
            mBookShelfEngine.startGetBookAd();
        }).start();
    }

    private View.OnClickListener mOnClickListener = (View v) -> {
        if (v.getId() == R.id.edit_delete) {
            for (int item : mCheckedBooks) {
                deleteBook(item);
            }
            mEditMenu.setVisibility(View.GONE);
        } else if (v.getId() == R.id.edit_all_select) {
            bookCheckedAll();
        } else if (v.getId() == R.id.edit_all_unselect) {
            bookUnChecked();
        } else if (v.getId() == R.id.edit_cancel) {
            mEditMenu.setVisibility(View.GONE);
        }
        this.mGridAdapter.notifyDataSetChanged();
    };

    private void loadBooks() {
        Log.i("blank screen", "blank screen loadBooks: " + this);
        mBookShelfEngine = new BookShelfEngine(this.getContext(), () -> {
            try {
                ((Activity) getContext()).runOnUiThread(() -> {
                    this.mGridAdapter.notifyDataSetChanged();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        Log.i("blank screen", "blank screen loadBooks mBookShelfEngine: " + mBookShelfEngine);
        mCheckedBooks = new ArrayList<Integer>();
    }

    public void refreshView() {
        try {
            ((Activity) getContext()).runOnUiThread(() -> {
                this.mGridAdapter.notifyDataSetChanged();
            });
        } catch (Exception e) {
            e.printStackTrace();
            ThirdAnalytics.reportError(getContext(), e);
        }
    }

    public void refreshBookChapterCount(int bookId, int chapterCount) {
        if (!mBookShelfEngine.refreshBookChapterCount(bookId, chapterCount))
            return;
        refreshView();
        return;
    }

    public void refreshBookReadProgress(BookShelfItem book) {
        if (mBookShelfEngine == null) return;
        mBookShelfEngine.refreshBookReadProgress(book);
    }

    public List<BookShelfItem> getBooks() {
        return mBookShelfEngine.books();
    }

    private List<Integer> getBookIds() {
        List<Integer> ids = new ArrayList<>();
        List<BookShelfItem> books = this.getBooks();
        for (BookShelfItem item : books) {
            ids.add(item.getBookId());
        }
        return ids;
    }

    public BookShelfItem getBook(int bookId) {
        Log.i("blank screen", "blank screen getBook: " + mBookShelfEngine);
        if (mBookShelfEngine == null) {
            return null;
        }
        return mBookShelfEngine.getBook(bookId);
    }

    public BookShelfItem getBookByIndex(int index) {
        try {
            return mBookShelfEngine.get(index);
        } catch (Exception e) {
            return null;
        }
    }

    public void getBuildinBookFinish() {
        uploadBookIds();
    }

    private void uploadBookIds() {
        mBookShelfEngine.uploadBookIds();
    }

    public boolean addBook(BookInfo bookInfo, int chapterId, boolean sort, boolean reset, boolean upload) {
        boolean result = mBookShelfEngine.addBook(bookInfo, chapterId, sort, reset);
        refreshView();
        if (result && upload)
            uploadBookIds();
        return result;
    }

    public void deleteBook(int bookId) {
        if (!mBookShelfEngine.deleteBook(bookId))
            return;
        uploadBookIds();
        this.mGridAdapter.notifyDataSetChanged();
    }
    public void readBook(int position) {
        if (Utils.isFastEvent())
            return;
        Widget.startActivity((Activity) getContext(), ReadActivity.class, ReadActivity.KEY_BOOKID, mBookShelfEngine.get(position).getBookId());
    }

    public BookShelf BookShelf() {
        return mBookShelf;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (StringUtil.isEmpty(DataSHP.getUserId(this.getContext()))) {
            Toast.makeText(getContext(), "登录错误，请重新登录", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mGrideView.getHeaderViewCount() > 0)
            position -= mGrideView.getHeaderViewCount() * 3;
        if (mEditMenu.getVisibility() == View.VISIBLE) {
            setBookCheckStatus(mBookShelfEngine.get(position).getBookId());
            return;
        }
        if (mBookShelfEngine.get(position).isAd()) {
            //mBookShelfEngine.get(position).refreshReadTime();
            AnalyticsEngine.advertisement(this.getContext(), mBookShelfEngine.get(position).getDataOffset(), mBookShelfEngine.get(position).getAuthor(), true);
            WebViewActivity.show((Activity) getContext(), mBookShelfEngine.get(position).getBookName(), WebViewActivity.CLOSED, "");
        } else {
            Widget.startActivity((Activity) getContext(), ReadActivity.class, ReadActivity.KEY_BOOKID, mBookShelfEngine.get(position).getBookId());
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (mEditMenu.getVisibility() == View.VISIBLE)
            return true;
        if (mGrideView.getHeaderViewCount() > 0)
            position -= mGrideView.getHeaderViewCount() * 3;
        mCheckedBooks.clear();
        mCheckedBooks.add(mBookShelfEngine.get(position).getBookId());
        mEditMenu.setVisibility(View.VISIBLE);
        this.mGridAdapter.notifyDataSetChanged();
        return true;
    }

    private void setBookCheckStatus(int bookId) {
        if (isBookChecked(bookId)) {
            mCheckedBooks.remove(new Integer(bookId));
        } else {
            mCheckedBooks.add(bookId);
        }
        this.mGridAdapter.notifyDataSetChanged();
    }

    private boolean isBookChecked(int bookId) {
        for (int item : mCheckedBooks) {
            if (item == bookId)
                return true;
        }
        return false;
    }

    private void bookCheckedAll() {
        mCheckedBooks.clear();
        for (BookShelfItem item : mBookShelfEngine.books()) {
            mCheckedBooks.add(item.getBookId());
        }
    }

    private void bookUnChecked() {
        List<Integer> tmp = new ArrayList<Integer>();
        for (BookShelfItem item : mBookShelfEngine.books()) {
            if (!isBookChecked(item.getBookId())) {
                tmp.add(item.getBookId());
            }
        }
        mCheckedBooks.clear();
        mCheckedBooks.addAll(tmp);
    }

    private class GridAdapter extends BaseAdapter {
        private Context mContext;

        public GridAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public int getCount() {
            if (mBookShelfEngine == null)
                return 0;
            return mBookShelfEngine.size();
        }

        @Override
        public Object getItem(int position) {
            return mBookShelfEngine.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.bookshelf_item, parent, false);
                //view.setLayoutParams(rllp);
                viewHolder = new ViewHolder();
                viewHolder.cover = (CoverView) convertView.findViewById(R.id.iv_cover);
                viewHolder.check = (ImageView) convertView.findViewById(R.id.check);
                viewHolder.name = (TextView) convertView.findViewById(R.id.name);
                viewHolder.update = (ImageView) convertView.findViewById(R.id.update);
                viewHolder.check.setOnClickListener((View v) -> {
                    setBookCheckStatus(mBookShelfEngine.get(position).getBookId());
                });
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            ViewGroup view = convertView.findViewById(R.id.container);
            RelativeLayout.LayoutParams rllp = (RelativeLayout.LayoutParams) view.getLayoutParams();
            int width = mGrideView.getWidth();
            int space = width / 8;
            rllp.setMargins(space / 3, 0, space / 3, Widget.dip2px(getContext(), 20));
//            if (position % 3 == 0) {
//                rllp.setMargins(hSpace, 0, space, Widget.dip2px(getContext(), 20));
//            } else if (position % 3 == 1) {
//                rllp.setMargins(0, 0, 0, Widget.dip2px(getContext(), 20));
//            } else {
//                rllp.setMargins(space, 0, hSpace, Widget.dip2px(getContext(), 20));
//            }
            if (mEditMenu.getVisibility() == View.VISIBLE) {
                viewHolder.check.setVisibility(View.VISIBLE);
                if (isBookChecked(mBookShelfEngine.get(position).getBookId())) {
                    viewHolder.check.setImageResource(R.drawable.book_checked);
                } else {
                    viewHolder.check.setImageResource(R.drawable.book_unchecked);
                }
            } else {
                viewHolder.check.setVisibility(View.GONE);
            }
            if (mBookShelfEngine.get(position).isAd()) {
                viewHolder.update.setVisibility(View.VISIBLE);
                viewHolder.update.setImageResource(R.drawable.bookad);
            } else {
                if (mBookShelfEngine.get(position).isUpdate()) {
                    viewHolder.update.setImageResource(R.drawable.update);
                    viewHolder.update.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.update.setVisibility(View.GONE);
                }
            }
            viewHolder.name.setText(mBookShelfEngine.get(position).getBookName());
            ViewHolder finalViewHolder = viewHolder;
            Glide.with(mContext)
                    .load(BookFileEngine.getBookCover(this.mContext, mBookShelfEngine.get(position).getBookId())).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Drawable> target, boolean b) {
//                    finalViewHolder.name.setVisibility(View.VISIBLE);
//                    finalViewHolder.name.setText(mBookShelfEngine.get(position).getBookName());
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable drawable, Object o, Target<Drawable> target, DataSource dataSource, boolean b) {
                    //finalViewHolder.name.setVisibility(View.GONE);
                    return false;
                }
            })
                    .apply(new RequestOptions().placeholder(R.drawable.default_cover).diskCacheStrategy(DiskCacheStrategy.NONE))
                    .into(viewHolder.cover);
            return convertView;
        }

        class ViewHolder {
            CoverView cover;
            ImageView check;
            ImageView update;
            TextView name;
        }
    }
}
