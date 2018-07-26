package cn.yjwfn.router.dagger;

import cn.yjwfn.router.App;
import dagger.Component;

@Component(modules = {AppModule.class})
public interface ApplicationComponent
{

    void inject(App app);

}
