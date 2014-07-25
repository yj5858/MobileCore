/**
 * @file XFooterView.java
 * @create Mar 31, 2012 9:33:43 PM
 * @author Maxwin
 * @description XListView's footer
 */
package com.mobile.core.views.mlistview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobile.core.R;

public class MListViewFooter extends LinearLayout {
	public final static int STATE_NORMAL = 0;
	public final static int STATE_READY = 1;
	public final static int STATE_LOADING = 2;
	public final static int STATE_COMPLETE = 3;
	
	private int mState = STATE_NORMAL;

	private Context mContext;
	private LinearLayout moreView;
	private RelativeLayout mContentView;
	private ProgressBar mProgressBar;
	private TextView mHintView;
	private ImageView mArrowImageView;
	
	private Animation mRotateUpAnim;
	private Animation mRotateDownAnim;

	private final int ROTATE_ANIM_DURATION = 180;
	
	public MListViewFooter(Context context) {
		super(context);
		initView(context);
	}
	
	public MListViewFooter(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	
	public void setState(int state) {
		if(mState == state){
			return;
		}
//		mHintView.setVisibility(View.INVISIBLE);
//		mProgressBar.setVisibility(View.INVISIBLE);
//		mHintView.setVisibility(View.INVISIBLE);
		
//		mContentView.setVisibility(View.INVISIBLE);
		if (state == STATE_READY) {
		//	mHintView.setVisibility(View.VISIBLE);
			
//			mContentView.setVisibility(View.INVISIBLE);
			mArrowImageView.clearAnimation();//这个地方不clear对自身的动画没有影响，没有clear这个动画应该会在内存中呆一段时间，这样会影响之后的setVisibility
												//所以最好是在每次动画完成后clear,感觉一次clear只能clear一个动画
			mArrowImageView.startAnimation(mRotateDownAnim);
			mHintView.setText(R.string.xlistview_footer_hint_ready);
			
		} else if (state == STATE_LOADING) {
			mArrowImageView.clearAnimation();
			mArrowImageView.setVisibility(View.INVISIBLE);
			mProgressBar.setVisibility(View.VISIBLE);
			mHintView.setText(R.string.xlistview_footer_hint_loading);
		} else if(state == STATE_NORMAL){
			if(mState == STATE_LOADING){
				mArrowImageView.clearAnimation();//这个地方必须clear，不然下面的setVisibility没有效果
				mArrowImageView.setVisibility(View.VISIBLE);
				mProgressBar.setVisibility(View.INVISIBLE);
				mHintView.setText(R.string.xlistview_footer_hint_normal);
			}
			if(mState == STATE_READY){
				mArrowImageView.clearAnimation();
				mArrowImageView.startAnimation(mRotateUpAnim);
				mHintView.setText(R.string.xlistview_footer_hint_normal);
			}
			//mHintView.setVisibility(View.INVISIBLE);
			//mHintView.setText(R.string.xlistview_footer_hint_normal);
			mProgressBar.setVisibility(View.INVISIBLE);
		//	mContentView.setVisibility(View.INVISIBLE);
		}else{
			mHintView.setText(R.string.xlistview_footer_hint_complete);
			mProgressBar.setVisibility(View.INVISIBLE);
		}
		mState = state;
	}
	
	public void setBottomMargin(int height) {
		if (height < 0)  
			height = 0;
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)moreView.getLayoutParams();
		lp.height = height;
		moreView.setLayoutParams(lp);
	}
	
	public int getBottomMargin() {
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)moreView.getLayoutParams();
		return moreView.getHeight();
	}
	
	
	/**
	 * normal status
	 */
	public void normal() {
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)moreView.getLayoutParams();
		lp.height = 0;
		moreView.setLayoutParams(lp);
//		mHintView.setVisibility(View.INVISIBLE);
//		mProgressBar.setVisibility(View.GONE);
		
//		mContentView.setVisibility(View.INVISIBLE);
	}
	
	
	/**
	 * loading status 
	 */
	public void loading() {
	//	mHintView.setVisibility(View.GONE);
		mProgressBar.setVisibility(View.VISIBLE);
	}
	
	/**
	 * hide footer when disable pull load more
	 */
	public void hide() {
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)moreView.getLayoutParams();
		lp.height = 0;
		moreView.setLayoutParams(lp);
		
//		mContentView.setVisibility(View.INVISIBLE);
	}
	
	/**
	 * show footer
	 */
	public void show() {
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)moreView.getLayoutParams();
		lp.height = LayoutParams.WRAP_CONTENT;
		moreView.setLayoutParams(lp);
//		mContentView.setVisibility(View.VISIBLE);
	}
	
	private void initView(Context context) {
		mContext = context;
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, 0);
		
		
		moreView = (LinearLayout)LayoutInflater.from(mContext).inflate(R.layout.xlistview_footer, null);
		addView(moreView,lp);
		//setGravity(Gravity.BOTTOM);
		//moreView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		
		mContentView = (RelativeLayout) moreView.findViewById(R.id.xlistview_footer_content);
		mProgressBar = (ProgressBar) moreView.findViewById(R.id.xlistview_footer_progressbar);
		mHintView = (TextView)moreView.findViewById(R.id.xlistview_footer_hint_textview);
		
		mArrowImageView = (ImageView) findViewById(R.id.xlistview_foot_arrow);
		mRotateDownAnim = new RotateAnimation(0.0f, -180.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
		mRotateDownAnim.setFillAfter(true);
		
		mRotateUpAnim = new RotateAnimation(-180.0f, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
		mRotateUpAnim.setFillAfter(true);
	}
	
	
}
