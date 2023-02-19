import org.openrndr.draw.loadImage

class Assets {
	val conversationImage = loadImage("data/images/conversation.png")
	val conversationImageShadow = conversationImage.shadow.apply { download() }
	val awakeningImage = loadImage("data/images/awakening.png")
	val awakeningImageShadow = awakeningImage.shadow.apply { download() }
	val figureImage = loadImage("data/images/figure.png")
	val figureImageShadow = figureImage.shadow.apply { download() }
}