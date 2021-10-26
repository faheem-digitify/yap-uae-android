package co.yap.app.di.component;

import co.yap.app.main.MainActivity;
import co.yap.app.di.CustomScope;
import co.yap.app.di.module.ActivityModule;
import dagger.Component;


@CustomScope
@Component(modules = {ActivityModule.class})
public interface ActivityComponent {

    void inject(MainActivity activity);

}