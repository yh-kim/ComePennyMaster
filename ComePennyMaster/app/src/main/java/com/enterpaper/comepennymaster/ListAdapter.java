package com.enterpaper.comepennymaster;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
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

public class ListAdapter extends ArrayAdapter<IdeaListItem> {
    //LayoutInflater -> XML을 동적으로 만들 때 필요
    private LayoutInflater inflater = null;
    //Context -> Activity Class의 객체
    private Context context = null;
    ImageLoader loader;

    public ListAdapter(Context context, int resource, ArrayList<IdeaListItem> objects) {
        super(context, resource, objects);
        //context는 함수를 호출한 activiy
        //resource는 row_xxx.xml 의 정보
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        loader = ImageLoader.getInstance();
    }


    //ArrayList에 저장되어있는 데이터를 fragment에 넣는 method
    //List 하나마다 getView가 한번 실행된다
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //  aq = new AQuery(convertView);
        //position -> List번호
        ViewHolder holder;

        //XML 파일이 비어있는 상태라면
        if (convertView == null) {
            //layout 설정
            convertView = inflater.inflate(R.layout.row_idea, null);
            //TextView 폰트 지정
            SetFont.setGlobalFont(context, convertView);

            holder = new ViewHolder();

            //row에 있는 정보들을 holder로 가져옴
            //  holder.img = (ImageView) convertView.findViewById(R.id.iv_company);
            holder.title = (TextView) convertView.findViewById(R.id.tv_idea);
            holder.Email = (TextView) convertView.findViewById(R.id.tv_UserId);
            holder.ViewCount = (TextView) convertView.findViewById(R.id.tv_count_view);
            holder.commentCount = (TextView) convertView.findViewById(R.id.tv_count_comment);
            holder.LikeCount = (TextView) convertView.findViewById(R.id.tv_count_like);
            holder.Booth_name=(TextView)convertView.findViewById(R.id.tv_row_booth_name);
            convertView.setTag(holder);
        }


        holder = (ViewHolder) convertView.getTag();

        IdeaListItem item = getItem(position);

        String contents =item.getContent();
        holder.title.setText(contents);
        int maxLines = 14;
        holder.title.setMaxLines(maxLines);
        if ( holder.title.getLineCount() > maxLines){
            int lastCharShown =  holder.title.getLayout().getLineVisibleEnd(maxLines - 2);
            String showString = contents.substring(0, lastCharShown);
            holder.title.setText(showString + "\n …");
        }
        holder.Booth_name.setText(item.getBooth_name());
        holder.Email.setText(item.getEmail());
        holder.ViewCount.setText(item.getViewCount() + "");
        holder.commentCount.setText(item.getCommentCount() + "");
        holder.LikeCount.setText(item.getLikeCount() + "");

        return convertView;
    }

    class ViewHolder {
        ImageView img;
        TextView title, UserId, ViewCount,commentCount, LikeCount, Email,Booth_name;
    }

}
