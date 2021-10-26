package co.yap.yapcore.helpers

import co.yap.networking.customers.CustomersRepository
import co.yap.networking.customers.requestdtos.TourGuideRequest
import co.yap.networking.customers.responsedtos.TourGuide
import co.yap.networking.models.RetroApiResponse
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object TourGuideManager {
    private val customerRepository: CustomersRepository = CustomersRepository
    private var listOfTourViews: ArrayList<TourGuide> = arrayListOf()
    val getBlockedTourGuideScreens: ArrayList<TourGuideType>
        get() = getBlockedTourGuideList()

    fun configure(tourViews: List<TourGuide>) {
        this.listOfTourViews = tourViews as ArrayList<TourGuide>
    }

    private fun getBlockedTourGuideList(): ArrayList<TourGuideType> {
        val blockedTourGuideList: ArrayList<TourGuideType> = arrayListOf()
        this.listOfTourViews.filter { it.completed == true || it.skipped == true }.forEach {
            when (it.viewName) {
                TourGuideType.DASHBOARD_SCREEN.name -> blockedTourGuideList.add(TourGuideType.DASHBOARD_SCREEN)
                TourGuideType.DASHBOARD_GRAPH_SCREEN.name -> blockedTourGuideList.add(TourGuideType.DASHBOARD_GRAPH_SCREEN)
                TourGuideType.CARD_HOME_SCREEN.name -> blockedTourGuideList.add(TourGuideType.CARD_HOME_SCREEN)
                TourGuideType.PRIMARY_CARD_DETAIL_SCREEN.name -> blockedTourGuideList.add(
                    TourGuideType.PRIMARY_CARD_DETAIL_SCREEN
                )
                TourGuideType.STORE_SCREEN.name -> blockedTourGuideList.add(TourGuideType.STORE_SCREEN)
                TourGuideType.MORE_SCREEN.name -> blockedTourGuideList.add(TourGuideType.MORE_SCREEN)
            }
        }

        return blockedTourGuideList
    }

    fun lockTourGuideScreen(
        screenName: TourGuideType,
        completed: Boolean? = null,
        skipped: Boolean? = null,
        viewed: Boolean? = null
    ) {
        updateTourGuideStatus(
            viewName = screenName.name,
            completed = completed,
            skipped = skipped,
            viewed = viewed
        )
    }

    private fun updateTourGuideStatus(
        viewName: String,
        completed: Boolean? = null,
        skipped: Boolean? = null,
        viewed: Boolean? = null
    ) {
        GlobalScope.launch {
            when (val response = customerRepository.updateTourGuideStatus(
                TourGuideRequest(
                    viewName = viewName,
                    completed = completed,
                    skipped = skipped,
                    viewed = viewed
                )
            )) {
                is RetroApiResponse.Success -> {
                    listOfTourViews.add(
                        TourGuide(
                            viewName = viewName,
                            completed = completed,
                            skipped = skipped
                        )
                    )
                }

                is RetroApiResponse.Error -> {
                }
            }
        }
    }

    fun getTourGuides(success: () -> Unit = {}) {
        GlobalScope.launch {
            when (val response = customerRepository.getTourGuides()) {
                is RetroApiResponse.Success -> {
                    configure(response.data.data as ArrayList<TourGuide>)
                    success.invoke()
                }

                is RetroApiResponse.Error -> {
                }
            }
        }
    }
}
