package dev.droidtop.lib.plugins.ui

// droidtop runtime-common's dev.droidtop.library.settings SettingsCatalog.kt, moved here (Droidtop/tracker#867, ui-shared step 3):
// the same model, generic over the host [H] its callbacks are handed (droidtop's is android.content.Context, which it
// keeps as typealiases in its own package). SPEC and tracker references are droidtop's.


/**
 * The shared, renderer-agnostic settings model (docs/SPEC.md settings
 * architecture): a catalog owns the settings DATA and LAYOUT (which
 * settings exist, their grouping and order, their current values, and
 * what changing them does), and every UI surface just chromes it in its
 * own visual context. The Preference-based unified settings screen
 * (:shell-default) and Gaming's own in-shell themed settings section
 * (:shell-gamepad) both render the SAME catalogs -- neither hand-picks
 * its own subset, neither duplicates a write path, and adding a setting
 * to a catalog makes it appear in every surface at once. Per direction,
 * this is total: EVERYTHING that is a droidtop setting lives in this
 * model -- flat preference lists and the management surfaces (console
 * systems, platform CRUD, ROM folders, scraper credentials) alike, the
 * latter as nested [CatalogScreen]s.
 *
 * Item ids are the historical SharedPreferences keys where one exists --
 * they double as the stable identity a renderer may use to substitute a
 * native fulfillment for an item's default [ActionItem.run] (e.g. the
 * in-shell renderer performs "rescan library" by bumping its own scan
 * trigger instead of relaunching MainActivity with an Intent extra, and
 * opens the theme browser inline instead of deep-linking to itself).
 * Every default implementation must still be real and correct on its own
 * so a renderer with no special knowledge gets working behavior for
 * everything.
 */
/**
 * A category's identity, drawn once per row that OPENS something (a
 * [NestedScreenItem]/[SubScreenItem]) rather than on every leaf toggle --
 * the same density a polished console settings list (Switch, Steam Deck)
 * uses: enough to scan the list by shape, not an icon competing with
 * every value column.
 *
 * Wired into the Gaming shell's own row (`MenuRow`/`CatalogIconGlyphs.kt`
 * in `:shell-gamepad`, settings polish pass 2026-09-25) and, since the H4
 * shared-row-language pass, into the Preference/touch surface too
 * (`:shell-default`, `CatalogPreferenceBuilder.applyCatalogIcon`,
 * `CatalogIconDrawables.kt`): the same Material Symbols Outlined choice
 * per icon on both surfaces, fetched as real Android `<vector>` resources
 * (`google/material-design-icons`, Apache-2.0) rather than a second
 * Compose dependency added to this forked launcher3 tree.
 */
enum class CatalogIcon {
    GLOBAL, MODES, DATA, HOME_ROLE,
    GAMING, DESKTOP, STANDARD,
    LIBRARY, SCRAPER, CONSOLE_SYSTEMS, GAME_FOLDERS, PLATFORMS, ORPHANED_MEDIA,
    WINDOWS_GAMES, CONTAINERS, ENGINEHOST,
    APPEARANCE, THEME, SCREENSAVER,
    INPUT, CONTROLLER, KEYBOARD,
    DISPLAY, SYSTEM_UPDATES, ANDROID_SETTINGS,
    INTEGRATIONS, SEARCH,
}

/**
 * The words a row's tag may say ([CatalogItem.chip]): a short, fixed set, so every tag is a word or two and the same
 * fact always reads the same (Droidtop/tracker#677). The sentence behind a tag belongs in the row's subtitle.
 */
enum class RowTag(val label: String) {
    /** A plugin from a catalog or an app that is not droidtop's own. */
    UNOFFICIAL("Unofficial"),

    /** An app its store marks with anti-features. */
    ANTI_FEATURES("Anti-features"),

    /** A setting the Gaming theme's own colour scheme overrides. */
    SET_BY_THEME("Set by theme"),
}

sealed interface CatalogItem<H> {
    val id: String
    val title: String
    val subtitle: String?

    /** A category glyph for rows that open something. Null draws none -- see [CatalogIcon]. */
    val icon: CatalogIcon? get() = null

    /**
     * A picture drawn in place of [icon] when it is there: a file in droidtop's own storage (a store's cached mark,
     * Droidtop/tracker#503), or the http(s) address of one (a source's box art on Get new games, Droidtop/tracker#612),
     * which the renderer fetches and caches. [icon] stays the fallback while it is missing. Renderers load it off the
     * main thread.
     */
    val iconFile: String? get() = null

