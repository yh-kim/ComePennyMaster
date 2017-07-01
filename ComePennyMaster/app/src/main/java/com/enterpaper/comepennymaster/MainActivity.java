package com.enterpaper.comepennymaster;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.enterpaper.comepennymaster.util.DataUtil;
import com.enterpaper.comepennymaster.util.SetFont;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.extras.SoundPullEventListener;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {
    int selectedItem;
    int row_cnt = 6;
    int count = 0;
    int offset = 0;
    boolean is_scroll = false;
    private PullToRefreshListView mPullRefreshListView;
    private ArrayList<IdeaListItem> arr_list = new ArrayList<>();
    private ListAdapter adapters;
    Toolbar mToolBar;
    String booth_name;
    String name[] = {"게임","공부","도전","독서","애니","예술","브랜드","사랑",
            "스포츠","시간","여행","영화","오글오글","음악","이별","인생","종교","창업",
            "취업","친구","희망","기타"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DataUtil.setAppPreferences(getApplicationContext(),"master_id","0");

        //TextView 폰트 지정
        SetFont.setGlobalFont(this, getWindow().getDecorView());

        //Toolbar 생성
        initializeToolbar();

        // 레이아웃 객체 생성
        initializeLayout();

        // 리스너 설정
        setListener();


        //초기화 & 쓰레드 실행
        initializationList();
    }

    //Initlist (초기화 메소드)
    private void initializationList() {
        //초기화
        is_scroll = false;
        offset = 0;
        arr_list.clear();

        //쓰레드 실행
        new NetworkGetIdeaList().execute("");
        return;
    }

    private void initializeLayout(){
        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);

        /**
         * Add Sound Event Listener
         */
        SoundPullEventListener<ListView> soundListener = new SoundPullEventListener<ListView>(this);

        /* 효과음
        soundListener.addSoundEvent(PullToRefreshBase.State.PULL_TO_REFRESH, R.raw.pull_event);
        soundListener.addSoundEvent(PullToRefreshBase.State.RESET, R.raw.reset_sound);
        soundListener.addSoundEvent(PullToRefreshBase.State.REFRESHING, R.raw.refreshing_sound);
         */

        mPullRefreshListView.setOnPullEventListener(soundListener);

        // Adapter 생성
        adapters = new ListAdapter(getApplicationContext(), R.layout.row_idea, arr_list);

        // Adapter와 GirdView를 연결
        mPullRefreshListView.setAdapter(adapters);
    }

    private void setListener(){
        mPullRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedItem = position - 1;
                Intent booth_ideas = new Intent(getApplicationContext(), IdeaDetailActivity.class);
                booth_ideas.putExtra("idea_id", arr_list.get(position - 1).getIdea_id());//헤더를 position0으로인식하기때문
                booth_ideas.putExtra("email",arr_list.get(position-1).getEmail());
//                startActivity(booth_ideas);
                startActivityForResult(booth_ideas,0);
                overridePendingTransition(0, 0);
            }

        });

        mPullRefreshListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {


            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if ((firstVisibleItem + visibleItemCount) > totalItemCount - 2) {
                    //서버로부터 받아온 List개수를 count
                    //지금까지 받아온 개수를 offset
                    if (count != 0) {
                        if (is_scroll) {
                            //스크롤 멈추게 하는거
                            is_scroll = false;
                            new NetworkGetIdeaList().execute("");
                        }
                    }
                }
            }
        });


        // Set a listener to be invoked when the list should be refreshed.
        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                // Update the LastUpdatedLabel
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

                // Do work to refresh the list here.
                new NetworkGetIdeaRefresh().execute("");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            // 일반적 상황 (조회수, 좋아요수, 댓글수, 컨텐츠 업데이트)
            case 1:
                String backContent = data.getStringExtra("backContent");
                int backView = data.getIntExtra("backView", 0);
                int backComment = data.getIntExtra("backComment", 0);
                int backLike = data.getIntExtra("backLike",0);

                IdeaListItem backItem = arr_list.get(selectedItem);
                backItem.setContent(backContent);
                backItem.setViewCount(backView);
                backItem.setCommentCount(backComment);
                backItem.setLikeCount(backLike);

                adapters.notifyDataSetChanged();
                break;

            // 삭제된 상황 (아이템 지우기)
            case 2:
                arr_list.remove(selectedItem);
                adapters.notifyDataSetChanged();
                break;
        }
    }

    private void initializeToolbar() {
        //액션바 객체 생성
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        //액션바 설정
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        //액션바 숨김
        actionBar.hide();

        //툴바 설정
        mToolBar = (Toolbar) findViewById(R.id.main_toolbar);
        mToolBar.setContentInsetsAbsolute(0, 0);
    }


    class NetworkGetIdeaRefresh extends AsyncTask<String, String, Integer> {
        private String err_msg = "Network error.";

        // JSON에서 받아오는 객체
        private JSONObject jObjects;

        // AsyncTask 실행되는거
        @Override
        protected Integer doInBackground(String... params) {

            return processing();
        }


        // AsyncTask 실행완료 후에 구동 (Data를 받은것을 Activity에 갱신하는 작업을 하면돼)
        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);


            // 지금 코드에서는 result가 0이면 정상적인 상황
            if (result == 0) {
                Log.i("Network Data", jObjects.toString());

                // JSON에서 받은 객체를 가지고 List에 뿌려줘야해
                // jObject에서 데이터를 뽑아내자
                try {
                    if(offset >0){
                        if(arr_list.get(0).getIdea_id() == jObjects.getJSONArray("ret").getJSONObject(0).getInt("id")){
                            mPullRefreshListView.onRefreshComplete();
                            is_scroll = true;
                            return;
                        }
                    }

                    // 가져오는 값의 개수를 가져옴
                    count = jObjects.getInt("cnt");
                    offset = offset + count;
                    JSONArray ret_arr = jObjects.getJSONArray("ret");
                    for (int index = 0; index < ret_arr.length(); index++) {
                        JSONObject obj_boothIdeas = ret_arr.getJSONObject(index);

                        int idea_id = obj_boothIdeas.getInt("id");
                        String content = obj_boothIdeas.getString("content");
                        int hit = obj_boothIdeas.getInt("hit");
                        int comment_num = obj_boothIdeas.getInt("comment_num");
                        int like_num = obj_boothIdeas.getInt("like_num");
                        int booth_id = obj_boothIdeas.getInt("booth_id");
                        String img_url = booth_id+"";
                        String getemail = obj_boothIdeas.getString("email");

                        booth_name = name[booth_id - 1];

                        // Item 객체로 만들어야함
                        IdeaListItem items = new IdeaListItem(img_url, content, getemail,booth_name, hit,comment_num, like_num, idea_id);

                        // Item 객체를 ArrayList에 넣는다
                        arr_list.add(0,items);


                        // Adapter에게 데이터를 넣었으니 갱신하라고 알려줌
                        adapters.notifyDataSetChanged();
                    }

                    // Call onRefreshComplete when the list has been refreshed.
                    mPullRefreshListView.onRefreshComplete();

                    // scroll 할 수 있게함
                    is_scroll = true;

                } catch (JSONException e) {
                    mPullRefreshListView.onRefreshComplete();
                    e.printStackTrace();
                }
                return;
            }
            // Error 상황
            else {
                mPullRefreshListView.onRefreshComplete();
                Toast.makeText(getApplicationContext(), "Error",
                        Toast.LENGTH_SHORT).show();
            }
        }

        private Integer processing() {
            try {
                HttpClient http_client = new DefaultHttpClient();
                // 요청한 후 7초 이내에 오지 않으면 timeout 발생하므로 빠져나옴
                http_client.getParams().setParameter("http.connection.timeout",
                        7000);

                // data를 Post방식으로 보냄
                HttpPost http_post = null;

                List<NameValuePair> name_value = new ArrayList<NameValuePair>();

                http_post = new HttpPost(
                        "http://54.199.176.234/api/get_idea_list.php");

                int refresh_id = arr_list.get(0).getIdea_id();

//                        //서버에 보낼 데이터
                // data를 담음
                name_value.add(new BasicNameValuePair("offset", offset + ""));
                name_value.add(new BasicNameValuePair("refresh_id", refresh_id+""));

                UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(
                        name_value, "UTF-8");
                http_post.setEntity(entityRequest);

                // 실행
                HttpResponse response = http_client.execute(http_post);

                // 받는 부분
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                response.getEntity().getContent(), "UTF-8"), 8);
                StringBuilder builder = new StringBuilder();
                for (String line = null; (line = reader.readLine()) != null; ) {
                    builder.append(line).append("\n");
                }

                // 우리가 사용하는 결과
                jObjects = new JSONObject(builder.toString());

                // err가 0이면 정상적인 처리
                // err가 0이 아닐시 오류발생
                if (jObjects.getInt("err") > 0) {
                    return jObjects.getInt("err");
                }
            } catch (Exception e) {
                // 오류발생시
                Log.i(err_msg, e.toString());
                return 100;
            }
            return 0;
        }

    }

    class NetworkGetIdeaList extends AsyncTask<String, String, Integer> {
        private String err_msg = "Network error.";

        // JSON에서 받아오는 객체
        private JSONObject jObjects;

        // AsyncTask 실행되는거
        @Override
        protected Integer doInBackground(String... params) {

            return processing();
        }


        // AsyncTask 실행완료 후에 구동 (Data를 받은것을 Activity에 갱신하는 작업을 하면돼)
        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);


            // 지금 코드에서는 result가 0이면 정상적인 상황
            if (result == 0) {
                Log.i("Network Data", jObjects.toString());

                // JSON에서 받은 객체를 가지고 List에 뿌려줘야해
                // jObject에서 데이터를 뽑아내자
                try {

                    // 가져오는 값의 개수를 가져옴
                    count = jObjects.getInt("cnt");
                    offset = offset + count;
                    JSONArray ret_arr = jObjects.getJSONArray("ret");
                    for (int index = 0; index < ret_arr.length(); index++) {
                        JSONObject obj_boothIdeas = ret_arr.getJSONObject(index);

                        int idea_id = obj_boothIdeas.getInt("id");
                        String content = obj_boothIdeas.getString("content");
                        int hit = obj_boothIdeas.getInt("hit");
                        int comment_num = obj_boothIdeas.getInt("comment_num");
                        int like_num = obj_boothIdeas.getInt("like_num");
                        int booth_id = obj_boothIdeas.getInt("booth_id");
                        String img_url = booth_id+"";
                        String getemail = obj_boothIdeas.getString("email");

                        booth_name = name[booth_id - 1];

                        // Item 객체로 만들어야함
                        IdeaListItem items = new IdeaListItem(img_url, content, getemail,booth_name, hit,comment_num, like_num, idea_id);

                        // Item 객체를 ArrayList에 넣는다
                        arr_list.add(items);


                        // Adapter에게 데이터를 넣었으니 갱신하라고 알려줌
                        adapters.notifyDataSetChanged();
                    }

                    // Call onRefreshComplete when the list has been refreshed.
                    mPullRefreshListView.onRefreshComplete();

                    // scroll 할 수 있게함
                    is_scroll = true;

                } catch (JSONException e) {
                    mPullRefreshListView.onRefreshComplete();
                    e.printStackTrace();
                }
                return;
            }
            // Error 상황
            else {
                Toast.makeText(getApplicationContext(), "Error",
                        Toast.LENGTH_SHORT).show();
            }
        }

        private Integer processing() {
            try {
                HttpClient http_client = new DefaultHttpClient();
                // 요청한 후 7초 이내에 오지 않으면 timeout 발생하므로 빠져나옴
                http_client.getParams().setParameter("http.connection.timeout",
                        7000);

                // data를 Post방식으로 보냄
                HttpPost http_post = null;

                List<NameValuePair> name_value = new ArrayList<NameValuePair>();

                http_post = new HttpPost(
                        "http://54.199.176.234/api/get_idea_list.php");

//                        //서버에 보낼 데이터
                // data를 담음
                name_value.add(new BasicNameValuePair("offset", offset + ""));
                name_value.add(new BasicNameValuePair("master", "1"));

                UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(
                        name_value, "UTF-8");
                http_post.setEntity(entityRequest);

                // 실행
                HttpResponse response = http_client.execute(http_post);

                // 받는 부분
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                response.getEntity().getContent(), "UTF-8"), 8);
                StringBuilder builder = new StringBuilder();
                for (String line = null; (line = reader.readLine()) != null; ) {
                    builder.append(line).append("\n");
                }

                // 우리가 사용하는 결과
                jObjects = new JSONObject(builder.toString());

                // err가 0이면 정상적인 처리
                // err가 0이 아닐시 오류발생
                if (jObjects.getInt("err") > 0) {
                    return jObjects.getInt("err");
                }
            } catch (Exception e) {
                // 오류발생시
                Log.i(err_msg, e.toString());
                return 100;
            }
            return 0;
        }

    }

}
