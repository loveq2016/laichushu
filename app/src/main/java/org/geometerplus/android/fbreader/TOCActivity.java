/*
 * Copyright (C) 2009-2015 FBReader.ORG Limited <contact@fbreader.org>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.android.fbreader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.laichushu.book.R;
import com.laichushu.book.db.Idea_Table;
import com.laichushu.book.db.Idea_TableDao;
import com.laichushu.book.global.BaseApplication;
import com.laichushu.book.global.ConstantValue;
import com.laichushu.book.utils.ToastUtil;
import com.laichushu.book.utils.UIUtil;

import org.fbreader.util.FBReaderWindowUtil;
import org.geometerplus.android.fbreader.api.FBReaderIntents;
import org.geometerplus.android.fbreader.bookmark.BookmarksEditActivity;
import org.geometerplus.android.fbreader.libraryService.BookCollectionShadow;
import org.geometerplus.android.util.OrientationUtil;
import org.geometerplus.android.util.UIMessageUtil;
import org.geometerplus.android.util.ViewUtil;
import org.geometerplus.fbreader.book.Book;
import org.geometerplus.fbreader.book.BookEvent;
import org.geometerplus.fbreader.book.Bookmark;
import org.geometerplus.fbreader.book.BookmarkQuery;
import org.geometerplus.fbreader.book.HighlightingStyle;
import org.geometerplus.fbreader.book.IBookCollection;
import org.geometerplus.fbreader.bookmodel.TOCTree;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.zlibrary.core.application.ZLApplication;
import org.geometerplus.zlibrary.core.tree.ZLTree;
import org.geometerplus.zlibrary.core.util.ZLColor;
import org.geometerplus.zlibrary.ui.android.library.UncaughtExceptionHandler;
import org.geometerplus.zlibrary.ui.android.util.ZLAndroidColorUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import yuku.ambilwarna.widget.AmbilWarnaPrefWidgetView;

/**
 * 目录
 */
public class TOCActivity extends Activity implements IBookCollection.Listener<Book> {
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private LayoutInflater mInflater;
    private List<String> mTitleList = new ArrayList<>(); // 页卡标题集合
    private View view1, view2, view3; // 页卡视图
    private List<View> mViewList = new ArrayList<>(); // 页卡视图集合

    private TOCAdapter myAdapter;
    private ZLTree<?> mySelectedItem;
    private RelativeLayout rlLayout;
    private TextView tvBook;
    private RelativeLayout rlBookmark;
    private RelativeLayout rlNote;

    private static final int OPEN_ITEM_ID = 0;
    private static final int EDIT_ITEM_ID = 1;
    private static final int DELETE_ITEM_ID = 2;

    private final Map<Integer, HighlightingStyle> myStyles =
            Collections.synchronizedMap(new HashMap<Integer, HighlightingStyle>());

    private final BookCollectionShadow myCollection = new BookCollectionShadow();
    private volatile Book myBook;
    private volatile Bookmark myBookmark;

    private final Comparator<Bookmark> myComparator = new Bookmark.ByTimeComparator();

