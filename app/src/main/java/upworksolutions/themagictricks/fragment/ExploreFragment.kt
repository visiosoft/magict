package upworksolutions.themagictricks.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import upworksolutions.themagictricks.databinding.FragmentExploreBinding

class ExploreFragment : Fragment() {
    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        // You can add any view setup or click listeners here
        binding.tvTitle.text = "Explore Magic Tricks"
        binding.tvSubtitle.text = "Discover Amazing Magic Tricks"
        binding.tvDescription.text = "Welcome to the Explore section! Here you'll find a collection of amazing magic tricks, tutorials, and performances. Browse through different categories and discover new tricks to learn and master."
        binding.tvCategories.text = "Categories:\n• Card Tricks\n• Coin Magic\n• Street Magic\n• Mentalism\n• Close-up Magic"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 