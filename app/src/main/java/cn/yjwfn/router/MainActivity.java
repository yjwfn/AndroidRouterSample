package cn.yjwfn.router;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import javax.inject.Inject;
import javax.inject.Provider;

import dagger.android.AndroidInjection;
import dagger.android.DaggerActivity;
import im.xingzhe.lib.router.action.DestinationAction;
import im.xingzhe.lib.router.action.UriDestinationActionBuilder;
import im.xingzhe.lib.router.destination.DestinationService;

public class MainActivity extends AppCompatActivity {


    @Inject
    Provider<UriDestinationActionBuilder> builderProvider;

    @Inject
    DestinationService destinationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void gotoSecondActivity(View view){
        DestinationAction action = builderProvider
                .get()
                .path("second")
                .context(this)
                .build();

        destinationService.start(action);
    }
}
