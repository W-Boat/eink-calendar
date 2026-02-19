package com.eink.calendar.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.eink.calendar.R
import com.eink.calendar.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * 主 Activity - 墨水屏日历应用的入口
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 设置 ToolBar
        setSupportActionBar(binding.toolbar)

        // 设置导航
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_month,
                R.id.navigation_day,
                R.id.navigation_agenda
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        // 墨水屏优化设置
        setupEinkOptimizations()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    /**
     * 为墨水屏设备优化应用
     */
    private fun setupEinkOptimizations() {
        // 禁用过渡动画
        overridePendingTransition(0, 0)

        // 禁用系统动画
        window.setWindowAnimationDuration(0)

        // 设置完全刷新模式初始化
        viewModel.initializeEinkDisplay()
    }
}
