package com.willard.picturesplayer;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.willard.photosplayer.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
/**
 * 图片播放器
 * @author willard
 * @2015-7-3
 * @上午9:34:25
 */
public class PicturesPlayer extends FrameLayout {
	private MyViewPager viewPager;
	// 当前轮播页
	private int currentItem = 0;
	// 定时任务
	private ScheduledExecutorService scheduledExecutorService;
	// 自动轮播的时间间隔
	private int timeInterval = 5;
	// 自动轮播启用开关
	private boolean isAutoPlayBegin = true;

	// 放轮播图片的ImageView 的list
	private List<ImageView> imageViewsList;

	private Context mContext;

	// 是否处于自动播放状态
	private boolean isAutoPlay = false;

	// 是否是循环播放模式
	private boolean isNeedRepeat = false;

	// 手动下一页
	private static final int NEXT_PAGE_MANUAL = 1;
	// 手动上一页
	private static final int PRE_PAGE_MANUAL = 2;
	// 自动播放
	private static final int AUTO_PLAY = 3;
	// 暂停播放
	private static final int PAUSE_PLAY = 4;
	// 继续播放
	private static final int GO_ON_PLAY = 5;
	// 默认不能手动滑动Pager
	private boolean canScroll = false;
	// 切换速度 默认200毫秒
	private int mDuration = 200;

	public void setTimeInterval(int timeInterval) {
		this.timeInterval = timeInterval;
	}

	public void setAutoPlayBegin(boolean isAutoPlayBegin) {
		this.isAutoPlayBegin = isAutoPlayBegin;
	}

	public void setNeedRepeat(boolean isNeedRepeat) {
		this.isNeedRepeat = isNeedRepeat;
	}

	public void setmDuration(int mDuration) {
		this.mDuration = mDuration;
	}

	/**
	 * 默认不能手动滑动Pager
	 * 
	 * @param isCanScroll
	 */
	public void setCanScroll(boolean isCanScroll) {
		canScroll = isCanScroll;
	}

	public PicturesPlayer(Context context) {
		this(context, null);
	}

	public PicturesPlayer(Context argContext, AttributeSet argAttrs) {
		this(argContext, argAttrs, 0);
	}

	public PicturesPlayer(Context argContext, AttributeSet argAttrs,
			int argDefStyle) {
		super(argContext, argAttrs, argDefStyle);
		mContext = argContext;
	}

	public void startPlayer(List<ImageView> listView) {
		closePlayer();
		initData(listView);
		initView(mContext);
		if (isAutoPlayBegin) {
			isAutoPlay = true;
			repeatPlay();
		}
	}

	public void closePlayer() {
		destoryBitmaps();
		stopPlay();
	}

	/**
	 * 初始化相关Data
	 */
	private void initData(List<ImageView> listView) {
		imageViewsList = listView;
	}

	/**
	 * 初始化Views等UI
	 */
	private void initView(Context context) {
		LayoutInflater.from(context)
				.inflate(R.layout.pictureplayer, this, true);
		viewPager = (MyViewPager) findViewById(R.id.viewPager);
		try {
			Field field = ViewPager.class.getDeclaredField("mScroller");
			field.setAccessible(true);
			FixedSpeedScroller scroller = new FixedSpeedScroller(context,
					new AccelerateInterpolator());
			field.set(viewPager, scroller);
			scroller.setmDuration(mDuration);
		} catch (Exception e) {
			e.printStackTrace();
		}
		viewPager.setFocusable(true);
		viewPager.setCanScroll(canScroll);
		viewPager.setAdapter(new MyPagerAdapter());
		viewPager.setOnPageChangeListener(new MyPageChangeListener());
	}

	/**
	 * 开始轮播图切换
	 */
	public void repeatPlay() {
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		scheduledExecutorService.scheduleAtFixedRate(new SlideShowTask(),
				timeInterval, timeInterval, TimeUnit.SECONDS);
	}

	/**
	 * 停止轮播图切换
	 */
	public void stopPlay() {
		if (scheduledExecutorService != null
				&& !scheduledExecutorService.isShutdown()) {
			scheduledExecutorService.shutdown();
		}
	}

	public void pausePlay() {
		mHandler.sendEmptyMessage(PAUSE_PLAY);
	}

	public void goOnPlay() {
		mHandler.sendEmptyMessage(GO_ON_PLAY);
	}

	public void nextPage() {
		mHandler.sendEmptyMessage(NEXT_PAGE_MANUAL);
	}

