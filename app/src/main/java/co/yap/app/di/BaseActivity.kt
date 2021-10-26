package co.yap.app.di

import android.os.Bundle
import co.yap.app.di.component.DaggerActivityComponent
import co.yap.app.di.module.ActivityModule
import co.yap.yapcore.BaseActivity
import co.yap.yapcore.IBase

abstract class BaseActivity<V: IBase.ViewModel<*>> : BaseActivity<V>() {
    lateinit var daggerComponent: DaggerActivityComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        performInjection()
    }

    private fun performInjection() {
        daggerComponent = DaggerActivityComponent.builder()
            .activityModule(ActivityModule(this))
            .build() as DaggerActivityComponent
    }
}