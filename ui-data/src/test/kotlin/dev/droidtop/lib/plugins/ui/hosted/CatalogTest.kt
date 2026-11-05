package dev.droidtop.lib.plugins.ui.hosted

import dev.droidtop.lib.plugins.ui.confirmText
import dev.droidtop.lib.plugins.ui.isInert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

/** The catalog model as a host uses it, through its typealiases (Host.kt), as droidtop does over Context. */
class CatalogTest {
    /** One host's own kind of item, as droidtop's folder and document pickers are. */
    private class PickItem(override val id: String, override val title: String, private val ask: String?) : PlatformItem() {
        override val subtitle: String? get() = null
        override val confirmText: String? get() = ask
    }

    @Test
    fun `an action with nothing to run is inert, and a paragraph too`() {
        assertTrue(ActionItem("a", "Version", value = "1.0").isInert)
        assertFalse(ActionItem("b", "Open", run = { _: TestHost -> }).isInert)
        assertTrue(TextBlockItem("t", "Some words").isInert)
    }

    @Test
    fun `a nested screen resolves inline, or through the host's registry`() {
        val screen = CatalogScreen("s", "Screen", groups = { emptyList() })
        assertSame(screen, NestedScreenItem("n", "Go", inline = screen).resolve { null })
        assertSame(screen, NestedScreenItem("r", "Go", registryId = "theirs").resolve { if (it == "theirs") screen else null })
        assertNull(NestedScreenItem("m", "Go", registryId = "missing").resolve { null })
    }

    @Test
    fun `a host's own item says what it asks before it runs`() {
        assertEquals("Replace the file?", (PickItem("p", "Pick", "Replace the file?") as CatalogItem).confirmText)
        assertNull((PickItem("q", "Pick", null) as CatalogItem).confirmText)
        assertEquals("Sure?", (ActionItem("x", "Delete", confirmTitle = "Sure?", run = { _: TestHost -> }) as CatalogItem).confirmText)
    }

    @Test
    fun `a page puts its header and buttons in a top group before its own`() {
        val group = CatalogGroup("g", "Group", items = listOf(ActionItem("a", "A")))
        val page = CatalogPage(
            header = PageHeader("Title", byline = "By"),
            buttons = listOf(PageButton("p", "Play", role = ButtonRole.PRIMARY, run = { _: TestHost -> })),
            groups = listOf(group),
        )
        val groups = page.asGroups()
        assertEquals(listOf(CatalogPage.TOP_GROUP_ID, "g"), groups.map { it.id })
        assertTrue(groups[0].items[0] is PageHeaderItem)
        assertTrue(groups[0].items[1] is ButtonBarItem)
        assertEquals(listOf("g"), CatalogPage(groups = listOf(group)).asGroups().map { it.id })
    }

    @Test(expected = IllegalArgumentException::class)
    fun `a danger button always confirms`() {
        ButtonBarItem(listOf(PageButton("d", "Delete", role = ButtonRole.DANGER, run = { _: TestHost -> })))
    }
}
