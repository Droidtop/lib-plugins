package dev.droidtop.lib.plugins.ui.hosted

import dev.droidtop.lib.plugins.ui.spokenName

// droidtop runtime-common's test of the same name, moved with the code it tests (Droidtop/tracker#867).

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.yield
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The danger dialog's contract and the flash's Recent list (docs/SPEC.md 7k, "Asking before a change, and the
 * flash"; Droidtop/tracker#485, final design tests 15 and 18, the parts that hold without a screen): with no
 * surface to ask on the answer is Cancel; the surface on top draws; questions come one at a time; a flash lands
 * (the Recent list of flashes is droidtop's Notices, and its test stayed there).
 */
class NoticeDialogsTest {
    private val danger = DangerConfirm(Notice(NoticeTone.Danger, "Allow full access?", recommendation = "We recommend Cancel."), "Install anyway")
    private val hosts = mutableListOf<Any>()

    private fun attach(): Any = Any().also { NoticeDialogs.attachHost(it); hosts += it }

    @After
    fun detach() {
        hosts.forEach { NoticeDialogs.detachHost(it) }
    }

    @Test
    fun `with no surface attached the answer is Cancel`() = runBlocking {
        assertEquals(ConfirmAnswer.Cancel, NoticeDialogs.confirmDanger(danger))
    }

    @Test
    fun `waiting for a host ends when one attaches, and gives up without one`() = runBlocking {
        assertFalse(NoticeDialogs.awaitHost(50))
        val waiting = async { NoticeDialogs.awaitHost(5_000) }
        yield()
        attach()
        assertTrue(waiting.await())
        assertTrue(NoticeDialogs.awaitHost(100))
    }

    @Test
    fun `the last host attached draws, and its answer comes back`() = runBlocking {
        val first = attach()
        val second = attach()
        assertTrue(NoticeDialogs.drawsHere(second))
        assertFalse(NoticeDialogs.drawsHere(first))
        val answer = async { NoticeDialogs.confirmDanger(danger) }
        val request = withTimeout(1_000) {
            while (NoticeDialogs.pending.value == null) yield()
            NoticeDialogs.pending.value!!
        }
        assertTrue(request.danger)
        assertEquals("Cancel", request.cancelLabel)
        assertEquals("Install anyway", request.proceedLabel)
        NoticeDialogs.answer(request, ConfirmAnswer.Proceed)
        assertEquals(ConfirmAnswer.Proceed, answer.await())
        assertNull(NoticeDialogs.pending.value)
    }

    @Test
    fun `a second question waits for the first`() = runBlocking {
        attach()
        val first = async { NoticeDialogs.confirmDanger(danger) }
        val shown = withTimeout(1_000) {
            while (NoticeDialogs.pending.value == null) yield()
            NoticeDialogs.pending.value!!
        }
        val second = async { NoticeDialogs.confirm(Notice(NoticeTone.Caution, "Turn on?"), proceedLabel = "Turn on") }
        yield()
        assertTrue(NoticeDialogs.pending.value === shown)
        NoticeDialogs.answer(shown, ConfirmAnswer.Cancel)
        assertEquals(ConfirmAnswer.Cancel, first.await())
        val next = withTimeout(1_000) {
            while (NoticeDialogs.pending.value == null || NoticeDialogs.pending.value === shown) yield()
            NoticeDialogs.pending.value!!
        }
        assertFalse(next.danger)
        NoticeDialogs.answer(next, ConfirmAnswer.Proceed)
        assertEquals(ConfirmAnswer.Proceed, second.await())
    }
}
