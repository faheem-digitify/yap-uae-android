package co.yap.modules.location.kyc_additional_info.employment_info.status

import android.app.Application
import android.view.View
import android.widget.CheckedTextView
import co.yap.modules.location.viewmodels.LocationChildViewModel
import co.yap.yapcore.SingleClickEvent
import co.yap.yapcore.enums.EmploymentStatus
import co.yap.yapcore.interfaces.OnItemClickListener

class EmploymentStatusSelectionViewModel(application: Application) :
    LocationChildViewModel<IEmploymentStatusSelection.State>(application),
    IEmploymentStatusSelection.ViewModel {
    override val state: EmploymentStatusSelectionState =
        EmploymentStatusSelectionState()
    override var clickEvent: SingleClickEvent = SingleClickEvent()
    override var employmentStatusAdapter: EmploymentStatusAdapter =
        EmploymentStatusAdapter(getEmploymentStatusList())
    override var lastItemCheckedPosition = -1;

    override fun onCreate() {
        super.onCreate()
        employmentStatusAdapter.onItemClickListener = onItemClickListener
    }

    override fun onResume() {
        super.onResume()
        if (parentViewModel?.isOnBoarding == true) {
            progressToolBarVisibility(true)
            setProgress(90)
        }
    }

    override fun handleOnPressNext(id: Int) {
        clickEvent.setValue(id)
    }

    private fun getEmploymentStatusList(): MutableList<EmploymentStatusSelectionModel> {
        val employmentStatuses = mutableListOf<EmploymentStatusSelectionModel>()
        employmentStatuses.add(
            EmploymentStatusSelectionModel(
                EmploymentStatus.EMPLOYED,
                EmploymentStatus.EMPLOYED.status,
                false
            )
        )
        employmentStatuses.add(
            EmploymentStatusSelectionModel(
                EmploymentStatus.SELF_EMPLOYED,
                EmploymentStatus.SELF_EMPLOYED.status,
                false
            )
        )
        employmentStatuses.add(
            EmploymentStatusSelectionModel(
                EmploymentStatus.SALARIED_AND_SELF_EMPLOYED,
                EmploymentStatus.SALARIED_AND_SELF_EMPLOYED.status,
                false
            )
        )
        employmentStatuses.add(
            EmploymentStatusSelectionModel(
                EmploymentStatus.OTHER,
                EmploymentStatus.OTHER.status,
                false
            )
        )
        return employmentStatuses
    }

    val onItemClickListener = object : OnItemClickListener {
        override fun onItemClick(view: View, data: Any, pos: Int) {
            if ((data is EmploymentStatusSelectionModel) && (view is CheckedTextView)) {
                if (!data.isSelected && lastItemCheckedPosition == -1) {
                    data.isSelected = true
                    lastItemCheckedPosition = pos
                    employmentStatusAdapter.notifyItemChanged(lastItemCheckedPosition)
                    state.enableNextButton.set(true)
                } else if (!data.isSelected && lastItemCheckedPosition != pos) {
                    data.isSelected = true
                    employmentStatusAdapter.setItemAt(pos, data)
                    val previousSelected =
                        employmentStatusAdapter.getDataForPosition(lastItemCheckedPosition)
                    previousSelected.isSelected = false
                    employmentStatusAdapter.setItemAt(lastItemCheckedPosition, previousSelected)
                    lastItemCheckedPosition = pos
                } else {
                    data.isSelected = false
                    employmentStatusAdapter.notifyItemChanged(pos)
                    lastItemCheckedPosition = -1
                    state.enableNextButton.set(false)
                }
            }
        }
    }
}
