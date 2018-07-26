package cn.yjwfn.router.dagger;

import cn.yjwfn.router.MainActivity;
import cn.yjwfn.router.SecondActivity;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;
import im.xingzhe.lib.router.action.UriDestinationActionBuilder;
import im.xingzhe.lib.router.action.impl.DestinationActionBuilderImpl;
import im.xingzhe.lib.router.destination.DestinationService;
import im.xingzhe.lib.router.destination.impl.DestinationServiceImpl;

@Module
public  abstract class AppModule
{

    @ContributesAndroidInjector
    public abstract MainActivity provdeMainActivity();

    @ContributesAndroidInjector
    public abstract SecondActivity provdeSecondActivity();



    @Provides
    public static DestinationService provideDestinationService(){
        return new DestinationServiceImpl();
    }

    @Provides
    public static UriDestinationActionBuilder provideUriDestinationActionBuilder( ){
        return new DestinationActionBuilderImpl();
    };

}
