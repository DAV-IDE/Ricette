package it.uninsubria.ricette
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
class CustomAdapter (context: Context, private val ricette: List<String>) : ArrayAdapter<String>(context, 0, ricette) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.ingredienti_list_item, parent, false)
        val textView = view.findViewById<TextView>(R.id.textView)
        textView.text = ricette[position]
        return view
    }
}
