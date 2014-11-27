package administrator.game15;

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
import android.widget.Chronometer;
import android.widget.LinearLayout;


public class MainActivity extends ActionBarActivity {

    // ゲームビューのインスタンス
    private GameView gameView;

    // 時間の表示
    public boolean playNow = false;
    private Chronometer timer;
    private long timeWhenStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ツールバーを非表示
//        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // リソース
        Resources rsc = getResources();

        // レイアウトの作成
        LinearLayout layoutAll = new LinearLayout(this);
        layoutAll.setBackgroundColor(Color.WHITE);
        layoutAll.setOrientation(LinearLayout.VERTICAL);

        setContentView(layoutAll);

        // ヘッダー部のレイアウト作成
        LinearLayout layoutHeader = new LinearLayout(this);
        layoutHeader.setBackgroundColor(Color.WHITE);
        layoutHeader.setOrientation(LinearLayout.HORIZONTAL);

        int wc = LinearLayout.LayoutParams.WRAP_CONTENT;

        // タイム
        timer = new Chronometer(this);
        timer.setTextColor(rsc.getColor(R.color.str_color));
        timer.setTextSize(30);    // なんとかしなきゃ…。
        timer.setTag("time1");
        LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(wc,wc);
        param1.gravity = Gravity.CENTER;
        layoutAll.addView(timer, param1);

        // ゲームビューを設定
        gameView = new GameView(this);
        layoutAll.addView(gameView, new LinearLayout.LayoutParams(wc, wc));
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // 再実行ボタン押下
        if (id == R.id.action_refresh) {

            // コマの並びをシャッフルし、再描画
            gameView.shufflePieces();
            gameView.invalidate();

            // タイマースタート
            timer.setBase(SystemClock.elapsedRealtime());
            timer.start();

            playNow = true;

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // ゲームクリアメッセージの出力
    public void clearedGame()
    {
        timer.stop();
        playNow = false;

        // スコア表示
        Intent it = new Intent(this, ScoreActivity.class);
        startActivity(it);
    }
}
