package com.example.magictricks.data

import com.example.magictricks.model.Trick

object TrickDataProvider {
    fun getTrendingTricks(): List<Trick> = listOf(
        Trick(
            id = "1",
            title = "Amazing Donut Vanished",
            description = "Learn this impressive donut vanish trick that will leave your audience amazed. Perfect for beginners!",
            videoUrl = "https://raw.githubusercontent.com/visiosoft/videostreaming/main/donut.mp4",
            thumbnailUrl = "https://i.ytimg.com/vi/cqNSka76wBA/hq720.jpg",
            duration = 180,
            categories = listOf("Card Tricks"),
            isPro = false,
            isFeatured = true
        ),
        Trick(
            id = "2",
            title = "Mind-Blowing Coin Magic",
            description = "IMPOSSIBLE Coin Tricks Anyone Can Do",
            videoUrl = "https://raw.githubusercontent.com/visiosoft/videostreaming/main/2.mp4",
            thumbnailUrl = "https://i.ytimg.com/vi/0V9qnde2Rcc/hq720.jpg",
            duration = 240,
            categories = listOf("Coin Magic"),
            isPro = true,
            isFeatured = true
        ),
        Trick(
            id = "3",
            title = "Funny Magic Tricks Secret Revealed",
            description = "Pencil Tricks Anyone Can Do",
            videoUrl = "https://raw.githubusercontent.com/visiosoft/videostreaming/main/3.mp4",
            thumbnailUrl = "https://i.ytimg.com/vi/yy1u9pHDm5k/hq720_2.jpg",
            duration = 240,
            categories = listOf("Coin Magic"),
            isPro = true,
            isFeatured = true
        ),
        Trick(
            id = "4",
            title = "Funny Magic Tricks And DIY Illusions That You Can Do",
            description = "3 Magic Tricks Anyone Can Do",
            videoUrl = "https://raw.githubusercontent.com/visiosoft/videostreaming/main/4.mp4",
            thumbnailUrl = "https://raw.githubusercontent.com/visiosoft/videostreaming/main/4.jpg",
            duration = 240,
            categories = listOf("Coin Magic"),
            isPro = true,
            isFeatured = true
        )
    )
} 