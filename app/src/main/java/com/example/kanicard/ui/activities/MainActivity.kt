package com.example.kanicard.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.zIndex
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.kanicard.R
import com.example.kanicard.constants.AppBarHeight
import com.example.kanicard.constants.NavigationBarHeight
import com.example.kanicard.constants.PauseSearchHistoryKey
import com.example.kanicard.constants.SearchSource
import com.example.kanicard.constants.SearchSourceKey
import com.example.kanicard.data.entities.SearchHistory
import com.example.kanicard.data.local.KaniDatabase
import com.example.kanicard.ui.component.AppDrawerSheet
import com.example.kanicard.ui.component.Gap
import com.example.kanicard.ui.component.InputFieldHeight
import com.example.kanicard.ui.component.SearchBar
import com.example.kanicard.ui.navigation.mainNavigationBuilder
import com.example.kanicard.ui.screen.Screens
import com.example.kanicard.ui.theme.KaniCardTheme
import com.example.kanicard.ui.theme.NavigationBarAnimationSpec
import com.example.kanicard.utils.appBarScrollBehavior
import com.example.kanicard.utils.dataStore
import com.example.kanicard.utils.get
import com.example.kanicard.utils.rememberEnumPreference
import com.example.kanicard.utils.resetHeightOffset
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var database: KaniDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            KaniCardTheme {

                val navController = rememberNavController()
                val coroutineScope = rememberCoroutineScope()

                val backStackEntry by navController.currentBackStackEntryAsState()

                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        AppDrawerSheet(
                            modifier = Modifier,
                            onNavigate = {
                                coroutineScope.launch {
                                    drawerState.close()
                                }
                                navController.navigate(it)
                            }
                        )
                    },
                    modifier = Modifier,
                    gesturesEnabled = false
                ) {
                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        val width = maxWidth
                        val navigationItems = remember { Screens.MainScreens }
                        val topLevelScreens = listOf(
                            Screens.Home.route,
                            Screens.Statistics.route,
                        )

                        val focusManager = LocalFocusManager.current

                        val windowInsets = WindowInsets.systemBars
                        val density = LocalDensity.current
                        val bottomInset = with(density) { windowInsets.getBottom(density).toDp() }
                        val topInset = with(density) { windowInsets.getTop(density).toDp() }

                        var active by rememberSaveable {
                            mutableStateOf(false)
                        }
                        val (query, onQueryChange) = rememberSaveable(stateSaver = TextFieldValue.Saver) {
                            mutableStateOf(TextFieldValue())
                        }
                        val onActiveChange: (Boolean) -> Unit = { newActive ->
                            active = newActive
                            if (!newActive) {
                                focusManager.clearFocus()
                                if (backStackEntry?.destination?.route == Screens.Home.route) {
                                    onQueryChange(TextFieldValue())
                                }
                            }
                        }


                        val searchSource by rememberEnumPreference(
                            SearchSourceKey,
                            SearchSource.ONLINE
                        )
                        val searchBarFocusRequester = remember { FocusRequester() }
                        val onSearch: (String) -> Unit = {
                            if (it.isNotEmpty()) {
                                onActiveChange(false)
                                navController.navigate("search/$it")
                                if (dataStore[PauseSearchHistoryKey] != true) {
                                    database.query {
                                        insert(SearchHistory(query = it))
                                    }
                                }
                            }
                        }

                        val shouldShowNavigationBar = remember(backStackEntry, active) {
                            backStackEntry?.destination?.route == null
                                    || navigationItems.fastAny { it.route == backStackEntry?.destination?.route }
                                    && !active
                        }
                        val navigationBarHeight by animateDpAsState(
                            targetValue = if (shouldShowNavigationBar) NavigationBarHeight else 0.dp,
                            label = "",
                            animationSpec = NavigationBarAnimationSpec
                        )

                        val shouldShowTopBar = remember(backStackEntry, active) {
                            backStackEntry?.destination?.route == null
                                    || backStackEntry?.destination?.route == Screens.Home.route
//                                    && !active
                        }
                        val topBarHeight by animateDpAsState(
                            targetValue = if (shouldShowTopBar) AppBarHeight else 0.dp,
                            label = "",
                            animationSpec = NavigationBarAnimationSpec
                        )

                        val shouldShowSearchBar = remember(backStackEntry, active) {
                            (active
                                    || backStackEntry?.destination?.route == Screens.Home.route
                                    || backStackEntry?.destination?.route?.startsWith("/search") == true)
                        }

                        /*Scroll Behaviour*/
                        val appBarScrollBehavior = appBarScrollBehavior(
                            canScroll = {
                                backStackEntry?.destination?.route == Screens.Home.route
                                        || backStackEntry?.destination?.route == Screens.Statistics.route
                            }
                        )
                        val topAppBarScrollBehavior = appBarScrollBehavior(
                            canScroll = {
                                !(backStackEntry?.destination?.route == Screens.Home.route
                                        || backStackEntry?.destination?.route == Screens.Statistics.route)
                            }
                        )
                        val searchBarScrollBehavior = appBarScrollBehavior(
                            canScroll = {
                                backStackEntry?.destination?.route?.startsWith("search/") == false
                            }
                        )

                        Timber.d(shouldShowSearchBar.toString())

                        val localAwareWindowInset =
                            remember(bottomInset, shouldShowNavigationBar, shouldShowSearchBar) {
                                var bottom = bottomInset
                                var top = AppBarHeight
                                if (shouldShowNavigationBar) bottom += NavigationBarHeight
                                if (shouldShowSearchBar) top += InputFieldHeight
                                windowInsets
                                    .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
                                    .add(WindowInsets(bottom = bottom, top = top))
                            }



                        LaunchedEffect(active) {
                            if (active) {
                                searchBarScrollBehavior.state.resetHeightOffset()
                            }
                        }

                        CompositionLocalProvider(
                            LocalDatabase provides database,
                            LocalAwareWindowInset provides localAwareWindowInset
                        ) {
                            NavHost(
                                navController = navController,
                                startDestination = Screens.Home.route,
                                modifier = Modifier
                                    .nestedScroll(
                                        if (navigationItems.fastAny { it.route == backStackEntry?.destination?.route } ||
                                            backStackEntry?.destination?.route?.startsWith("search/") == true) {
                                            searchBarScrollBehavior.nestedScrollConnection
                                        } else {
                                            topAppBarScrollBehavior.nestedScrollConnection
                                        }
                                    )
                            ) {
                                mainNavigationBuilder(
                                    navController = navController,
                                    appBarScrollBehavior = appBarScrollBehavior,
                                    topAppBarScrollBehavior = topAppBarScrollBehavior
                                )
                            }

                            NavigationBar(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .offset {
                                        if (navigationBarHeight == 0.dp) {
                                            IntOffset(
                                                x = 0,
                                                y = (bottomInset + NavigationBarHeight).roundToPx()
                                            )
                                        } else {
                                            IntOffset(x = 0, y = 0)
                                        }
                                    }
                            ) {
                                navigationItems.fastForEach { screens ->
                                    key(screens.route) {
                                        NavigationBarItem(
                                            selected = backStackEntry?.destination?.hierarchy?.any { it.route == screens.route } == true,
                                            icon = {
                                                Icon(
                                                    painter = painterResource(screens.iconId),
                                                    contentDescription = null
                                                )
                                            },
                                            label = {
                                                Text(
                                                    text = stringResource(screens.titleId),
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            },
                                            onClick = {
                                                if (backStackEntry?.destination?.hierarchy?.any { it.route == screens.route } == true) {
                                                    backStackEntry?.savedStateHandle?.set(
                                                        "scrollToTop",
                                                        true
                                                    )
                                                } else {
                                                    navController.navigate(screens.route) {
                                                        popUpTo(navController.graph.startDestinationId) {
                                                            saveState = true
                                                        }
                                                        launchSingleTop = true
                                                        restoreState = true
                                                    }
                                                }
                                            }
                                        )
                                    }
                                }
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .animateContentSize()
                            ) {
                                TopAppBar(
                                    title = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.ic_fake_logo),
                                                contentDescription = null
                                            )
                                            Gap(8.dp)
                                            Text(
                                                text = stringResource(R.string.app_name)
                                            )
                                        }
                                    },
                                    actions = {
                                        IconButton(
                                            onClick = {
                                                // TODO: Sync function
                                            }
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.ic_sync),
                                                contentDescription = null
                                            )
                                        }

                                        IconButton(
                                            onClick = {
                                                coroutineScope.launch {
                                                    drawerState.open()
                                                }
                                            }
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.ic_menu),
                                                contentDescription = null
                                            )
                                        }
                                    },
                                    modifier = Modifier
                                        .offset {
                                            if (topBarHeight == 0.dp) {
                                                IntOffset(
                                                    x = 0,
                                                    y = -(AppBarHeight + topInset).roundToPx()
                                                )
                                            } else {
                                                IntOffset(0, 0)
                                            }
                                        }
//                                        .align(Alignment.TopCenter)
                                )
                                AnimatedVisibility(
                                    visible = shouldShowSearchBar,
                                    enter = fadeIn(),
                                    exit = fadeOut(),
                                    modifier = Modifier
                                        .zIndex(-1f)
                                ) {
                                    SearchBar(
                                        query = query,
                                        onQueryChange = onQueryChange,
                                        onSearch = {

                                        },
                                        active = active,
                                        onActiveChange = onActiveChange,
                                        scrollBehavior = searchBarScrollBehavior,
                                        modifier = Modifier
                                    ) {

                                    }
                                }
                            }

                            val state = TopAppBarDefaults.enterAlwaysScrollBehavior()
                        }

                    }
                }
            }
        }
    }
}

val LocalDatabase = staticCompositionLocalOf<KaniDatabase> { error("No database provided") }
val LocalAwareWindowInset =
    staticCompositionLocalOf<WindowInsets> { error("No WindowInset provided") }