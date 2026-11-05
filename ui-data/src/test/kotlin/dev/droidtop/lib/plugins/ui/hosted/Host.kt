package dev.droidtop.lib.plugins.ui.hosted

// The model as a host uses it: droidtop declares these over android.content.Context in its own package, so its
// code names no type parameter. These tests do the same over a host of their own, and so check that it works.

/** The host these tests hand the model's callbacks. */
class TestHost

typealias CatalogItem = dev.droidtop.lib.plugins.ui.CatalogItem<TestHost>
typealias ChoiceItem = dev.droidtop.lib.plugins.ui.ChoiceItem<TestHost>
typealias ToggleItem = dev.droidtop.lib.plugins.ui.ToggleItem<TestHost>
typealias SliderItem = dev.droidtop.lib.plugins.ui.SliderItem<TestHost>
typealias TextInputItem = dev.droidtop.lib.plugins.ui.TextInputItem<TestHost>
typealias ActionItem = dev.droidtop.lib.plugins.ui.ActionItem<TestHost>
typealias TextBlockItem = dev.droidtop.lib.plugins.ui.TextBlockItem<TestHost>
typealias AsyncActionItem = dev.droidtop.lib.plugins.ui.AsyncActionItem<TestHost>
typealias CatalogScreen = dev.droidtop.lib.plugins.ui.CatalogScreen<TestHost>
typealias NestedScreenItem = dev.droidtop.lib.plugins.ui.NestedScreenItem<TestHost>
typealias CatalogGroup = dev.droidtop.lib.plugins.ui.CatalogGroup<TestHost>
typealias NoticeAction = dev.droidtop.lib.plugins.ui.NoticeAction<TestHost>
typealias Notice = dev.droidtop.lib.plugins.ui.Notice<TestHost>
typealias NoticeItem = dev.droidtop.lib.plugins.ui.NoticeItem<TestHost>
typealias CatalogPage = dev.droidtop.lib.plugins.ui.CatalogPage<TestHost>
typealias PageButton = dev.droidtop.lib.plugins.ui.PageButton<TestHost>
typealias PageHeaderItem = dev.droidtop.lib.plugins.ui.PageHeaderItem<TestHost>
typealias ButtonBarItem = dev.droidtop.lib.plugins.ui.ButtonBarItem<TestHost>
typealias PlatformItem = dev.droidtop.lib.plugins.ui.PlatformItem<TestHost>
typealias NoticeTone = dev.droidtop.lib.plugins.ui.NoticeTone
typealias NoticePlacement = dev.droidtop.lib.plugins.ui.NoticePlacement
typealias NoticePress = dev.droidtop.lib.plugins.ui.NoticePress
typealias JobLook = dev.droidtop.lib.plugins.ui.JobLook
typealias StatusMark = dev.droidtop.lib.plugins.ui.StatusMark
typealias CatalogIcon = dev.droidtop.lib.plugins.ui.CatalogIcon
typealias RowTag = dev.droidtop.lib.plugins.ui.RowTag
typealias ChoiceOption = dev.droidtop.lib.plugins.ui.ChoiceOption
typealias CatalogChip = dev.droidtop.lib.plugins.ui.CatalogChip
typealias PageHeader = dev.droidtop.lib.plugins.ui.PageHeader
typealias ButtonRole = dev.droidtop.lib.plugins.ui.ButtonRole
typealias CatalogLayout = dev.droidtop.lib.plugins.ui.CatalogLayout
typealias NoticeDialogs = dev.droidtop.lib.plugins.ui.NoticeDialogs
typealias DangerConfirm = dev.droidtop.lib.plugins.ui.DangerConfirm
typealias ConfirmAnswer = dev.droidtop.lib.plugins.ui.ConfirmAnswer