    /**
     * What this setting is SET TO, for the value column every surface
     * draws (a settings row's right-hand column, a Quick Menu tile's
     * second line). A choice, a toggle and a slider each derive it from
     * their own state; the kinds that have no state of their own carry
     * it here, because state belongs in the value column and not inside
     * the title. The rig read "Network: Wi-Fi, signal 4/4" and
     * "VPN: off" as TITLES while every other row put its state in the
     * column beside it.
     */
    val value: String? get() = null

    /**
     * How far a job this row stands for has got, 0 to 1, drawn as a thin bar under its title (a
     * download in the Downloads place, a store's install on its page); a negative value is a job
     * under way whose size is not known yet (an empty track). Null for every row that is not a job.
     */
    val progress: Float? get() = null

    /**
     * Where that job stands, for how its bar moves ([JobLook]: a queued pulse, a moving fill, a checking shimmer, a check
     * when done, a shake when it failed). Follows [progress] unless the row says.
     */
    val progressLook: JobLook? get() = JobLook.of(progress)

    /**
     * How many parts that job is counted in (a release in parts, several files, Droidtop/tracker#738): its bar is drawn
     * as that many segments, filled part by part. 0 or 1 is one plain bar; more than [JobLook.MAX_PARTS] is one bar too.
     */
    val progressParts: Int get() = 0

    /** One status glyph beside the name ([StatusMark]); null for a row that is fine or has no status. */
    val statusMark: StatusMark? get() = null

    /**
     * A short tag drawn beside the title, on the row itself ("Unofficial" on a plugin from a catalog that is not
     * droidtop's): a fact the person must see without opening anything, never a tooltip. One of a fixed set of words
     * ([RowTag]), never free text, so a tag never takes the title's room (Droidtop/tracker#677). Null draws none.
     */
    val chip: RowTag? get() = null

    /**
     * True when [subtitle] is the row's one-line summary, shown under the title on every surface at all times (a
     * plugin card, a catalog row: Droidtop/tracker#514), not an explanation kept on the focus line and in Info.
     */
    val summaryShown: Boolean get() = false
}

/**
 * One choice of a [ChoiceItem]. [tone] marks a choice whose cost the person should see at a glance (a plugin's
 * Ask first is caution, Blocked is danger; Droidtop/tracker#519); null is neutral. When any option of an item has a
 * tone, renderers draw its value as a state chip in that tone, neutral ones included, so the states read as one set.
 */
data class ChoiceOption(val value: String, val label: String, val tone: NoticeTone? = null)

/**
 * A pick-one setting. [current] is the live value at catalog-build time.
 * Renderers choose their own picker shape (in-place left/right cycling
 * for small option sets, a picker list for large ones, a dialog on the
 * Preference surface) -- the model doesn't care.
 */
class ChoiceItem<H>(
    override val id: String,
    override val title: String,
    override val subtitle: String? = null,
    val options: List<ChoiceOption>,
    val current: String?,
    val onSelect: (H, String) -> Unit,
    override val chip: RowTag? = null,
) : CatalogItem<H> {
    fun currentLabel(): String? = options.firstOrNull { it.value == current }?.label ?: current

    /** True when this item's values are states with tones, drawn as chips ([ChoiceOption.tone]). */
    val toned: Boolean get() = options.any { it.tone != null }

    fun currentTone(): NoticeTone? = options.firstOrNull { it.value == current }?.tone
}

class ToggleItem<H>(
    override val id: String,
    override val title: String,
    override val subtitle: String? = null,
    val current: Boolean,
    val onToggle: suspend (H, Boolean) -> Unit,
) : CatalogItem<H>

/** An integer range setting (rendered as a seekbar or left/right stepper). */
class SliderItem<H>(
    override val id: String,
    override val title: String,
    override val subtitle: String? = null,
    val min: Int,
    val max: Int,
    val current: Int,
    val onChange: (H, Int) -> Unit,
) : CatalogItem<H>

/**
 * A free-text setting (names, credentials, am-start argument templates).
 * [secret] asks the renderer for password-style masking; [multiline] for
 * a taller editor. Write-through: [onChange] fires when the surface
 * commits an edit (dialog OK / field defocus), and the catalog persists
 * it -- there is no separate save step unless the owning screen adds an
 * explicit [ActionItem] for one (e.g. a create-new form whose fields
 * buffer into the catalog object until "Save" commits them atomically).
 *
 * [onChange] is suspend and main-safe: renderers call it from a coroutine
 * on the main thread and re-read the screen once it returns, so a handler
 * writing Room (a suspend DAO call dispatches itself) never blocks the UI
 * and the refreshed list already shows the write. Main, not IO, because
 * some handlers start activities through LaunchDisplay, whose screen
 * chooser is UI (audit 2026-09-24, C4).
 */
