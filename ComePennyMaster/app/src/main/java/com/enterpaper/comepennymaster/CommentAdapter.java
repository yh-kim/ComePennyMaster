package com.enterpaper.comepennymaster;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.enterpaper.comepennymaster.util.SetFont;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Kim on 2015-07-14.
 */

public class CommentAdapter extends ArrayAdapter<CommentItem> {
    //LayoutInflater -> XML을 동적으로 만들 때 필요
    private LayoutInflater inflater = null;
    //Context -> Activity Class의 객체
    private Context context = null;
    ImageLoader loader;


    public CommentAdapter(Context context, int resource, ArrayList<CommentItem> objects) {
        super(context, resource, objects);

        //context는 함수를 호출한 activiy
        //resource는 row_xxx.xml 의 정보
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        loader = ImageLoader.getInstance();
    }


    //ArrayList에 저장되어있는 데이터를 fragment에 넣는 method
    //List 하나마다 getView가 한번 실행된다
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //position -> List번호
        ViewHolder holder;

        //XML 파일이 비어있는 상태라면
        if (convertView == null) {
            //layout 설정
            convertView = inflater.inflate(R.layout.row_comment, null);
            //TextView 폰트 지정
            SetFont.setGlobalFont(context, convertView);

            holder = new ViewHolder();

            //row에 있는 정보들을 holder로 가져옴
            holder.img = (ImageView) convertView.findViewById(R.id.iv_comment_basic);
            holder.UserId = (TextView) convertView.findViewById(R.id.tv_comment_userid);
            holder.Comment_Content = (TextView) convertView.findViewById(R.id.tv_comment);
            holder.Comment_time = (TextView) convertView.findViewById(R.id.tv_comment_time);

            convertView.setTag(holder);
        }


        holder = (ViewHolder) convertView.getTag();

        CommentItem item = getItem(position);

        // holder.img.setImageBitmap(item.getImg());
        holder.UserId.setText(item.getEmail());
        holder.Comment_Content.setText(item.getComment_content());
        holder.Comment_time.setText(item.getComment_time());

        if (!item.getUser_comment_img().contains("null")) {
            loader.displayImage("https://s3-ap-northeast-1.amazonaws.com/comepenny/" + item.getUser_comment_img(), holder.img);
        }else{
            loader.displayImage("https://s3-ap-northeast-1.amazonaws.com/comepenny/myinfo_userimage.png", holder.img);
        }

        return convertView;
    }

    class ViewHolder {
        ImageView img;
        TextView UserId, Comment_Content, Comment_time;
    }

}
