package ind.eniac.life;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import ind.eniac.annotation.onCreate;
import ind.eniac.annotation.onDestroy;
import ind.eniac.binder.Life;

public class MainActivity extends AppCompatActivity {


    TextView tv_hello;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Life.bind(this);
        setContentView(R.layout.activity_main);
    }

    @onCreate()
    public void LifeTest() {
        Log.e("orz", "sro1");
    }

    @onCreate(pos = 1)
    public void LifeTest2() {
        Log.e("orz", "sro2");
    }

    @onDestroy(pos = 1)
    public void LifeTest3() {
        Log.e("orz", "sro");
    }
}
