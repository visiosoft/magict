package upworksolutions.themagictricks.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import upworksolutions.themagictricks.R
import upworksolutions.themagictricks.model.Trick

class VideoPlayerFragment : Fragment() {
    private var trick: Trick? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            trick = it.getParcelable("trick")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_video_player, container, false)
    }

    companion object {
        fun newInstance(trick: Trick): VideoPlayerFragment {
            return VideoPlayerFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("trick", trick)
                }
            }
        }
    }
} 