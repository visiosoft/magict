package upworksolutions.themagictricks.activity

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import coil.load
import upworksolutions.themagictricks.R
import upworksolutions.themagictricks.model.TipCard

class TipDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tip_detail)

        // Get tip card data from intent
        val tipCard = intent.getParcelableExtra<TipCard>("tip_card")
        tipCard?.let { setupUI(it) }
    }

    private fun setupUI(tipCard: TipCard) {
        // Set up toolbar
        supportActionBar?.apply {
            title = tipCard.title
            setDisplayHomeAsUpEnabled(true)
        }

        // Load background image
        findViewById<ImageView>(R.id.backgroundImage).load(tipCard.backgroundImageUrl) {
            crossfade(true)
        }

        // Set text content
        findViewById<TextView>(R.id.titleText).text = tipCard.title
        findViewById<TextView>(R.id.descriptionText).text = tipCard.description
        findViewById<TextView>(R.id.difficultyText).text = "Difficulty: ${tipCard.difficulty}"
        findViewById<TextView>(R.id.secretTipText).text = "Secret Tip: ${tipCard.secretTip}"
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
} 