package administrator.game15;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


public class MainActivity extends ActionBarActivity implements View.OnClickListener
{

    // ゲームビューのインスタンス
    private GameView gameView;

    // 時間の表示
    public boolean playNow = false;
    private Chronometer timer;
    private long timeWhenStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Resources rsc = getResources();

        // レイアウトの作成
        RelativeLayout layoutAll = new RelativeLayout(this);
        layoutAll.setBackgroundColor(Color.WHITE);

        // ヘッダー部のレイアウト作成
        LinearLayout layoutHeader = new LinearLayout(this);
        layoutHeader.setBackgroundColor(Color.WHITE);

        int wc = LinearLayout.LayoutParams.WRAP_CONTENT;

        int id = 0;

        // タイム
        timer = new Chronometer(this);
        timer.setId(++id);
        timer.setTextColor(rsc.getColor(R.color.str_color));
        timer.setTextSize(30);    // なんとかしなきゃ…。
        timer.setTag("time1");
        RelativeLayout.LayoutParams param1 = new RelativeLayout.LayoutParams(wc,wc);
        param1.addRule(RelativeLayout.CENTER_HORIZONTAL);
        param1.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layoutAll.addView(timer, param1);

        // ゲームビューを設定
        gameView = new GameView(this);
        gameView.setId(++id);
        RelativeLayout.LayoutParams param2 = new RelativeLayout.LayoutParams(wc,wc);
        param2.addRule(RelativeLayout.BELOW, id - 1);
        layoutAll.addView(gameView, param2);

        // 開始ボタン
        ImageButton btn = new ImageButton(this);
        btn.setId(++id);
        btn.setTag("btn1");
        btn.setScaleType(ImageView.ScaleType.FIT_XY);
        btn.setImageResource(R.drawable.ic_play);
        btn.setBackgroundColor(Color.TRANSPARENT);
        btn.setOnClickListener(this);
        RelativeLayout.LayoutParams param3 = new RelativeLayout.LayoutParams(320,300);
        param3.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        param3.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutAll.addView(btn, param3);

        setContentView(layoutAll);
    }

    @Override
    protected void onResume(){
        super.onResume();

        // タイマーを再開
        if( playNow ) {
            timer.setBase(SystemClock.elapsedRealtime() + timeWhenStop);
            timer.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // タイマーを止める
        if( playNow ) {
            timeWhenStop = timer.getBase() - SystemClock.elapsedRealtime();
            timer.stop();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onClick(View view){
        // 実行ボタン押下
        String tag = (String)view.getTag();
        if (tag == "btn1") {

            // コマの並びをシャッフルし、再描画
            gameView.shufflePieces();
            gameView.invalidate();

            // タイマースタート
            timer.setBase(SystemClock.elapsedRealtime());
            timer.start();

            playNow = true;
        }
    }

    // ゲームクリアメッセージの出力
    public void clearedGame()
    {
        timer.stop();
        playNow = false;

        Resources rsc = getResources();

        // ダイアログ表示
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
//        ad.setTitle(rsc.getString(R.string.clear_mess_1));
        ad.setMessage(String.format(rsc.getString(R.string.clear_mess_1), timer.getText()));
        ad.setPositiveButton("Close", null);
        ad.show();

//        // スコア表示
//        Intent it = new Intent(this, ScoreActivity.class);
//        it.putExtra("timeStr", "03:01:2");
//        startActivity(it);
    }
}