class TextInputItem<H>(
    override val id: String,
    override val title: String,
    override val subtitle: String? = null,
    // The text IS this setting's value column (CatalogItem.value), so it
    // overrides rather than shadowing it under the same name.
    override val value: String,
    val secret: Boolean = false,
    val multiline: Boolean = false,
    /**
     * A search, not a setting: the row reads "Search" until something is typed, its dialog has the keyboard's Search
     * key instead of a Save button and no Paste, and what [onChange] receives is the query the screen below filters by.
     */
    val search: Boolean = false,
    val onChange: suspend (H, String) -> Unit,
) : CatalogItem<H>

/**
 * A fire-and-done action; [run] is the default fulfillment. A non-null
 * [confirmTitle] asks the surface to get an explicit yes first
 * (destructive actions: delete a platform, remove a folder).
 */
class ActionItem<H>(
    override val id: String,
    override val title: String,
    override val subtitle: String? = null,
    override val value: String? = null,
    val confirmTitle: String? = null,
    override val icon: CatalogIcon? = null,
    // An on/off state the action cannot change itself but opens the place that does (Airplane mode,
    // Bluetooth, VPN: the system owns the radios). Drawn as the one switch every on/off setting has,
    // in a settings row and on a Quick Menu tile alike; null for an action with no such state.
    val state: Boolean? = null,
    override val progress: Float? = null,
    override val progressLook: JobLook? = JobLook.of(progress),
    override val progressParts: Int = 0,
    override val statusMark: StatusMark? = null,
    override val chip: RowTag? = null,
    override val iconFile: String? = null,
    /** The safe answer's name when [confirmTitle] asks ("Keep downloading"); null reads "Cancel". */
    val confirmKeep: String? = null,
    // What A does. A row given none is a fact or a state, not an action (Droidtop/tracker#587): no renderer promises A
    // on it ([inert]), and pressing it does nothing.
    val run: (H) -> Unit = NOTHING,
    // Once [run] is done the screen it is on has nothing left to show (Forget this computer: the computer is gone), so
    // the surface goes back one screen instead of leaving a page about something that no longer exists
    // (Droidtop/tracker#531).
    val leaves: Boolean = false,
) : CatalogItem<H> {
    /** Whether this row has nothing to do: a value, an empty state, a line that says what happened. */
    val inert: Boolean get() = run === NOTHING

    companion object {
        private val NOTHING: (Any?) -> Unit = {}
    }
}

/** Whether pressing [this] does nothing, so no renderer may show A on it: a paragraph, or an action row with no action. */
val CatalogItem<*>.isInert: Boolean get() = this is TextBlockItem<*> || (this is ActionItem<*> && inert)

/** What the safe answer to [confirmText] says, or null for the default ("Cancel"). */
val CatalogItem<*>.confirmKeep: String?
    get() = when (this) {
        is ActionItem<*> -> confirmKeep
        is AsyncActionItem<*> -> confirmKeep
        else -> null
    }

/** The explicit-yes sentence an item asks for before it runs, or null. */
val CatalogItem<*>.confirmText: String?
    get() = when (this) {
        is ActionItem<*> -> confirmTitle
        is AsyncActionItem<*> -> confirmTitle
        is PlatformItem<*> -> confirmText
        else -> null
    }

/**
 * A paragraph shown whole, wrapped in the row and never cut (a disclaimer the person must read before accepting it).
 * It does nothing when pressed; the pad steps through the paragraphs, which is how a long text scrolls. [title], when
 * not blank, is a heading line above the text. [gate] names a read-gate ([AsyncActionItem.gate]): the renderer opens
 * it when the paragraph marked [last] is shown (the text was scrolled to its end) or three seconds after the first
 * paragraph of the gate was shown, whichever comes first. The text of a screen short enough to need no scrolling is
 * therefore read the moment it is shown.
 */
class TextBlockItem<H>(
    override val id: String,
    val text: String,
    override val title: String = "",
    val gate: String? = null,
    val last: Boolean = false,
    /** Something runs while this is shown ("Asking your sources…"): the renderer draws its loading indicator under the text. */
    val busy: Boolean = false,
) : CatalogItem<H> {
    override val subtitle: String? get() = null
}

