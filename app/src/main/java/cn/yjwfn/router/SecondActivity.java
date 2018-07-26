package cn.yjwfn.router;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import im.xingzhe.lib.router.annotation.DestinationArgument;
import im.xingzhe.lib.router.annotation.DestinationUri;
import im.xingzhe.lib.router.annotation.UriDestination;

@UriDestination(name = "Second", uri = @DestinationUri(path = "second"), in = {
        @DestinationArgument(key = "name", type = String.class)
})
public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }
}
