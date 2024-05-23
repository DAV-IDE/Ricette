package it.uninsubria.ricette

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView
import java.util.ArrayList

class CustomAdapter(private val context: Context, private val ingredients: ArrayList<String>) : BaseAdapter() {

    // Questo set terrà traccia degli elementi selezionati
    val selectedIngredients = HashSet<Int>()

    override fun getCount(): Int = ingredients.size

    override fun getItem(position: Int): Any = ingredients[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.ingredienti_list_item, parent, false)
        val checkBox: CheckBox = view.findViewById(R.id.ingredient_checkbox)
        val textView: TextView = view.findViewById(R.id.textView)
        textView.text = ingredients[position]

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedIngredients.add(position)
            } else {
                selectedIngredients.remove(position)
            }
        }

        // Mantenere lo stato della selezione quando scorri la lista
        checkBox.isChecked = selectedIngredients.contains(position)

        return view
    }
}