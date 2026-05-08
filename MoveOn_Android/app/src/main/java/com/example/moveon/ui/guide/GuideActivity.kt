package com.example.moveon.ui.guide

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.moveon.R
import com.example.moveon.data.local.OnboardingPreference
import com.example.moveon.databinding.ActivityGuideBinding
import com.example.moveon.ui.guide.adapter.GuidePagerAdapter
import com.example.moveon.ui.login.LoginActivity

class GuideActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGuideBinding
    private lateinit var viewModel: GuideViewModel
    private var isPermissionRequestInProgress = false

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        isPermissionRequestInProgress = false

        if (hasRequiredPermissions()) {
            moveToPage(GuideViewModel.LAST_PAGE_INDEX)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGuideBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[GuideViewModel::class.java]
        binding.guideViewPager.adapter = GuidePagerAdapter(this)

        binding.guideViewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    if (position == GuideViewModel.LAST_PAGE_INDEX && !hasRequiredPermissions()) {
                        moveToPage(GuideViewModel.PERMISSION_PAGE_INDEX, smoothScroll = false)
                        requestRequiredPermissions(forceRequest = false)
                        return
                    }

                    viewModel.setCurrentPage(position)

                    if (position == GuideViewModel.PERMISSION_PAGE_INDEX && !hasRequiredPermissions()) {
                        requestRequiredPermissions(forceRequest = false)
                    }
                }
            }
        )

        binding.nextButton.setOnClickListener {
            val currentPage = binding.guideViewPager.currentItem
            val nextPage = viewModel.nextPage(currentPage)

            when {
                nextPage == null -> finishGuide()
                currentPage == GuideViewModel.PERMISSION_PAGE_INDEX && !hasRequiredPermissions() -> {
                    requestRequiredPermissions(forceRequest = true)
                }
                else -> moveToPage(nextPage)
            }
        }

        viewModel.isLastPage.observe(this) { isLastPage ->
            val textResId = if (isLastPage) {
                R.string.button_text_guide_finish
            } else {
                R.string.button_text_guide_next
            }
            binding.nextButton.setText(textResId)
        }
    }

    fun moveToPage(position: Int, smoothScroll: Boolean = true) {
        binding.guideViewPager.setCurrentItem(position, smoothScroll)
    }

    private fun finishGuide() {
        OnboardingPreference.setGuideCompleted(this)
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun requestRequiredPermissions(forceRequest: Boolean) {
        if (binding.guideViewPager.currentItem != GuideViewModel.PERMISSION_PAGE_INDEX) {
            moveToPage(GuideViewModel.PERMISSION_PAGE_INDEX, smoothScroll = false)
        }

        val deniedPermissions = deniedRequiredPermissions()

        if (deniedPermissions.isEmpty()) {
            moveToPage(GuideViewModel.LAST_PAGE_INDEX)
        } else if (forceRequest && hasPermanentlyDeniedPermission(deniedPermissions)) {
            showPermissionSettingsDialog()
        } else if (forceRequest || !isPermissionRequestInProgress) {
            isPermissionRequestInProgress = true
            permissionLauncher.launch(deniedPermissions.toTypedArray())
        }
    }

    private fun hasPermanentlyDeniedPermission(permissions: List<String>): Boolean {
        return permissions.any { permission ->
            ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED &&
                !shouldShowRequestPermissionRationale(permission)
        }
    }

    private fun showPermissionSettingsDialog() {
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다")
            .setMessage("가이드를 완료하려면 위치 및 파일 접근 권한을 허용해주세요.")
            .setPositiveButton("설정으로 이동") { _, _ ->
                openAppSettings()
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun openAppSettings() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        )
        startActivity(intent)
    }

    private fun hasRequiredPermissions(): Boolean {
        return hasLocationPermission() && storagePermissions().all { permission ->
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun deniedRequiredPermissions(): List<String> {
        return buildList {
            if (!hasLocationPermission()) {
                add(Manifest.permission.ACCESS_FINE_LOCATION)
                add(Manifest.permission.ACCESS_COARSE_LOCATION)
            }

            storagePermissions().forEach { permission ->
                if (ContextCompat.checkSelfPermission(
                        this@GuideActivity,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    add(permission)
                }
            }
        }
    }

    private fun hasLocationPermission(): Boolean {
        val hasFineLocation = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasCoarseLocation = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return hasFineLocation || hasCoarseLocation
    }

    private fun storagePermissions(): List<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
            )
        } else {
            listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }
}
