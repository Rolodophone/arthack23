import org.openrndr.draw.loadImage

class Assets {
	val conversationImage = loadImage("data/images/conversation.png")
	val conversationImageShadow = conversationImage.shadow.apply { download() }
}