package com.enterpaper.comepennymaster;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.enterpaper.comepennymaster.util.DataUtil;
import com.enterpaper.comepennymaster.util.SetFont;
import com.nostra13.universalimageloader.core.ImageLoader;

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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class IdeaDetailActivity extends ActionBarActivity {
    int row_cnt = 6;
    int count = 0;
    int offset = 0;
    int commentDelPosition;
    boolean is_scroll = true;
    //  private String msg, reg_Time, regTime_str;
    InputMethodManager keyboard;
    Toolbar mToolBar;
    ImageView btn_ideaback, iv_comment_basic, iv_boothicon;
    ListView lvIdeaDetailComment;
    ImageButton btn_pick;
    EditText Edit_reple, Edit_reple_adjust;
    TextView tv_logo_name, tv_Writer, tv_view, tv_like, tv_ideaoriginal, tv_commentcount, tv_time, btn_reple, btn_del, btn_reple_update, btn_reple_cancel;
    int pick_boolean = 0;
    View header;
    int idea_id, booth_id, comment_id;
    String email, content, user_id, user_email, writer_email, content_idea, content_reple, user_comment_img;
    AlertDialog mDialog;
    CommentAdapter adapters;
    ArrayList<CommentItem> arr_list = new ArrayList<>();
    boolean is_adjust_check = false;
    private ScrollView scrollView_mainidea_detail;
    ImageLoader loader;

    public static String formatTimeString(String str) throws ParseException {

        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        java.util.Date date = format.parse(str);

        long curTime = System.currentTimeMillis();
        long regTime = date.getTime();
        long diffTime = (curTime - regTime) / 1000;

        String msg = null;
        if (diffTime < TIME_MAXIMUM.SEC) {
// sec
            msg = "방금 전";
        } else if ((diffTime /= TIME_MAXIMUM.SEC) < TIME_MAXIMUM.MIN) {
// min
            msg = diffTime + "분 전";
        } else if ((diffTime /= TIME_MAXIMUM.MIN) < TIME_MAXIMUM.HOUR) {
// hour
            msg = (diffTime) + "시간 전";
        } else if ((diffTime /= TIME_MAXIMUM.HOUR) < TIME_MAXIMUM.DAY) {
// day
            msg = (diffTime) + "일 전";
        } else if ((diffTime /= TIME_MAXIMUM.DAY) < TIME_MAXIMUM.MONTH) {
// day
            msg = (diffTime) + "달 전";
        } else {
            msg = str;
        }
        return msg;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idea_detail);
        loader = ImageLoader.getInstance();
        //idea_id받기
        Intent itReceive = getIntent();
        idea_id = itReceive.getExtras().getInt("idea_id");
        email = itReceive.getExtras().getString("email");

        //TextView 폰트 지정
        SetFont.setGlobalFont(this, getWindow().getDecorView());

        //Toolbar 생성
        initializeToolbar();

        // 레이아웃 객체 생성
        initializeLayout();

        // 헤더 설정
        lvIdeaDetailComment.addHeaderView(header);

        // Adapter 생성
        adapters = new CommentAdapter(getApplicationContext(), R.layout.row_comment, arr_list);

        // Adapter와 GirdView를 연결
        lvIdeaDetailComment.setAdapter(adapters);
        adapters.notifyDataSetChanged();

        // 액션 리스너 생성
        initializeListener();

        //      new NetworkGetIdeainfo().execute();
        new NetworkGetCommentList().execute();

    }

    //다른 activity에 갔다가 돌아왔을때 실행되는 코드, onCreate()실행되고 뭐 실행되고 뭐실행되고 실행되는게 onResume()
    public void onResume() {
        super.onResume();

        //초기화 & 쓰레드 실행
        initializationContent();

    }

    //Initlist (초기화 메소드)
    private void initializationContent() {
        //초기화
        tv_ideaoriginal.setText("");
        arr_list.clear();
        offset = 0;


        //쓰레드 실행

        new NetworkGetIdeainfo().execute("");
        new NetworkGetCommentList().execute("");


        return;
    }

    // layout
    private void initializeLayout() {
        //스크린키보드
        keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        // 리스트 헤더 부분
        header = getLayoutInflater().inflate(R.layout.activity_idea_detail_header, null, false);

        scrollView_mainidea_detail = (ScrollView) header.findViewById(R.id.scrollView_mainidea_detail);
        btn_pick = (ImageButton) header.findViewById(R.id.btn_pick);
        tv_Writer = (TextView) header.findViewById(R.id.tv_Writer);
        tv_view = (TextView) header.findViewById(R.id.tv_view);
        tv_like = (TextView) header.findViewById(R.id.tv_like);
        tv_time = (TextView) header.findViewById(R.id.tv_time);
        btn_del = (TextView) header.findViewById(R.id.btn_del);
        tv_ideaoriginal = (TextView) header.findViewById(R.id.tv_ideaoriginal);
        tv_commentcount = (TextView) header.findViewById(R.id.tv_comment_view);
        btn_reple = (TextView) header.findViewById(R.id.btn_reple);
        iv_boothicon = (ImageView) header.findViewById(R.id.iv_boothicon);
        // 리스트부분
        lvIdeaDetailComment = (ListView) findViewById(R.id.lv_idea_detail_comments);
        btn_ideaback = (ImageView) findViewById(R.id.btn_ideaback);
        tv_logo_name = (TextView) findViewById(R.id.tv_logo_name);
        Edit_reple = (EditText) header.findViewById(R.id.Edit_reple);

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
        mToolBar = (Toolbar) findViewById(R.id.idea_detail_toolbar);
        mToolBar.setContentInsetsAbsolute(0, 0);
    }

    private void initializeListener() {
        btn_ideaback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, 0);
            }
        });


        btn_reple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                content = Edit_reple.getText().toString().trim();

                if (content.length() == 0) {
                    Toast.makeText(getApplicationContext(), "내용을 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 서버에 저장
                new NetworkaddComment().execute();

            }
        });
        btn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                // mDialog = createDialog();
                final CharSequence[] items = {"수정하기", "삭제하기"};

                AlertDialog.Builder builder = new AlertDialog.Builder(IdeaDetailActivity.this);     // 여기서 this는 Activity의 this

