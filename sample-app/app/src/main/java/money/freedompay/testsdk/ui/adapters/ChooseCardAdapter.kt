package money.freedompay.testsdk.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import money.freedompay.qrreader.api.models.responses.TokenizedCardDetailResponse
import money.freedompay.testsdk.R
import money.freedompay.testsdk.extencions.maskedCardPan

class ChooseCardAdapter(context: Context, private val items: List<TokenizedCardDetailResponse>) : BaseAdapter() {

    private val inflater = LayoutInflater.from(context)

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): TokenizedCardDetailResponse = items[position]

    override fun getItemId(position: Int): Long = -1

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = inflater.inflate(R.layout.choose_card_item, parent, false)
        val text = items[position].cardHash.maskedCardPan()
        view.findViewById<TextView>(R.id.cardItem).text = text
        return view
    }
}
