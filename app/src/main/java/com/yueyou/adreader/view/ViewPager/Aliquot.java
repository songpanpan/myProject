package com.yueyou.adreader.view.ViewPager;

import java.io.Serializable;

public class Aliquot implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1201661092364954156L;
	public String mContent;
	public int    mSrcRightDrawableId;
	public int    mSrcLeftDrawableId;
	public int 	  mBackgroundId;
	
	public int 	  mAliquotId;
	public int 	  mGravity;
	public int 	  mAliquotValue;
	public String mTipContent;
	public int 	  mTipBackgroundId;
	public int 	  mAliquotColor;
	
	public Aliquot(String mContent, int mBackgroundId, int mAliquotId) {
		super();
		this.mContent 		= mContent;
		this.mBackgroundId 	= mBackgroundId;
		this.mAliquotId 	= mAliquotId;
	}

	public Aliquot(int srcRightDrawableId , int mBackgroundId, int mAliquotId) {
		super();
		this.mSrcRightDrawableId = srcRightDrawableId;
		this.mBackgroundId  = mBackgroundId;
		this.mAliquotId     = mAliquotId;
	}
	
}
