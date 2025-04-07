package com.example.myappplaylistmaker.presentation.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.example.myappplaylistmaker.R
import com.example.myappplaylistmaker.databinding.FragmentSettingsBinding
import com.example.myappplaylistmaker.presentation.view_models.settings.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val settingsViewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindingButtons()
        setupThemeSwitch()

    }

    private fun setupThemeSwitch() {
        val switchTheme: SwitchCompat = binding.switchTheme

        settingsViewModel.themeState.observe(viewLifecycleOwner) { setNightMode ->
            switchTheme.isChecked = setNightMode
        }
        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.setNightModeState(isChecked)
        }
    }

    private fun bindingButtons() {

        binding.share.setOnClickListener {
            val shareMessage = getString(R.string.app_link)
            val shareIntent = settingsViewModel.createShareIntent(shareMessage)
            val chooser = Intent.createChooser(shareIntent, "")
            startActivity(chooser)
        }

        binding.support.setOnClickListener {
            settingsViewModel.writeSupport()
        }

        binding.acception.setOnClickListener {
            settingsViewModel.acceptTermsOfUse()
        }
    }
}