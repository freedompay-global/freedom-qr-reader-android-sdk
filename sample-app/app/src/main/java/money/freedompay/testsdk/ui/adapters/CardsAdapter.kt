package money.freedompay.testsdk.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import money.freedompay.qrreader.api.models.responses.TokenizedCardDetailResponse
import money.freedompay.testsdk.databinding.CardItemBinding
import money.freedompay.testsdk.extencions.getCardType
import money.freedompay.testsdk.extencions.maskedCardPan

class CardsAdapter : RecyclerView.Adapter<CardsAdapter.ViewHolder>() {

    private var items = mutableListOf<TokenizedCardDetailResponse>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = CardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    fun setData(newItems: List<TokenizedCardDetailResponse>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun getData() = items.toMutableList()

    class ViewHolder(private val binding: CardItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TokenizedCardDetailResponse) = with(binding) {
            val text = item.cardHash.maskedCardPan()
            cardText.text = text
            cardImageView.setImageResource(item.cardHash.getCardType().cardIcon)
        }
    }
}