// 여기서 부터는 알림창의 속성 설정
                builder.setTitle("글을 수정/삭제 하시겠습니까?")        // 제목 설정
                        .setItems(items, new DialogInterface.OnClickListener() {    // 목록 클릭시 설정
                            public void onClick(DialogInterface dialog1, int index) {
                                // int형으로 조건 지정
                                switch (index) {
                                    case 0:
                                        new NetworkIdeaAdjustWrite().execute();
                                        break;
                                    case 1:
                                        AlertDialog.Builder builder = new AlertDialog.Builder(IdeaDetailActivity.this);
                                        builder.setTitle("삭제 확인")        // 제목 설정
                                                .setMessage("이 글을 삭제하시겠습니까?")        // 메세지 설정
                                                .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                                                .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                                                    // 확인 버튼 클릭시 설정
                                                    public void onClick(DialogInterface dialog, int whichButton) {
                                                        new NetworkIdeaDel().execute();
                                                        finish();

                                                    }
                                                })
                                                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                                    // 취소 버튼 클릭시 설정
                                                    public void onClick(DialogInterface dialog, int whichButton) {
                                                        dialog.cancel();
                                                    }
                                                });

                                        AlertDialog dialog = builder.create();    // 알림창 객체 생성
                                        dialog.show();    // 알림창 띄우기

                                        break;

                                    default:
                                        dialog1.cancel();
                                        break;
                                }

                            }
                        });

                AlertDialog dialog1 = builder.create();    // 알림창 객체 생성
                dialog1.show();    // 알림창 띄우기

            }
        });
        btn_pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (pick_boolean == 0) {
                    btn_pick.setBackgroundResource(R.drawable.detail_pickbutton_after);
                    pick_boolean = 1;
                    new NetworkGetlike().execute();


                } else {
                    btn_pick.setBackgroundResource(R.drawable.detail_pickbutton_before);
                    pick_boolean = 0;
                    new NetworkGetlike().execute();
                }

            }
        });


        lvIdeaDetailComment.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                commentDelPosition = position - 1;

                    final CharSequence[] items = {"댓글 수정하기", "댓글 삭제하기"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(IdeaDetailActivity.this);     // 여기서 this는 Activity의 this

                    // 여기서 부터는 알림창의 속성 설정
                    builder.setItems(items, new DialogInterface.OnClickListener() {    // 목록 클릭시 설정
                                public void onClick(DialogInterface dialog, int index) {
                                    // int형으로 조건 지정
                                    switch (index) {
                                        case 0:
                                            mDialog = Comment_createDialog();
                                            break;
                                        case 1:
                                            AlertDialog.Builder builder = new AlertDialog.Builder(IdeaDetailActivity.this);
                                            builder.setTitle("삭제 확인")
                                                    .setMessage("이 글을 삭제하시겠습니까?")        // 메세지 설정
                                                    .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                                                    .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                                                                // 확인 버튼 클릭시 설정
                                                                public void onClick(DialogInterface dialog_del, int whichButton) {
                                                                    new NetworkCommentDel().execute();
                                                                }
                                                            }
                                                    )
                                                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                                                // 취소 버튼 클릭시 설정
                                                                public void onClick(DialogInterface dialog_del, int whichButton) {
                                                                    dialog_del.cancel();
                                                                }
                                                            }
                                                    );
                                            AlertDialog dialog_del = builder.create();    // 알림창 객체 생성
                                            dialog_del.show();    // 알림창 띄우기
                                            break;
                                        default:
                                            dialog.cancel();
                                            break;
                                    }
                                }
                            }
                    );
                    AlertDialog dialog = builder.create();    // 알림창 객체 생성
                    dialog.show();    // 알림창 띄우기
                return false;
            }
        });

        lvIdeaDetailComment.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if ((firstVisibleItem + visibleItemCount) == totalItemCount) {
                    //서버로부터 받아온 List개수를 count
                    //지금까지 받아온 개수를 offset
                    if (count != 0 && offset > 4 && offset % row_cnt == 0) {
                        if (is_scroll) {
                            //스크롤 멈추게 하는거
                            is_scroll = false;
                            new NetworkGetCommentList().execute("");
                        }
                    }
                }
            }
        });

    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(0, 0);
    }

    //dialog
    private AlertDialog Comment_createDialog() {
        final View innerView = getLayoutInflater().inflate(R.layout.dialog_comment, null);
        iv_comment_basic = (ImageView) innerView.findViewById(R.id.iv_comment_basic);
        Edit_reple_adjust = (EditText) innerView.findViewById(R.id.Edit_reple_adjust);
        btn_reple_update = (TextView) innerView.findViewById(R.id.btn_reple_update);
        btn_reple_cancel = (TextView) innerView.findViewById(R.id.btn_reple_cancel);


        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setView(innerView);
        //ab.setTitle("댓글 수정하기");
        ab.setCancelable(true);

        final Dialog mDialog = ab.create();
        ///클릭리스너
        if (!arr_list.get(commentDelPosition).getUser_comment_img().contains("null")) {
            loader.displayImage("https://s3-ap-northeast-1.amazonaws.com/comepenny/" + arr_list.get(commentDelPosition).getUser_comment_img(), iv_comment_basic);
        } else {
            iv_comment_basic.setBackgroundResource(R.drawable.myinfo_userimage);
        }

        Edit_reple_adjust.setText(arr_list.get(commentDelPosition).getComment_content());
        Edit_reple_adjust.setSelection(Edit_reple_adjust.length()); //커서를 끝에 위치!
        Edit_reple_adjust.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (arr_list.get(commentDelPosition).getComment_content().equals(s.toString())) {
                    btn_reple_update.setVisibility(View.INVISIBLE);
                    is_adjust_check = false;

                } else {
                    btn_reple_update.setVisibility(View.VISIBLE);
                    is_adjust_check = true;

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btn_reple_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                content_reple = Edit_reple_adjust.getText().toString().trim();
                if (content_reple.length() == 0) {
                    Toast.makeText(getApplicationContext(), "내용을 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!is_adjust_check) {
                    return;
                }

                new NetworkCommentAdjustWrite().execute("");
                mDialog.cancel();

            }
        });
        btn_reple_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.cancel();
            }
        });

        //dialog크기조절
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(mDialog.getWindow().getAttributes());
        // params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        // params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mDialog.show();
        //Window window = mDialog.getWindow();
        //window.setAttributes(params);
        return ab.create();

    }

    private static class TIME_MAXIMUM {
        public static final int SEC = 60;
        public static final int MIN = 60;
        public static final int HOUR = 24;
        public static final int DAY = 30;
        public static final int MONTH = 12;
    }

    // 아이디어 헤더정보가져오기 - HTTP연결 Thread 생성 클래스
    class NetworkGetIdeainfo extends AsyncTask<String, String, Integer> {
        private String err_msg = "Network error.";

        // JSON에서 받아오는 객체
        private JSONObject jObject;

        // AsyncTask 실행되는거
        @Override
        protected Integer doInBackground(String... params) {

            return processing();
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
                        "http://54.199.176.234/api/get_idea_info.php");

                //서버에 보낼 데이터
                // data를 담음
                name_value.add(new BasicNameValuePair("idea_id", idea_id + ""));
                name_value.add(new BasicNameValuePair("user_id", DataUtil.getAppPreferences(getApplicationContext(), "master_id")));


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
//                jObject = new JSONObject(builder.toString());
                jObject = new JSONObject(builder.toString().substring(builder.toString().indexOf("{"), builder.toString().lastIndexOf("}") + 1));

                // err가 0이면 정상적인 처리
                // err가 0이 아닐시 오류발생
                if (jObject.getInt("err") > 0) {
                    return jObject.getInt("err");
                }
            } catch (Exception e) {
                // 오류발생시
                Log.i(err_msg, e.toString());
                return 100;
            }
            return 0;
        }

        // AsyncTask 실행완료 후에 구동 (Data를 받은것을 Activity에 갱신하는 작업을 하면돼)
        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);

            // 지금 코드에서는 result가 0이면 정상적인 상황
            if (result == 0) {
                Log.i("Network IdeaHeader Data", jObject.toString());


                // jObject에서 데이터를 뽑아내자
                try {
                    String idea_user_id = jObject.get("user_id").toString();
                    String booth_name = jObject.get("name").toString();
                    int booth_id = jObject.getInt("booth_id");
                    String img_url = booth_id + "";
                    content_idea = jObject.get("content").toString();
                    int hit = jObject.getInt("hit");
                    int like_num = jObject.getInt("like_num");
                    int like = jObject.getInt("like");
                    int comment_num = jObject.getInt("comment_num");

                    //서버에서 date받아와서 formatTimeString이용해서 값 변환
                    String reg_Time = jObject.getString("date");
                    String time = formatTimeString(reg_Time);


                    // tv_Writer.setText(email);

                    String getemail = email;

                    byte[] mailarray = getemail.getBytes();
                    String email_view = new String(mailarray, 0, 3);
                    String hide_email = email_view + "*****";

                    tv_Writer.setText(hide_email);
                    tv_logo_name.setText(booth_name);
                    tv_ideaoriginal.setText(content_idea);
                    tv_view.setText(hit + "");
                    tv_like.setText(like_num + "");
                    tv_time.setText(time);
                    tv_commentcount.setText(comment_num + "");

                    loader.displayImage("https://s3-ap-northeast-1.amazonaws.com/comepenny/booth/" + img_url + ".png", iv_boothicon);

                    if (like == 1) {
                        pick_boolean = 1;
                        btn_pick.setBackgroundResource(R.drawable.detail_pickbutton_after);
                    } else {
                        pick_boolean = 0;
                        btn_pick.setBackgroundResource(R.drawable.detail_pickbutton_before);
                    }
                    String user_id = DataUtil.getAppPreferences(getApplicationContext(), "master_id");
                    // 글을 쓴 사람이거나 관리자이면
                    if (user_id.equals(idea_user_id) || user_id.equals("0")) {
                        btn_del.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
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
    }

    //like정보가져오기 - HTTP연결 Thread 생성 클래스
    class NetworkGetlike extends AsyncTask<String, String, Integer> {
        private String err_msg = "Network error.";

        // JSON에서 받아오는 객체
        private JSONObject jObject;

        // AsyncTask 실행되는거
        @Override
        protected Integer doInBackground(String... params) {

            return processing();
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
                        "http://54.199.176.234/api/like.php");

                //서버에 보낼 데이터
                // data를 담음
                name_value.add(new BasicNameValuePair("idea_id", idea_id + ""));
                name_value.add(new BasicNameValuePair("user_id", DataUtil.getAppPreferences(getApplicationContext(), "master_id")));
                if (pick_boolean == 1) {
                    name_value.add(new BasicNameValuePair("is_like", 0 + ""));
                } else {
                    name_value.add(new BasicNameValuePair("is_like", 1 + ""));
                }

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
                jObject = new JSONObject(builder.toString().substring(builder.toString().indexOf("{"), builder.toString().lastIndexOf("}") + 1));

                // err가 0이면 정상적인 처리,err가 0이 아닐시 오류발생
                if (jObject.getInt("err") > 0) {
                    return jObject.getInt("err");
                }
            } catch (Exception e) {
                // 오류발생시
                Log.i(err_msg, e.toString());
                return 100;
            }
            return 0;
        }

        // AsyncTask 실행완료 후에 구동 (Data를 받은것을 Activity에 갱신하는 작업을 하면돼)
        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);

            // 지금 코드에서는 result가 0이면 정상적인 상황
            if (result == 0) {
                Log.i("Network Data", jObject.toString());

                // jObject에서 데이터를 뽑아내자
                int like_num = 0;
                try {
                    like_num = jObject.getInt("like_num");
                    tv_like.setText(like_num + "");
                } catch (JSONException e) {
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
    }

    // 댓글 list HTTP연결 Thread 생성 클래스
    class NetworkGetCommentList extends AsyncTask<String, String, Integer> {
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
                Log.i("Network like Data", jObjects.toString());

                // JSON에서 받은 객체를 가지고 List에 뿌려줘야해
                // jObject에서 데이터를 뽑아내자
                try {
                    // 가져오는 값의 개수를 가져옴
                    count = jObjects.getInt("cnt");
                    offset = offset + count;


                    JSONArray ret_arr = jObjects.getJSONArray("ret");
                    for (int index = 0; index < ret_arr.length(); index++) {
                        JSONObject obj_boothIdeas = ret_arr.getJSONObject(index);
                        comment_id = obj_boothIdeas.getInt("id");

                        content = obj_boothIdeas.getString("comment");
                        writer_email = obj_boothIdeas.getString("email");

                        user_comment_img = obj_boothIdeas.getString("image_t");

                        byte[] mailarray = writer_email.getBytes();
                        String email_view = new String(mailarray, 0, 3);
                        // int email_length = mailarray.length;
                        String hide_email = email_view + "*****";


                        //서버에서 date받아와서 formatTimeString이용해서 값 변환
                        String reg_Time = obj_boothIdeas.getString("date");
                        String comment_time = formatTimeString(reg_Time);


                        // Item 객체로 만들어야함
                        CommentItem items = new CommentItem(user_comment_img, content, hide_email, comment_time, comment_id);

                        // Item 객체를 ArrayList에 넣는다
                        //                      arr_list.add(items);
                        arr_list.add(0, items);
                    }


                    // Adapter에게 데이터를 넣었으니 갱신하라고 알려줌
                    adapters.notifyDataSetChanged();

                    // scroll 할 수 있게함
                    is_scroll = true;

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
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
                        "http://54.199.176.234/api/get_comment_list.php");

//                        //서버에 보낼 데이터
                // data를 담음
                name_value.add(new BasicNameValuePair("offset", offset + ""));
                name_value.add(new BasicNameValuePair("idea_id", idea_id + ""));


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

    //
    private class NetworkaddComment extends AsyncTask<String, String, Integer> {
        String comment;
        // JSON 받아오는 객체
        private JSONObject jObject;

        @Override
        protected Integer doInBackground(String... params) {
            return processing();
        }

        // 서버 연결
        private Integer processing() {
            try {
                HttpClient http_client = new DefaultHttpClient();

                // 요청 후 7초 이내에 응답없으면 timeout 발생
                http_client.getParams().setParameter("http.connection.timeout", 7000);
                // post 방식
                HttpPost http_post = null;

                List<NameValuePair> name_value = new ArrayList<NameValuePair>();

                http_post = new HttpPost("http://54.199.176.234/api/write_comment.php");
                comment = Edit_reple.getText().toString().trim();
                String user_id = DataUtil.getAppPreferences(IdeaDetailActivity.this, "master_id");

                // 데이터 담음
                name_value.add(new BasicNameValuePair("idea_id", idea_id + ""));
                name_value.add(new BasicNameValuePair("comment", comment + ""));
                name_value.add(new BasicNameValuePair("user_id", user_id + ""));

                UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(name_value, "UTF-8");
                http_post.setEntity(entityRequest);


                // 서버 전송
                HttpResponse response = http_client.execute(http_post);

                // 받는 부분
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"), 8);
                StringBuilder builder = new StringBuilder();
                for (String line = null; (line = reader.readLine()) != null; ) {
                    builder.append(line).append("\n");
                }

                // json
                jObject = new JSONObject(builder.toString());


                // 0이면 정상, 0이 아니면 오류 발생
                if (jObject.getInt("err") > 0) {
                    return jObject.getInt("err");
                }

            } catch (Exception e) {
                // 오류발생시
                e.printStackTrace();
                return 100;
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {

            // 정상적으로 글쓰기
            if (result == 0) {
                try {

                    Edit_reple.setText("");
                    jObject.getInt("err");

                    initializationContent();

                    //new NetworkGetCommentList().execute("");


                    //키보드숨기기
                    keyboard.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    return;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            Toast.makeText(getApplicationContext(), "server error", Toast.LENGTH_SHORT).show();
            return;
        }


    }
//    ///아이디어 수정

    //아이디어 삭제
    private class NetworkIdeaDel extends AsyncTask<String, String, Integer> {
        // JSON 받아오는 객체
        private JSONObject jObject;

        @Override
        protected Integer doInBackground(String... params) {
            return processing();
        }

        // 서버 연결
        private Integer processing() {
            try {
                HttpClient http_client = new DefaultHttpClient();

                // 요청 후 7초 이내에 응답없으면 timeout 발생
                http_client.getParams().setParameter("http.connection.timeout", 7000);
                // post 방식
                HttpPost http_post = null;

                List<NameValuePair> name_value = new ArrayList<NameValuePair>();

                http_post = new HttpPost("http://54.199.176.234/api/delete_idea.php");

                // 데이터 담음
                name_value.add(new BasicNameValuePair("idea_id", idea_id + ""));

                UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(name_value, "UTF-8");
                http_post.setEntity(entityRequest);


                // 서버 전송
                HttpResponse response = http_client.execute(http_post);

                // 받는 부분
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"), 8);
                StringBuilder builder = new StringBuilder();
                for (String line = null; (line = reader.readLine()) != null; ) {
                    builder.append(line).append("\n");
                }

                // json
                jObject = new JSONObject(builder.toString());


                // 0이면 정상, 0이 아니면 오류 발생
                if (jObject.getInt("err") > 0) {
                    return jObject.getInt("err");
                }

            } catch (Exception e) {
                // 오류발생시
                e.printStackTrace();
                return 100;
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {

            // 정상적으로 글쓰기
            if (result == 0) {
                try {
                    jObject.getInt("err");

                    IdeaDetailActivity.this.finish();
                    return;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            Toast.makeText(getApplicationContext(), "server error", Toast.LENGTH_SHORT).show();
            return;
        }


    }

    private class NetworkIdeaAdjustWrite extends AsyncTask<String, String, Integer> {
        private String err_msg = "Network  AdjustWrite error.";

        // JSON에서 받아오는 객체
        private JSONObject jObject;

        // AsyncTask 실행되는거
        @Override
        protected Integer doInBackground(String... params) {

            return processing();
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
                        "http://54.199.176.234/api/get_idea_info.php");

                //서버에 보낼 데이터
                // data를 담음
                name_value.add(new BasicNameValuePair("idea_id", idea_id + ""));
                name_value.add(new BasicNameValuePair("user_id", DataUtil.getAppPreferences(getApplicationContext(), "master_id")));


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
//                jObject = new JSONObject(builder.toString());
                jObject = new JSONObject(builder.toString().substring(builder.toString().indexOf("{"), builder.toString().lastIndexOf("}") + 1));

                // err가 0이면 정상적인 처리
                // err가 0이 아닐시 오류발생
                if (jObject.getInt("err") > 0) {
                    return jObject.getInt("err");
                }
            } catch (Exception e) {
                // 오류발생시
                Log.i(err_msg, e.toString());
                return 100;
            }
            return 0;
        }

        // AsyncTask 실행완료 후에 구동 (Data를 받은것을 Activity에 갱신하는 작업을 하면돼)
        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);

            // 지금 코드에서는 result가 0이면 정상적인 상황
            if (result == 0) {
                Log.i("Network 수정 할 Data", jObject.toString());


                // jObject에서 데이터를 뽑아내자
                try {

                    String content = jObject.get("content").toString();
                    // 여기서인텐트하기
                    Intent itIdeaDetail = new Intent(getApplicationContext(), AdjustWriteActivity.class);
                    itIdeaDetail.putExtra("idea_id", idea_id);
                    itIdeaDetail.putExtra("content", content);
                    startActivity(itIdeaDetail);
                    overridePendingTransition(0, 0);
                    // finish();


                } catch (JSONException e) {
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

    }

    //댓글 삭제
    private class NetworkCommentDel extends AsyncTask<String, String, Integer> {
        // JSON 받아오는 객체
        private JSONObject jObject;

        @Override
        protected Integer doInBackground(String... params) {
            return processing();
        }

        // 서버 연결
        private Integer processing() {
            try {
                HttpClient http_client = new DefaultHttpClient();

                // 요청 후 7초 이내에 응답없으면 timeout 발생
                http_client.getParams().setParameter("http.connection.timeout", 7000);
                // post 방식
                HttpPost http_post = null;

                List<NameValuePair> name_value = new ArrayList<NameValuePair>();

                http_post = new HttpPost("http://54.199.176.234/api/delete_comment.php");

                int id = arr_list.get(commentDelPosition).getComment_id();

                // 데이터 담음
                name_value.add(new BasicNameValuePair("comment_id", id + ""));

                UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(name_value, "UTF-8");
                http_post.setEntity(entityRequest);


                // 서버 전송
                HttpResponse response = http_client.execute(http_post);

                // 받는 부분
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"), 8);
                StringBuilder builder = new StringBuilder();
                for (String line = null; (line = reader.readLine()) != null; ) {
                    builder.append(line).append("\n");
                }

                // json
                jObject = new JSONObject(builder.toString());


                // 0이면 정상, 0이 아니면 오류 발생
                if (jObject.getInt("err") > 0) {
                    return jObject.getInt("err");
                }

            } catch (Exception e) {
                // 오류발생시
                e.printStackTrace();
                return 100;
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {

            // 정상적으로 글쓰기
            if (result == 0) {
                try {
                    jObject.getInt("err");

                    new NetworkGetIdeainfo().execute("");
                    //   arr_list.remove(commentDelPosition);

                    for (int i = commentDelPosition; i < arr_list.size() - 1; i++) {
                        arr_list.set(i, arr_list.get(i + 1));

                    }
                    arr_list.remove(arr_list.size() - 1);
                    adapters.notifyDataSetChanged();


                    return;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            Toast.makeText(getApplicationContext(), "server error", Toast.LENGTH_SHORT).show();
            return;
        }


    }

    ///댓글수정
    private class NetworkCommentAdjustWrite extends AsyncTask<String, String, Integer> {
        private String err_msg = "Network  AdjustComment error.";

        // JSON에서 받아오는 객체
        private JSONObject jObject;

        // AsyncTask 실행되는거
        @Override
        protected Integer doInBackground(String... params) {

            return processing();
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
                        "http://54.199.176.234/api/modify_comment.php");
                int id = arr_list.get(commentDelPosition).getComment_id();
                //   int id = comment_id;
                Log.i("comment_id", id + "");
                content_reple = Edit_reple_adjust.getText().toString().trim();
                Log.i("content_reple", content_reple);


                //서버에 보낼 데이터
                // data를 담음
                name_value.add(new BasicNameValuePair("comment_id", id + ""));
                name_value.add(new BasicNameValuePair("comment", content_reple));

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
//                jObject = new JSONObject(builder.toString());
                jObject = new JSONObject(builder.toString().substring(builder.toString().indexOf("{"), builder.toString().lastIndexOf("}") + 1));

                // err가 0이면 정상적인 처리
                // err가 0이 아닐시 오류발생
                if (jObject.getInt("err") > 0) {
                    return jObject.getInt("err");
                }
            } catch (Exception e) {
                // 오류발생시
                Log.i(err_msg, e.toString());
                return 100;
            }
            return 0;
        }

        // AsyncTask 실행완료 후에 구동 (Data를 받은것을 Activity에 갱신하는 작업을 하면돼)
        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);

            // 지금 코드에서는 result가 0이면 정상적인 상황
            if (result == 0) {
                Log.i("Network 수정 할 reple Data", jObject.toString());


                // jObject에서 데이터를 뽑아내자
                try {

                    jObject.getInt("err");
                    // new NetworkGetCommentList().execute("");
                    arr_list.get(commentDelPosition).setComment_content(content_reple);
                    adapters.notifyDataSetChanged();
//                    overridePendingTransition(0, 0);

                    return;


                } catch (JSONException e) {
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

    }

}