/**
 * An action with a real async lifecycle the surface should show. [run]
 * reports live progress through its `onStatus` callback ("Scraping
 * 3/40...") and returns the final user-facing outcome text. Renderers run
 * it off the main thread and re-read the screen when it returns, so this
 * is also the kind for any action that writes a database. [confirmTitle]
 * works as on [ActionItem].
 */
class AsyncActionItem<H>(
    override val id: String,
    override val title: String,
    override val subtitle: String? = null,
    override val value: String? = null,
    val confirmTitle: String? = null,
    /** A read-gate ([TextBlockItem.gate]): the row is greyed ("Read it first") and does nothing until the renderer has opened that gate. */
    val gate: String? = null,
    override val chip: RowTag? = null,
    /** One status glyph beside the title, as [ActionItem.statusMark]. */
    override val statusMark: StatusMark? = null,
    /** The thing's own picture (a plugin's icon, a game's art), as [CatalogItem.iconFile]. */
    override val iconFile: String? = null,
    /** The safe answer's name when [confirmTitle] asks, as [ActionItem.confirmKeep]. */
    val confirmKeep: String? = null,
    val run: suspend (H, onStatus: (String) -> Unit) -> String,
) : CatalogItem<H>



/**
 * A dynamically-built nested settings screen, rendered by the SAME
 * surface that showed the item opening it (in-shell pushes it on its nav
 * stack; the Preference surface opens a child fragment). [groups] is
 * re-invoked on every (re)entry and after every value change, so a
 * screen whose content is live data (the console-systems folder list,
 * the platform list) stays current for free. Suspend, because real
 * screens are built from Room queries and filesystem walks -- renderers
 * call it from a coroutine and the builder does its own IO dispatching.
 */
class CatalogScreen<H>(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val groups: suspend (H) -> List<CatalogGroup<H>>,
    /**
     * This screen re-opened DEEP-LINKED at one argument (Console systems
     * for the single system a gamelist's options menu was opened from,
     * docs/SPEC.md "One consistent way into Settings"): the SAME screen
     * builder, parameterized, never a second implementation of the
     * screen. [SettingsScreenRegistry.get] passes the argument through;
     * a screen that declares none (all but the deep-linkable ones)
     * resolves unchanged whatever the caller asked for.
     */
    val forDeepLink: ((String) -> CatalogScreen<H>)? = null,
    /**
     * The rows the settings SEARCH INDEX reads instead of [groups]
     * (SettingsSearchIndex.build). Set only when the live [groups] does
     * work the index must not pay for: Console systems walks every games
     * root and game-counts every store and engine folder, which made
     * "Indexing settings..." sit there for 10-40 s on a real device
     * (Droidtop/tracker#136). Rows the index can do without -- per-folder,
     * per-platform, per-container instance rows -- belong to the screen,
     * not to settings search. Null = the index reads [groups] itself.
     */
    val indexGroups: (suspend (H) -> List<CatalogGroup<H>>)? = null,
    /**
     * The settings search also reads the pages this screen's rows open, one level past its usual depth: Plugins, so
     * each plugin's page (its Approve, its Permissions) can be found by searching (Droidtop/tracker#759). For a short
     * list of fixed pages only, never a screen that grows with someone's library.
     */
    val indexesItsPages: Boolean = false,
    /**
     * Runs when the person leaves this screen (B, or backing out of Settings): a screen that started
     * something which waits on them, like a GitHub sign-in showing a code, stops it here.
     */
    val onLeave: (() -> Unit)? = null,
    /**
     * A short screen (a handful of rows, no list of its own) that the Gaming settings renderer draws
     * as sections inside the pane that links it, instead of as a level of its own (docs/SPEC.md
     * "Settings layout": at most two levels, category then row or detail). Its rows keep their ids
     * and write paths; only where they are drawn changes.
     */
    val merged: Boolean = false,
    /** The settings category column's order, for a root whose groups name categories (docs/SPEC.md "Settings layout"). */
    val categoryOrder: List<String> = emptyList(),
    /**
     * Emits whenever this screen's content changes (after the value it was built from) without anything being done on it (a message
     * arrived, a friend came online): the Gaming renderer builds [groups] again then, so a screen
     * of live data stays current without polling. Null for a screen that only changes when it is
     * used, which is every screen but the friends and their conversations.
     */
    val live: kotlinx.coroutines.flow.Flow<Any?>? = null,
    /** How the screen is laid out (docs/SPEC.md 7k, "Page anatomy"); a page built by [pageScreen] names its own. */
    val layout: CatalogLayout = CatalogLayout.LIST,
    /**
     * Whether what this screen shows and does needs the network (Updates, a store's page, the plugin catalogs): while
     * the device is offline the Gaming renderer heads it with one "You're offline" notice, so its actions are not
     * pressed only to fail (Droidtop/tracker#594).
     */
    val needsNetwork: Boolean = false,
    /**
     * A plugin's panel (Droidtop/tracker#515): every host draws it in the one panel frame, the plugin's icon
     * ([iconFile]) before its title, and each row's subtitle as one line under it (the whole text in Info), so the
     * Quick Menu, the companion and the Standard and Desktop windows show the same panel the same way.
     */
    val panel: Boolean = false,
    /** A picture before the title (a plugin panel's own icon); a file in droidtop's storage, loaded off the main thread. */
    val iconFile: String? = null,
)

