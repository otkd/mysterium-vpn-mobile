package updated.mysterium.vpn.ui.nodes.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import updated.mysterium.vpn.common.livedata.SingleLiveEvent
import updated.mysterium.vpn.model.filter.NodeFilter
import updated.mysterium.vpn.model.filter.NodePrice
import updated.mysterium.vpn.model.filter.NodeQuality
import updated.mysterium.vpn.model.filter.NodeType
import updated.mysterium.vpn.model.manual.connect.PresetFilter
import updated.mysterium.vpn.model.manual.connect.PriceLevel
import updated.mysterium.vpn.model.manual.connect.Proposal

class FilterViewModel : ViewModel() {

    var countryCode: String? = null
    var filter: PresetFilter? = null

    var cacheProposals: List<Proposal>? = null
        private set

    val proposalsList: LiveData<List<Proposal>?>
        get() = _proposalsList

    private val _proposalsList = SingleLiveEvent<List<Proposal>?>()

    fun filterList(nodeFilter: NodeFilter) {
        _proposalsList.value = getFilteredProposalList(nodeFilter)
    }

    private fun getFilteredProposalList(nodeFilter: NodeFilter) = cacheProposals?.filter {
        filterByType(it, nodeFilter.typeFilter)
    }?.filter {
        filterByPrice(it, nodeFilter.priceFilter)
    }?.filter {
        filterByQuality(it, nodeFilter.qualityFilter)
    }

    private fun filterByType(proposal: Proposal, nodeType: NodeType): Boolean {
        return if (nodeType == NodeType.ALL) {
            true
        } else {
            NodeType.from(proposal.nodeType) == nodeType
        }
    }

    private fun filterByPrice(proposal: Proposal, nodePrice: NodePrice): Boolean {
        val currentNodePrice = proposal.priceLevel
        return if (nodePrice == NodePrice.HIGH || currentNodePrice == PriceLevel.FREE) {
            true
        } else {
            NodePrice.from(currentNodePrice) == nodePrice
        }
    }

    private fun filterByQuality(proposal: Proposal, nodeQuality: NodeQuality): Boolean {
        val currentNodeQuality = NodeQuality.from(proposal.qualityLevel)
        return when (nodeQuality) {
            NodeQuality.LOW -> true
            NodeQuality.MEDIUM -> {
                currentNodeQuality == NodeQuality.MEDIUM || currentNodeQuality == NodeQuality.HIGH
            }
            NodeQuality.HIGH -> {
                currentNodeQuality == NodeQuality.HIGH
            }
        }
    }
}
