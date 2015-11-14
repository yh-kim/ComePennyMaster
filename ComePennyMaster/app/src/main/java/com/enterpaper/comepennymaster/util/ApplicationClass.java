package com.enterpaper.comepennymaster.util;

import android.app.Application;
import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class ApplicationClass extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		init(this);
	}

	public void init(Context ctx){
		//image 옵션 설정
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(null)		//loading중일때 나오는 Image
				.showImageForEmptyUri(null)    //Image를 요청했을때 없을때 나오는 Image
				.showImageOnFail(null)                //Image를 요청했지만 에러가 생겼을 떄 나오는 Image
				.cacheInMemory(true)            //memory cache 사용하겠다
				.cacheOnDisc(true)                //File cache 사용
				.considerExifParams(true)	//가로 사진이면 가로로 세로사진이면 세로로
				.build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(ctx)
				.threadPriority(Thread.NORM_PRIORITY - 2)		//쓰레드의 우선순위를 nomal 보다 2낮게
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())		//File cache의 이름을 저장하는 방식
				.tasksProcessingOrder(QueueProcessingType.LIFO)    //Thread Pool을 어떻게 작동시킬꺼냐(스택형식으로 사용)
				.defaultDisplayImageOptions(options)
				.build();

		ImageLoader.getInstance().init(config);

	}

}
