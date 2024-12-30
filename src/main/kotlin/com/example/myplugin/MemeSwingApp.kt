import javax.swing.*
import java.awt.*
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter
import java.awt.event.MouseAdapter
import java.net.MalformedURLException
import java.net.URL


class MemeSwingApp {
    companion object {
        const val SCREEN_WIDTH = 800
        const val SCREEN_HEIGHT = 600
        const val MEME_URL = "https://content.imageresizer.com/images/memes/Pet-the-cat-meme-6.jpg"
    }

    fun createAndShowGUI() {
        SwingUtilities.invokeLater {
            val frame = JFrame("Meme Follower")
            frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            frame.setSize(SCREEN_WIDTH, SCREEN_HEIGHT)
            frame.setLocationRelativeTo(null)

            val memePanel = try {
                MemePanel()
            } catch (e: MalformedURLException) {
                throw RuntimeException(e)
            }
            frame.add(memePanel)
            frame.isVisible = true
        }
    }
}

class MemePanel : JPanel() {
    private val memeImage: Image
    private var memePosition: Point? = null
    private var memeEntraceDirection: String? = null
    private var isVisible = false

    init {
        // Load the meme image
        memeImage = ImageIcon(URL(MemeSwingApp.MEME_URL)).image

        // Mouse listeners to track cursor entry
        addMouseMotionListener(object : MouseMotionAdapter() {
            override fun mouseMoved(e: MouseEvent?) {
                if (isVisible) {
                    memePosition = e?.point
                    repaint()
                }
            }
        })

        addMouseListener(object : MouseAdapter() {
            override fun mouseEntered(e: MouseEvent?) {
                memePosition = e?.point
                isVisible = true
                val MARGIN = 12
                memePosition?.let {
                    memeEntraceDirection = when {
                        it.x <= MARGIN -> "left"
                        it.x >= width - MARGIN -> "right"
                        it.y <= MARGIN -> "top"
                        it.y >= height - MARGIN -> "bottom"
                        else -> null
                    }
                }
                repaint()
            }

            override fun mouseExited(e: MouseEvent?) {
                // Hide the meme when the cursor leaves the window
                isVisible = false
                repaint()
            }
        })
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        // Draw the meme if it's visible
        if (isVisible && memePosition != null && memeEntraceDirection != null) {
            val newDims = getImgDimensionsAtPosition(memePosition!!.x, memePosition!!.y)
            val memeWidth = newDims.width
            val memeHeight = newDims.height
            val x = memePosition!!.x - memeWidth / 2
            val y = memePosition!!.y - memeHeight / 2

            g.drawImage(memeImage, x, y, memeWidth, memeHeight, this)
        }
    }

    private fun getImgDimensionsAtPosition(xPosition: Int, yPosition: Int): Dimension {
        // Scale meme according to direction and screen dimensions
        var mousePosition = xPosition.toDouble()
        var totalLength = MemeSwingApp.SCREEN_WIDTH.toDouble()
        when (memeEntraceDirection) {
            "right" -> mousePosition = width - xPosition.toDouble()
            "top" -> {
                mousePosition = yPosition.toDouble()
                totalLength = MemeSwingApp.SCREEN_HEIGHT.toDouble()
            }
            "bottom" -> {
                mousePosition = height - yPosition.toDouble()
                totalLength = MemeSwingApp.SCREEN_HEIGHT.toDouble()
            }
        }

        val initialWidth = memeImage.getWidth(this) / 4
        val finalWidth = memeImage.getWidth(this)
        val widthDiff = finalWidth - initialWidth
        val widthExpansionRatio = mousePosition / totalLength
        val imgAspectRatio = memeImage.getWidth(this).toDouble() / memeImage.getHeight(this)

        // Cap the maximum/minimum size of the meme
        var newWidth = (initialWidth + widthDiff * widthExpansionRatio).toInt()
        if (newWidth > finalWidth) newWidth = finalWidth
        else if (newWidth < initialWidth) newWidth = initialWidth

        val newHeight = (newWidth * imgAspectRatio).toInt()
        return Dimension(newWidth, newHeight)
    }
}