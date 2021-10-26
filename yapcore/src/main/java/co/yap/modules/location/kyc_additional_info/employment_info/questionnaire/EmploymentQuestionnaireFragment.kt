package co.yap.modules.location.kyc_additional_info.employment_info.questionnaire

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.yap.countryutils.country.unSelectAllCountries
import co.yap.modules.location.fragments.LocationChildFragment
import co.yap.modules.location.kyc_additional_info.employment_info.questionnaire.adapter.QuestionItemViewHolders
import co.yap.modules.location.kyc_additional_info.employment_info.questionnaire.models.QuestionUiFields
import co.yap.translation.Strings
import co.yap.widgets.bottomsheet.BottomSheetConfiguration
import co.yap.widgets.skeletonlayout.views
import co.yap.yapcore.BR
import co.yap.yapcore.R
import co.yap.yapcore.constants.Constants
import co.yap.yapcore.databinding.FragmentEmploymentQuestionnaireBinding
import co.yap.yapcore.enums.EmploymentQuestionIdentifier
import co.yap.yapcore.enums.EmploymentStatus
import co.yap.yapcore.helpers.extentions.launchBottomSheetSegment
import co.yap.yapcore.helpers.extentions.launchMultiSelectionBottomSheet
import co.yap.yapcore.helpers.infoDialog
import co.yap.yapcore.interfaces.OnItemClickListener
import com.liveperson.infra.utils.UIUtils.hideKeyboard

class EmploymentQuestionnaireFragment : LocationChildFragment<IEmploymentQuestionnaire.ViewModel>(),
    IEmploymentQuestionnaire.View {
    override fun getBindingVariable(): Int = BR.viewModel
    override fun getLayoutId(): Int = R.layout.fragment_employment_questionnaire
    override val viewModel: EmploymentQuestionnaireViewModel
        get() = ViewModelProviders.of(this).get(EmploymentQuestionnaireViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addObservers()
        viewModel.employmentStatus = arguments?.get("EMPLOYMENT_STATUS") as EmploymentStatus
        viewModel.isDataRequiredFromApi(forStatus = viewModel.employmentStatus)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initQuestionViews()
    }

    private fun initQuestionViews() {
        viewModel.questionsList.addAll(viewModel.questionnaires(viewModel.employmentStatus))
        val questionItemViewHolders = QuestionItemViewHolders(viewModel)
        viewModel.questionsList.forEachIndexed { position, questionUiField ->
            val questionView: View?
            val binding =
                DataBindingUtil.inflate<ViewDataBinding>(
                    layoutInflater,
                    questionItemViewHolders.getItemViewType(position),
                    null,
                    false
                )
            questionView = questionItemViewHolders.getViewFromBinding(
                binding,
                questionUiField,
                position,
                listener
            )
            if (questionView != null)
                getBinding().llQuestions.addView(questionView)
        }
    }

    private val clickObserver = Observer<Int> {
        when (it) {
            R.id.btnSubmit -> {
                viewModel.saveEmploymentInfo(viewModel.getEmploymentInfoRequest(viewModel.employmentStatus)) {
                    navigate(R.id.action_employmentQuestionnaireFragment_to_cardOnTheWayFragment)
                }
            }
        }
    }

    val listener = object : OnItemClickListener {
        override fun onItemClick(view: View, data: Any, pos: Int) {
            viewModel.rvQuestionItemListener.onItemClick(view, data, pos)
            when (view.id) {
                R.id.ivSupport -> {
                    viewModel.onInfoClick(data as QuestionUiFields) { title, message ->
                        showInfoDialog(title, message)
                    }
                }

                R.id.searchCountries -> {
                    hideKeyboard(requireActivity())
                    requireActivity().launchMultiSelectionBottomSheet(
                        itemClickListener = object : OnItemClickListener {
                            override fun onItemClick(view: View, data: Any, pos: Int) {
                                onBusinessCountriesSelection(data as ArrayList<String>)
                                viewModel.validate()
                            }
                        },configuration = BottomSheetConfiguration(
                            heading = "Add all the countries your company does business with:",
                            showSearch = true,
                            showHeaderSeparator = true
                        ),
                        countriesList = viewModel.getSelectedStateCountries(
                            viewModel.parentViewModel?.countries ?: arrayListOf()
                        )
                    )
                }

                R.id.tvDropDown -> {
                    hideKeyboard(requireActivity())
                    when ((data as QuestionUiFields).key) {
                        EmploymentQuestionIdentifier.EMPLOYMENT_TYPE -> openEmploymentTypeBottomSheet()
                        EmploymentQuestionIdentifier.INDUSTRY_SEGMENT -> openSegmentsBottomSheet()
                        else -> {
                        }
                    }
                }
            }
        }
    }

    private fun onBusinessCountriesSelection(list: ArrayList<String>) {
        val viewCountriesLayout =
            getBinding().llQuestions.views()[viewModel.selectedQuestionItemPosition]
        viewModel.setBusinessCountries(
            viewCountriesLayout,
            list,
            viewModel.selectedQuestionItemPosition
        )
    }

    private fun openEmploymentTypeBottomSheet() {
        launchBottomSheetSegment(
            viewModel.employmentTypeItemClickListener,
            configuration = BottomSheetConfiguration(heading = getString(Strings.screen_employment_questionnaire_display_text__bottom_sheet_title_describe_you)),
            viewType = Constants.VIEW_WITHOUT_FLAG,
            listData = viewModel.parseEmploymentTypes(viewModel.employmentTypes())
        )
    }

    private fun openSegmentsBottomSheet() {
        launchBottomSheetSegment(
            viewModel.employmentTypeItemClickListener,
            configuration = BottomSheetConfiguration(heading = getString(Strings.screen_employment_questionnaire_display_text__bottom_sheet_title_segments)),
            viewType = Constants.VIEW_FIXED_HEIGHT,
            listData = viewModel.parseSegments(viewModel.industrySegmentsList)
        )
    }

    override fun addObservers() {
        viewModel.clickEvent.observe(this, clickObserver)
    }

    override fun removeObservers() {
        viewModel.clickEvent.removeObserver(clickObserver)
    }

    override fun onDestroy() {
        super.onDestroy()
        removeObservers()
    }

    override fun getBinding(): FragmentEmploymentQuestionnaireBinding =
        viewDataBinding as FragmentEmploymentQuestionnaireBinding


    override fun showInfoDialog(title: String, message: String) {
        requireContext().infoDialog(
            title = title,
            message = message,
            buttonText = getString(Strings.screen_employment_information_dialog_button_text_close)
        )
    }

    override fun onResume() {
        super.onResume()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun onStop() {
        super.onStop()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        viewModel.parentViewModel?.countries?.unSelectAllCountries(
            viewModel.selectedBusinessCountries.get() ?: arrayListOf()
        )
    }
}