	public void prePage() {
		mHandler.sendEmptyMessage(PRE_PAGE_MANUAL);
	}

	private Handler mHandler = new PicturePlayerHandler(this);

	private static class PicturePlayerHandler extends Handler {
		private WeakReference<PicturesPlayer> wActivity;

		public PicturePlayerHandler(PicturesPlayer picturePlayer) {
			this.wActivity = new WeakReference<PicturesPlayer>(picturePlayer);
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == NEXT_PAGE_MANUAL || msg.what == PRE_PAGE_MANUAL) {
				wActivity.get().isAutoPlay = false;
				if (msg.what == NEXT_PAGE_MANUAL) {
					wActivity.get().currentItem = (wActivity.get().currentItem + 1)
							% wActivity.get().imageViewsList.size();
				} else if (msg.what == PRE_PAGE_MANUAL) {
					wActivity.get().currentItem = (wActivity.get().currentItem
							+ wActivity.get().imageViewsList.size() - 1)
							% wActivity.get().imageViewsList.size();
				}
				wActivity.get().viewPager
						.setCurrentItem(wActivity.get().currentItem);
				wActivity.get().isAutoPlay = true;
			} else if (msg.what == AUTO_PLAY) {
				Log.d("mydebug", "AUTO_PLAY");
				wActivity.get().currentItem = msg.arg1;
				wActivity.get().viewPager
						.setCurrentItem(wActivity.get().currentItem);
			} else if (msg.what == PAUSE_PLAY)// 暂停播放
			{
				if (wActivity.get().isAutoPlay) {
					wActivity.get().isAutoPlay = false;
				}
			} else if (msg.what == GO_ON_PLAY)// 继续播放
			{
				if (!wActivity.get().isAutoPlay) {
					wActivity.get().isAutoPlay = true;
				}
			}
		}
	}

	/**
	 * 填充ViewPager的页面适配器
	 */
	private class MyPagerAdapter extends PagerAdapter {

		@Override
		public void destroyItem(View container, int position, Object object) {
			// TODO Auto-generated method stub
			// ((ViewPag.er)container).removeView((View)object);
			((ViewPager) container).removeView(imageViewsList.get(position));
		}

		@Override
		public Object instantiateItem(View container, int position) {
			// TODO Auto-generated method stub
			((ViewPager) container).addView(imageViewsList.get(position));
			return imageViewsList.get(position);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return imageViewsList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public Parcelable saveState() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void finishUpdate(View arg0) {
			// TODO Auto-generated method stub

		}

	}

	/**
	 * ViewPager的监听器 当ViewPager中页面的状态发生改变时调用
	 */
	private class MyPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {// 当viewPager状态改变时
														// 三种状态的变化顺序为（1，2，0）
			// TODO Auto-generated method stub
			switch (arg0) {
			case 1:// 手势滑动，空闲中 滑动状态下回触发
				isAutoPlay = false;
				break;
			case 2:// 界面切换中 只要是切换都会触发 不管是否滑动
				break;
			case 0:// 滑动结束，即切换完毕或者加载完毕
					// 手动滑动结束后开启自动滑动
				if (!isAutoPlay) {
					isAutoPlay = true;
				}
				break;
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {// 当页面在滑动的时候会调用此方法
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageSelected(int pos) {// 此方法是页面跳转完后得到调用
			// TODO Auto-generated method stub
			currentItem = pos;
		}

	}

	/**
	 * 执行轮播图切换任务
	 */
	private class SlideShowTask implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			synchronized (viewPager) {
				if (isAutoPlay) {
					int nextItem = (currentItem + 1) % imageViewsList.size();
					if (nextItem == 0 && !isNeedRepeat) {// 如果已经是最后一个了且无需重复切换则停止自动播放
						stopPlay();
					} else {
						Message msg = new Message();
						msg.what = AUTO_PLAY;
						msg.arg1 = nextItem;// currentItem
						mHandler.sendMessage(msg);
					}
				}
			}
		}

	}

	/**
	 * 销毁ImageView资源，回收内存
	 */
	private void destoryBitmaps() {
		if (imageViewsList == null) {
			return;
		}
		for (int i = 0; i < imageViewsList.size(); i++) {
			ImageView imageView = imageViewsList.get(i);
			Drawable drawable = imageView.getDrawable();
			if (drawable != null) {
				// 解除drawable对view的引用
				drawable.setCallback(null);
			}
		}
	}
}
