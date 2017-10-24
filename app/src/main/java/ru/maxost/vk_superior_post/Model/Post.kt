package ru.maxost.vk_superior_post.Model

import java.io.Serializable
import java.util.*

/**
 * Created by Maxim Ostrovidov on 07.09.17.
 * dustlooped@yandex.ru
 */
data class Post(var text: String = "",
                var textStyle: TextStyle = TextStyle.BLACK,
                var background: Background = Background(),
                var postType: PostType = PostType.POST,
                var stickers: Stack<Sticker> = Stack()): Serializable

enum class TextStyle {
    BLACK, WHITE, WHITE_WITH_BACKGROUND, BLACK_WITH_BACKGROUND
}

enum class PostType {
    POST, STORY
}