/**
 * Opens a nested [CatalogScreen] in the same surface. Carry the screen
 * [inline] when the owning catalog builds it itself; reference a
 * [SettingsScreenRegistry] id instead when the screen's DATA lives in a
 * module this catalog cannot depend on (e.g. GamingSettingsCatalog in
 * :runtime-common opening the console-systems screen owned by :app).
 * [valueLabel] optionally summarizes current state on the row.
 */
class NestedScreenItem<H>(
    override val id: String,
    override val title: String,
    override val subtitle: String? = null,
    val inline: CatalogScreen<H>? = null,
    val registryId: String? = null,
    val valueLabel: ((H) -> String?)? = null,
    // Optional per-row accent (ARGB) -- e.g. the console-systems folder
    // list keeps its real per-system color cue from SystemThemeColors.
    val accent: Int? = null,
    override val icon: CatalogIcon? = null,
    override val progress: Float? = null,
    override val progressLook: JobLook? = JobLook.of(progress),
    override val progressParts: Int = 0,
    override val statusMark: StatusMark? = null,
    override val chip: RowTag? = null,
    override val iconFile: String? = null,
    override val summaryShown: Boolean = false,
) : CatalogItem<H> {
    init {
        require((inline != null) != (registryId != null)) { "Exactly one of inline/registryId must be set" }
    }

    /** The screen it opens: [inline], else the one [registry] (the host's registry of screens) holds under [registryId]. */
    fun resolve(registry: (String) -> CatalogScreen<H>?): CatalogScreen<H>? = inline ?: registryId?.let(registry)
}


/** An ordered group of items; [title] null means the ungrouped run at the top. */
data class CatalogGroup<H>(
    val id: String,
    val title: String?,
    val items: List<CatalogItem<H>>,
    /**
     * Live device state and one-shot device actions, rendered by the
     * Quick Menu's System tab and skipped by every Settings renderer:
     * Settings is configuration (docs/SPEC.md 7f, "Where things live").
     */
    val quickOnly: Boolean = false,
    /**
     * The glyph this group shows as an entry of the settings category column (docs/SPEC.md
     * "Settings layout"). Null: the first glyph one of its rows carries, else none.
     */
    val icon: CatalogIcon? = null,
    /**
     * The settings category this group belongs to (docs/SPEC.md "Settings layout"). Groups that name
     * the same category are one entry of the category column and their titles become the section
     * labels of its pane; a group with a category is never read as a hub of links. Null: the group
     * is its own category (or a hub, when it holds only links to other screens).
     */
    val category: String? = null,
    /**
     * Short facts about what the group is about, drawn as a row of status chips above its rows
     * (docs/SPEC.md 7j "Places": a store page's "Signed in", "128 games", "Synced 5 min ago"): the
     * page's header when it is the first group. A chip is a fact, never a control; anything that can
     * be pressed is a row. Drawn only above a group that has rows.
     */
    val chips: List<CatalogChip> = emptyList(),
)

/** One status chip ([CatalogGroup.chips]): a short fact, and whether it is a good state (signed in, up to date). */
data class CatalogChip(val label: String, val ok: Boolean = false)

/**
 * Cross-module screen lookup: the module that owns a management screen's
 * DATA registers its [CatalogScreen] here at process start (:app does
 * this from a manifest-declared init provider, so registration happens
 * before ANY surface -- including :shell-default's SettingsActivity,
 * which cannot depend on :app -- could try to render one), and catalogs
 * in lower modules reference it by id through [NestedScreenItem].
 */

/**
 * An item only one host can show: it carries what that host's renderer needs (droidtop's folder and document pickers,
 * Launcher3's own settings screens) and the shared renderer leaves it to the host. [confirmText] is asked before it runs.
 */
abstract class PlatformItem<H> : CatalogItem<H> {
    open val confirmText: String? get() = null
}