    private volatile BookmarksAdapter myThisBookAdapter;//想法adapter
    private ListView thisBookListView;
    private String bgValue;
    private RelativeLayout emptyRl;
    private ListView markLv;
    private BookmarkAdapter bookmarkAdapter;
    private FBReaderApp kooreader;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler(this));

        setContentView(R.layout.listview_toc);
        rlLayout = (RelativeLayout) findViewById(R.id.rl_shelf);
        tvBook = (TextView) findViewById(R.id.tv_book);
        mViewPager = (ViewPager) findViewById(R.id.vp_view);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mInflater = LayoutInflater.from(this);

        view1 = mInflater.inflate(R.layout.item_listview, null);
        ListView listView = (ListView) view1.findViewById(R.id.listview);
        kooreader = (FBReaderApp) ZLApplication.Instance();
        final TOCTree root = kooreader.Model.TOCTree;
        tvBook.setText(kooreader.getCurrentBook().getTitle());

        myAdapter = new TOCAdapter(listView, root);
        TOCTree treeToSelect = kooreader.getCurrentTOCElement();
        myAdapter.selectItem(treeToSelect); // 设置当前位置
        mySelectedItem = treeToSelect;

        view2 = mInflater.inflate(R.layout.list_bookmark, null);
        rlBookmark = (RelativeLayout) view2.findViewById(R.id.rl_bookmark);
        thisBookListView = (ListView) view2.findViewById(R.id.bookmark_this_book);

        myBook = FBReaderIntents.getBookExtra(getIntent(), myCollection);
        myBookmark = FBReaderIntents.getBookmarkExtra(getIntent());

        view3 = mInflater.inflate(R.layout.list_note, null);
        rlNote = (RelativeLayout) view3.findViewById(R.id.rl_note);
        emptyRl = (RelativeLayout) view3.findViewById(R.id.rl_null);//空
        markLv = (ListView) view3.findViewById(R.id.lv_mark);

        myCollection.bindToService(this, new Runnable() {
            public void run() {
                myThisBookAdapter =
                        new BookmarksAdapter(thisBookListView, myBookmark != null);

                bookmarkAdapter = new BookmarkAdapter(markLv);
                myCollection.addListener(TOCActivity.this);
                updateStyles();
                loadBookmarks();
            }
        });


        /**
         * 设置背景与阅读背景一致
         */
        bgValue = kooreader.ViewOptions.getColorProfile().WallpaperOption.getValue();
        bgValue = "wallpapers/bg_vine_grey.png";
        switch (bgValue) {
            case "wallpapers/bg_green.png":
                listView.setBackgroundResource(R.drawable.bg_green);
                rlLayout.setBackgroundResource(R.drawable.bg_green);
                mTabLayout.setBackgroundResource(R.drawable.bg_green);
                rlBookmark.setBackgroundResource(R.drawable.bg_green);
                rlNote.setBackgroundResource(R.drawable.bg_green);
                break;
            case "wallpapers/bg_grey.png":
                listView.setBackgroundResource(R.drawable.bg_grey);
                rlLayout.setBackgroundResource(R.drawable.bg_grey);
                mTabLayout.setBackgroundResource(R.drawable.bg_grey);
                rlBookmark.setBackgroundResource(R.drawable.bg_grey);
                rlNote.setBackgroundResource(R.drawable.bg_grey);
                break;
            case "wallpapers/bg_night.png":
                listView.setBackgroundResource(R.drawable.bg_white);
                rlLayout.setBackgroundResource(R.drawable.bg_white);
                mTabLayout.setBackgroundResource(R.drawable.bg_white);
                rlBookmark.setBackgroundResource(R.drawable.bg_white);
                rlNote.setBackgroundResource(R.drawable.bg_white);
                break;
            case "wallpapers/bg_vine_grey.png":
                listView.setBackgroundResource(R.drawable.bg_vine_grey);
                rlLayout.setBackgroundResource(R.drawable.bg_vine_grey);
//				mTabLayout.setBackgroundResource(R.drawable.bg_vine_grey);
//				rlBookmark.setBackgroundResource(R.drawable.bg_vine_grey);
//				rlNote.setBackgroundResource(R.drawable.bg_vine_grey);
                break;
            case "wallpapers/bg_vine_white.png":
                listView.setBackgroundResource(R.drawable.bg_vine_white);
                rlLayout.setBackgroundResource(R.drawable.bg_vine_white);
                mTabLayout.setBackgroundResource(R.drawable.bg_vine_white);
                rlBookmark.setBackgroundResource(R.drawable.bg_vine_white);
                rlNote.setBackgroundResource(R.drawable.bg_vine_white);
                break;
            case "wallpapers/bg_white.png":
                listView.setBackgroundResource(R.drawable.bg_white);
                rlLayout.setBackgroundResource(R.drawable.bg_white);
                mTabLayout.setBackgroundResource(R.drawable.bg_white);
                rlBookmark.setBackgroundResource(R.drawable.bg_white);
                rlNote.setBackgroundResource(R.drawable.bg_white);
                break;
        }

        //添加页卡视图
        mViewList.add(view1);
        mViewList.add(view3);
        mViewList.add(view2);
        //添加页卡标题
        mTitleList.add("目录");
        mTitleList.add("书签");
        mTitleList.add("想法");

        mTabLayout.setTabMode(TabLayout.MODE_FIXED);//设置tab模式，当前为系统默认模式
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(0)));//添加tab选项卡
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(1)));
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(2)));

        MyPagerAdapter mAdapter = new MyPagerAdapter(mViewList);
        mViewPager.setAdapter(mAdapter); // 给ViewPager设置适配器
        mTabLayout.setupWithViewPager(mViewPager); // 将TabLayout和ViewPager关联起来。
        mTabLayout.setTabsFromPagerAdapter(mAdapter); // 给Tabs设置适配器
    }

    @Override
    protected void onStart() {
        super.onStart();
        OrientationUtil.setOrientation(this, getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        OrientationUtil.setOrientation(this, intent);
    }

    private final class TOCAdapter extends ZLTreeAdapter {
        TOCAdapter(ListView listView, TOCTree root) {
            super(listView, root);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final View view = (convertView != null) ? convertView : LayoutInflater.from(parent.getContext()).inflate(R.layout.toc_tree_item, parent, false);
            final TOCTree tree = (TOCTree) getItem(position);
            ViewUtil.findTextView(view, R.id.toc_tree_item_text).setText(tree.getText());
            return view;
        }

        void openBookText(TOCTree tree) {
            final TOCTree.Reference reference = tree.getReference();
            if (reference != null) {
                finish();
                final FBReaderApp kooreader = (FBReaderApp) ZLApplication.Instance();
                kooreader.addInvisibleBookmark();
                kooreader.BookTextView.gotoPosition(reference.ParagraphIndex, 0, 0);
                kooreader.showBookTextView();
                kooreader.storePosition();
            }
        }

        @Override
        protected boolean runTreeItem(ZLTree<?> tree) {
            if (super.runTreeItem(tree)) {
                return true;
            }
            if (ConstantValue.ISREADER) {
                FBReaderApp fbReaderApp = (FBReaderApp) FBReaderApp.Instance();
                if (fbReaderApp.getCurrentTOCElement() != null) {
                    TOCTree parent = fbReaderApp.getCurrentTOCElement().Parent;
                    List<TOCTree> subtrees = parent.subtrees();//目录
                    for (int i = 0; i < subtrees.size(); i++) {
                        if (((TOCTree) tree).getText().equals(subtrees.get(i).getText())) {
                            if (i > ConstantValue.ISREADER_NUMBER - 1) {
                                ToastUtil.showToast("购买后才可阅读");
                                return false;
                            }
                        }
                    }
                }
            }
            openBookText((TOCTree) tree);
            return true;
        }
    }


    //ViewPager适配器
    class MyPagerAdapter extends PagerAdapter {
        private List<View> mViewList;

        public MyPagerAdapter(List<View> mViewList) {
            this.mViewList = mViewList;
        }

        @Override
        public int getCount() {
            return mViewList.size();//页卡数
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;//官方推荐写法
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mViewList.get(position));//添加页卡
            return mViewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViewList.get(position));//删除页卡
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitleList.get(position);//页卡标题
        }

    }


    private void updateStyles() {
        synchronized (myStyles) {
            myStyles.clear();
            for (HighlightingStyle style : myCollection.highlightingStyles()) {
                myStyles.put(style.Id, style);
            }
        }
    }

    private final Object myBookmarksLock = new Object();

    private void loadBookmarks() {
        new Thread(new Runnable() {
            public void run() {
                synchronized (myBookmarksLock) {
                    for (BookmarkQuery query = new BookmarkQuery(myBook, 10000); ; query = query.next()) {
                        final List<Bookmark> thisBookBookmarks = myCollection.bookmarks(query);
                        if (thisBookBookmarks.isEmpty()) {
                            break;
                        }
                        myThisBookAdapter.addAll(thisBookBookmarks);
                        bookmarkAdapter.addAll(thisBookBookmarks);
//                        myAllBooksAdapter.addAll(thisBookBookmarks);
                    }
//                    for (BookmarkQuery query = new BookmarkQuery(50); ; query = query.next()) {
//                        final List<Bookmark> allBookmarks = myCollection.bookmarks(query);
//                        if (allBookmarks.isEmpty()) {
//                            break;
//                        }
//                        myAllBooksAdapter.addAll(allBookmarks);
//                    }
                }
            }
        }).start();
    }

    private void updateBookmarks(final Book book) {
        new Thread(new Runnable() {
            public void run() {
                synchronized (myBookmarksLock) {
                    final boolean flagThisBookTab = book.getId() == myBook.getId();

                    final Map<String, Bookmark> oldBookmarks = new HashMap<String, Bookmark>();
                    final Map<String, Bookmark> oldBookmarks2 = new HashMap<String, Bookmark>();
                    if (flagThisBookTab) {
                        for (Bookmark b : myThisBookAdapter.bookmarks()) {
                            oldBookmarks.put(b.Uid, b);
                        }
                        for (Bookmark b : bookmarkAdapter.bookmarks()) {
                            oldBookmarks2.put(b.Uid, b);
                        }
                    }

                    for (BookmarkQuery query = new BookmarkQuery(book, 50); ; query = query.next()) {
                        final List<Bookmark> loaded = myCollection.bookmarks(query);
                        if (loaded.isEmpty()) {
                            break;
                        }
                        for (Bookmark b : loaded) {
                            final Bookmark old = oldBookmarks.remove(b.Uid);
//                            myAllBooksAdapter.replace(old, b);
                            if (flagThisBookTab) {
                                myThisBookAdapter.replace(old, b);
                            }
                        }
                        for (Bookmark b : loaded) {
                            final Bookmark old = oldBookmarks2.remove(b.Uid);
                            if (flagThisBookTab) {
                                bookmarkAdapter.replace(old, b);
                            }
                        }
                    }
                    if (flagThisBookTab) {
                        myThisBookAdapter.removeAll(oldBookmarks.values());
                        bookmarkAdapter.removeAll(oldBookmarks.values());
                    }
                }
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        myCollection.unbind();
        super.onDestroy();
    }

    private boolean isBookMark;

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final int position = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
        BaseAdapter adapter = null;
        if (isBookMark) {//判断点开的是那个页面
            adapter = bookmarkAdapter;
        } else {
            adapter = myThisBookAdapter;
        }

        final Bookmark bookmark = (Bookmark) adapter.getItem(position);
        switch (item.getItemId()) {
            case OPEN_ITEM_ID://打开想法
                gotoBookmark(bookmark);
                return true;
            case EDIT_ITEM_ID://编辑想法
                final Intent intent = new Intent(this, BookmarksEditActivity.class);
                intent.putExtra("bgColor", bgValue);
                intent.putExtra("type", true);
                FBReaderIntents.putBookmarkExtra(intent, bookmark);
                OrientationUtil.startActivity(this, intent);
                return true;
            case DELETE_ITEM_ID://删除想法
                myCollection.deleteBookmark(bookmark);
                FBReaderWindowUtil.deleteBookmarkForIdea_Table(bookmark);
                if (isBookMark) {
                    bookmarkAdapter.myBookmarksList.remove(bookmark);
                    bookmarkAdapter.notifyDataSetChanged();
                    if (bookmarkAdapter.myBookmarksList.size() == 0) {
                        emptyRl.setVisibility(View.VISIBLE);
                        markLv.setVisibility(View.INVISIBLE);
                    }
                }
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void gotoBookmark(Bookmark bookmark) {
        bookmark.markAsAccessed();
        myCollection.saveBookmark(bookmark);
        final Book book = myCollection.getBookById(bookmark.BookId);
        if (book != null) {
//            int paragraphIndex = bookmark.getParagraphIndex();
//            int paragraphIndex1 = kooreader.getCurrentTOCElement().getReference().ParagraphIndex;
//            int abs = paragraphIndex1 - paragraphIndex;
//            kooreader.getTextView().gotoPosition(paragraphIndex1+abs,0,0);
//            FBReader.openBookActivity(this, book, bookmark);
            kooreader.getTextView().gotoPosition(bookmark);
            kooreader.getViewWidget().reset();
            kooreader.getViewWidget().repaint();
            finish();
        } else {
            UIMessageUtil.showErrorMessage(this, "cannotOpenBook");
        }
    }

    private final class BookmarksAdapter extends BaseAdapter implements AdapterView.OnItemClickListener, View.OnCreateContextMenuListener {
        private final List<Bookmark> myBookmarksList =
                Collections.synchronizedList(new LinkedList<Bookmark>());
        private volatile boolean myShowAddBookmarkItem;

        BookmarksAdapter(ListView listView, boolean showAddBookmarkItem) {
            myShowAddBookmarkItem = showAddBookmarkItem;
            listView.setAdapter(this);
            listView.setOnItemClickListener(this);
            listView.setOnCreateContextMenuListener(this);
        }

        public List<Bookmark> bookmarks() {
            return Collections.unmodifiableList(myBookmarksList);
        }

        public void addAll(final List<Bookmark> bookmarks) {
            runOnUiThread(new Runnable() {
                public void run() {
                    synchronized (myBookmarksList) {
                        for (Bookmark b : bookmarks) {
                            final int position = Collections.binarySearch(myBookmarksList, b, myComparator);
                            if (position < 0 && b.getStyleId() != 5) {
                                myBookmarksList.add(-position - 1, b);

                            }
                        }
                    }
                    notifyDataSetChanged();
                }
            });
        }

        private boolean areEqualsForView(Bookmark b0, Bookmark b1) {
            return
                    b0.getStyleId() == b1.getStyleId() &&
                            b0.getText().equals(b1.getText()) &&
                            b0.getTimestamp(Bookmark.DateType.Latest).equals(b1.getTimestamp(Bookmark.DateType.Latest));
        }

        public void replace(final Bookmark old, final Bookmark b) {
            if (old != null && areEqualsForView(old, b)) {
                return;
            }
            if (b.getStyleId() == 5) {
                return;
            }
            runOnUiThread(new Runnable() {
                public void run() {
                    synchronized (myBookmarksList) {
                        if (old != null) {
                            myBookmarksList.remove(old);
                        }
                        final int position = Collections.binarySearch(myBookmarksList, b, myComparator);
                        if (position < 0) {
                            myBookmarksList.add(-position - 1, b);
                        }
                    }
                    notifyDataSetChanged();
                }
            });
        }

        public void removeAll(final Collection<Bookmark> bookmarks) {
            if (bookmarks.isEmpty()) {
                return;
            }
            runOnUiThread(new Runnable() {
                public void run() {
                    myBookmarksList.removeAll(bookmarks);
                    notifyDataSetChanged();
                }
            });
        }

        public void clear() {
            runOnUiThread(new Runnable() {
                public void run() {
                    myBookmarksList.clear();
                    notifyDataSetChanged();
                }
            });
        }

        public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
            final int position = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
            if (getItem(position) != null) {
                isBookMark = false;
                menu.add(0, OPEN_ITEM_ID, 0, "打开想法");
                menu.add(0, EDIT_ITEM_ID, 0, "编辑想法");
                menu.add(0, DELETE_ITEM_ID, 0, "删除想法");
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final View view = (convertView != null) ? convertView :
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark_item, parent, false);
            final ImageView imageView = ViewUtil.findImageView(view, R.id.bookmark_item_icon);
            final View colorContainer = ViewUtil.findView(view, R.id.bookmark_item_color_container);
            final AmbilWarnaPrefWidgetView colorView =
                    (AmbilWarnaPrefWidgetView) ViewUtil.findView(view, R.id.bookmark_item_color);
            final TextView textView = ViewUtil.findTextView(view, R.id.bookmark_item_text);
            final TextView bookTitleView = ViewUtil.findTextView(view, R.id.bookmark_item_booktitle);

            final Bookmark bookmark = getItem(position);
            if (bookmark == null) {
//				imageView.setVisibility(View.VISIBLE);
//				imageView.setImageResource(R.drawable.ic_list_plus);
                colorContainer.setVisibility(View.GONE);
//				textView.setText("添加想法");
                bookTitleView.setVisibility(View.GONE);
            } else {
                imageView.setVisibility(View.GONE);
//				colorContainer.setVisibility(View.VISIBLE);
                setupColorView(colorView, myStyles.get(bookmark.getStyleId()));
                textView.setText(bookmark.getText());
                if (myShowAddBookmarkItem) {
                    bookTitleView.setVisibility(View.GONE);
                } else {
                    bookTitleView.setVisibility(View.VISIBLE);
                    bookTitleView.setText(bookmark.BookTitle);
                }
            }
            return view;
        }

        @Override
        public final boolean areAllItemsEnabled() {
            return true;
        }

        @Override
        public final boolean isEnabled(int position) {
            return true;
        }

        @Override
        public final long getItemId(int position) {
            final Bookmark item = getItem(position);
            return item != null ? item.getId() : 0;
        }

        @Override
        public final Bookmark getItem(int position) {
            if (myShowAddBookmarkItem) {
//				--position;
            }
            return position >= 0 ? myBookmarksList.get(position) : null;
        }

        @Override
        public final int getCount() {
            return myShowAddBookmarkItem ? myBookmarksList.size() : myBookmarksList.size();
        }

        public final void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final Bookmark bookmark = getItem(position);
            if (bookmark != null) {
                gotoBookmark(bookmark);
            } else if (myShowAddBookmarkItem) {
                myShowAddBookmarkItem = false;
                myCollection.saveBookmark(myBookmark);
            }
        }
    }

    // method from IBookCollection.Listener
    public void onBookEvent(BookEvent event, Book book) {
        switch (event) {
            default:
                break;
            case BookmarkStyleChanged:
                runOnUiThread(new Runnable() {
                    public void run() {
                        updateStyles();
                        myThisBookAdapter.notifyDataSetChanged();
                    }
                });
                break;
            case BookmarksUpdated:
                updateBookmarks(book);
                break;
        }
    }

    // method from IBookCollection.Listener
    public void onBuildEvent(IBookCollection.Status status) {
    }

    static void setupColorView(AmbilWarnaPrefWidgetView colorView, HighlightingStyle style) {
        Integer rgb = null;
        if (style != null) {
            final ZLColor color = style.getBackgroundColor();
            if (color != null) {
                rgb = ZLAndroidColorUtil.rgb(color);
            }
        }

        if (rgb != null) {
            colorView.setBackgroundColor(rgb);
        } else {
            colorView.setBackgroundColor(0);
        }
    }

    private final class BookmarkAdapter extends BaseAdapter implements AdapterView.OnItemClickListener, View.OnCreateContextMenuListener {

        private ListView mListView;
        private final List<Bookmark> myBookmarksList =
                Collections.synchronizedList(new LinkedList<Bookmark>());

        public BookmarkAdapter(ListView markLv) {
            mListView = markLv;
            mListView.setAdapter(this);
            mListView.setOnItemClickListener(this);
            mListView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            final int position = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
            if (getItem(position) != null) {
                isBookMark = true;
                menu.add(0, OPEN_ITEM_ID, 0, "打开书签");
                menu.add(0, DELETE_ITEM_ID, 0, "删除书签");
            }
        }

        @Override
        public int getCount() {
            return myBookmarksList.size();
        }

        @Override
        public final Bookmark getItem(int position) {
            return myBookmarksList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Viewholder holder = null;
            if (convertView != null) {
                holder = (Viewholder) convertView.getTag();
            } else {
                convertView = UIUtil.inflate(R.layout.item_bookmark);
                holder = new Viewholder(convertView);
                convertView.setTag(holder);
            }

            if (myBookmarksList.size() > 0) {//隐藏空界面
                markLv.setVisibility(View.VISIBLE);
                emptyRl.setVisibility(View.INVISIBLE);
            } else {//显示空界面
                emptyRl.setVisibility(View.VISIBLE);
                markLv.setVisibility(View.INVISIBLE);
            }

            TOCTree currentTOCElement = kooreader.getCurrentTOCElement(getItem(position).getParagraphIndex());
            int paragraphIndex = currentTOCElement.getReference().ParagraphIndex;
            String text = currentTOCElement.getText().toString();
            String content = getItem(position).getText().toString();
//            holder.tv.setText("章节："+text+"     "+"第"+paragraphIndex+"段");
            if (content.length() > 5) {
                content = content.substring(0, 4);
            }
            holder.tv.setText("章节：" + text + "     " + content + "......");

            return convertView;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final Bookmark bookmark = getItem(position);
            if (bookmark != null) {
                gotoBookmark(bookmark);
            }
        }

        public void addAll(final List<Bookmark> bookmarks) {
            runOnUiThread(new Runnable() {
                public void run() {
                    synchronized (myBookmarksList) {
                        for (Bookmark b : bookmarks) {
                            final int position = Collections.binarySearch(myBookmarksList, b, myComparator);
                            if (position < 0 && b.getStyleId() == 5) {
                                myBookmarksList.add(-position - 1, b);

                            }
                        }
                    }
                    notifyDataSetChanged();
                }
            });
        }

        public void replace(final Bookmark old, final Bookmark b) {
            if (old != null && areEqualsForView(old, b)) {
                return;
            }
            if (b.getStyleId() != 5) {
                return;
            }
            runOnUiThread(new Runnable() {
                public void run() {
                    synchronized (myBookmarksList) {
                        if (old != null) {
                            myBookmarksList.remove(old);
                        }
                        final int position = Collections.binarySearch(myBookmarksList, b, myComparator);
                        if (position < 0) {
                            myBookmarksList.add(-position - 1, b);
                        }
                    }
                    notifyDataSetChanged();
                }
            });
        }

        private boolean areEqualsForView(Bookmark b0, Bookmark b1) {
            return
                    b0.getStyleId() == b1.getStyleId() &&
                            b0.getText().equals(b1.getText()) &&
                            b0.getTimestamp(Bookmark.DateType.Latest).equals(b1.getTimestamp(Bookmark.DateType.Latest));
        }

        class Viewholder {
            TextView tv;

            public Viewholder(View itemView) {
                tv = (TextView) itemView.findViewById(R.id.tv_item);
            }
        }

        public void removeAll(final Collection<Bookmark> bookmarks) {
            if (bookmarks.isEmpty()) {
                return;
            }
            runOnUiThread(new Runnable() {
                public void run() {
                    myBookmarksList.removeAll(bookmarks);
                    notifyDataSetChanged();
                }
            });
        }

        public List<Bookmark> bookmarks() {
            return Collections.unmodifiableList(myBookmarksList);
        }
    }
}
