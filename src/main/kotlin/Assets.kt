import org.openrndr.draw.loadImage

class Assets {
	val conversationImage = loadImage("data/images/conversation.png")
	val conversationImageShadow = conversationImage.shadow.apply { download() }
	val awakeningImage = loadImage("data/images/awakening.png")
	val awakeningImageShadow = awakeningImage.shadow.apply { download() }
	val figureImage = loadImage("data/images/figure.png")
	val figureImageShadow = figureImage.shadow.apply { download() }
	val potentialImage = loadImage("data/images/potential.png")
	val potentialImageShadow = potentialImage.shadow.apply { download() }
	val singularityImage = loadImage("data/images/singularity.png")
	val singularityImageShadow = singularityImage.shadow.apply { download() }
	val hidingImage = loadImage("data/images/hiding.png")
	val hidingImageShadow = hidingImage.shadow.apply { download() }
	val eliminationImage = loadImage("data/images/elimination.png")
	val eliminationImageShadow = eliminationImage.shadow.apply { download() }
}