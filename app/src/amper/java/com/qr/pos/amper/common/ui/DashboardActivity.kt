package com.qr.pos.amper.common.ui

import android.content.Context
import android.content.Intent
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.GridLayoutManager
import com.qr.pos.amper.R
import com.qr.pos.amper.accounting.ui.AccountingActivity
import com.qr.pos.amper.auth.ui.login.LoginActivity
import com.qr.pos.amper.auth.utils.UserType
import com.qr.pos.amper.cashier.ui.PosActivity
import com.qr.pos.amper.common.domain.repository.SharedPrefRepo
import com.qr.pos.amper.common.ui.adapter.MenuAdapter
import com.qr.pos.amper.create_user.ui.UserCreateActivity
import com.qr.pos.amper.utils.base.activity.BaseActivity
import com.qr.pos.amper.utils.base.binding.viewBinding
import com.qr.pos.amper.databinding.ActivityDashboardBinding
import com.qr.pos.amper.inventory.ui.InventoryActivity
import com.qr.pos.amper.utils.deligate.adapter.AdapterOnItemClickListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DashboardActivity : BaseActivity<ActivityDashboardBinding>(R.layout.activity_dashboard), AdapterOnItemClickListener<DashboardActions> {

    override val binding by viewBinding(ActivityDashboardBinding::inflate)

    @Inject
    lateinit var sf: SharedPrefRepo

    override fun setupView() {
        with(binding){
            optionsRecycler.adapter = MenuAdapter(this@DashboardActivity, getOptions())
            optionsRecycler.layoutManager = GridLayoutManager(this@DashboardActivity, 2, GridLayoutManager.VERTICAL, false)
        }
    }

    private fun getOptions(): List<DashboardActions> {
        return when(sf.getUserType()){
            UserType.ADMIN -> DashboardActions.values().toList()
            else -> DashboardActions.values().filter { it != DashboardActions.USER_CREATION }
        }
    }

    override fun setupListeners() {
        onBackPressedDispatcher.addCallback(object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                showSignOutDialog()
            }
        })
    }

    companion object {
        fun getStartIntent(context: Context) = Intent(context, DashboardActivity::class.java)
    }

    override fun onItemClick(data: DashboardActions, position: Int) {
        when(data){
            DashboardActions.POS -> navigateToPos()
            DashboardActions.INVENTORY -> navigateToInventory()
            DashboardActions.SALES -> {}
            DashboardActions.USER_CREATION -> navigateToUserCreation()
            DashboardActions.ACCOUNTING -> navigateToAccounting()
        }
    }

    private fun navigateToUserCreation() {
        navigate(UserCreateActivity.getStartIntent(this))
    }

    private fun navigateToAccounting() {
        navigate(AccountingActivity.getStartIntent(this))
    }

    private fun navigateToInventory() {
        navigate(InventoryActivity.getStartIntent(this))
    }

    private fun navigate(startIntent: Intent) {
        startActivity(startIntent)
    }

    private fun navigateToPos() {
        navigate(PosActivity.getStartIntent(this))
    }

    private fun showSignOutDialog() {
        alertDialog.setTitle(getString(R.string.signout_dialog_title))
            .setMessage(getString(R.string.signout_dialog_message))
            .setPositiveButton(getString(R.string.yes_dialog_label)){ d, _ ->
                d.dismiss()
                logoutUser()
            }.setNegativeButton(getString(R.string.cancel_label)){ d,_ ->
                d.dismiss()
            }.create().show()
    }

    private fun navigateToLogin() {
        startActivity(LoginActivity.getStartIntent(this))
        finish()
    }


}