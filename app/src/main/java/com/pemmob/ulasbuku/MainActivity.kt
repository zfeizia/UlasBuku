package com.pemmob.ulasbuku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pemmob.ulasbuku.data.model.Book
import com.pemmob.ulasbuku.ui.screens.*
import com.pemmob.ulasbuku.ui.theme.PureWhite
import com.pemmob.ulasbuku.ui.theme.TextPrimary
import com.pemmob.ulasbuku.ui.theme.UlasBukuTheme
import com.pemmob.ulasbuku.ui.theme.WarmCreamBg
import com.pemmob.ulasbuku.ui.theme.*
import com.pemmob.ulasbuku.ui.viewmodel.BookViewModel

// ─────────────────────────────────────────────────────────────────────────────
// NAVIGASI — Tab Bottom Navigation: Beranda | Cari | Profil
// ─────────────────────────────────────────────────────────────────────────────
enum class MainTab(val label: String, val icon: ImageVector) {
    KATALOG("Beranda", Icons.Default.AutoStories),
    SEARCH("Cari", Icons.Default.Search),
    PROFIL("Profil", Icons.Default.Person)
}

// ─────────────────────────────────────────────────────────────────────────────
// SCREEN SEALED INTERFACE — Type-Safe Navigation (manual UDF)
// ─────────────────────────────────────────────────────────────────────────────
sealed interface Screen {
    data object Splash : Screen
    data object Login : Screen
    data object Register : Screen
    data class Main(val tab: MainTab = MainTab.KATALOG) : Screen
    data class BookDetail(val book: Book, val returnTab: MainTab = MainTab.KATALOG) : Screen
    data class AllReviews(val book: Book, val returnTab: MainTab = MainTab.KATALOG) : Screen
    data class AuthorBooks(val authorName: String, val returnTab: MainTab = MainTab.KATALOG) : Screen
    data class SavedBooks(val returnTab: MainTab = MainTab.PROFIL) : Screen
    data class ReviewHistory(val returnTab: MainTab = MainTab.PROFIL) : Screen
    /**
     * Layar tulis ulasan.
     * [bookId] adalah ID buku yang akan diulas (dipass dari BookDetail).
     * [returnTab] adalah tab yang aktif saat kembali.
     */
    data class AddReview(val bookId: Int, val returnTab: MainTab = MainTab.KATALOG) : Screen
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UlasBukuTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFA8B9E4)   // seamless dengan SplashScreen
                ) {
                    UlasBukuApp()
                }
            }
        }
    }
}

@Composable
fun UlasBukuApp(viewModel: BookViewModel = viewModel()) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }
    val currentUser by viewModel.currentUser.collectAsState()

    when (val screen = currentScreen) {

        // ── SPLASH ─────────────────────────────────────────────────────────
        is Screen.Splash -> {
            SplashScreen(
                onNavigateToLogin = { currentScreen = Screen.Login },
                onNavigateToRegister = { currentScreen = Screen.Register }
            )
        }

        // ── LOGIN ──────────────────────────────────────────────────────────
        is Screen.Login -> {
            BackHandler { currentScreen = Screen.Splash }
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = { currentScreen = Screen.Main(MainTab.KATALOG) },
                onNavigateToRegister = { currentScreen = Screen.Register },
                onBackToWelcome = { currentScreen = Screen.Splash }
            )
        }

        // ── REGISTER ───────────────────────────────────────────────────────
        is Screen.Register -> {
            BackHandler { currentScreen = Screen.Splash }
            RegisterScreen(
                viewModel = viewModel,
                onRegisterSuccess = { currentScreen = Screen.Main(MainTab.KATALOG) },
                onNavigateToLogin = { currentScreen = Screen.Login },
                onBackToWelcome = { currentScreen = Screen.Splash }
            )
        }

        // ── MAIN (dengan BottomNavigation) ─────────────────────────────────
        is Screen.Main -> {
            var activeTab by remember { mutableStateOf(screen.tab) }

            Scaffold(
                bottomBar = {
                    NavigationBar(
                        containerColor = PureWhite,
                        contentColor = TextPrimary,
                        tonalElevation = 0.dp,
                        modifier = Modifier.border(1.dp, BorderSubtle)
                    ) {
                        MainTab.entries.forEach { tab ->
                            val isSelected = activeTab == tab
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { activeTab = tab },
                                icon = {
                                    Icon(
                                        imageVector = tab.icon,
                                        contentDescription = tab.label,
                                        modifier = Modifier.size(if (isSelected) 24.dp else 22.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = tab.label,
                                        fontSize = if (isSelected) 11.sp else 10.sp,
                                        fontWeight = if (isSelected)
                                            androidx.compose.ui.text.font.FontWeight.Black
                                        else
                                            androidx.compose.ui.text.font.FontWeight.Medium
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = TextPrimary,
                                    selectedTextColor = TextPrimary,
                                    indicatorColor = PastelBlueGradientStart,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted
                                )
                            )
                        }
                    }
                },
                containerColor = PureWhite
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    when (activeTab) {

                        // ── BERANDA ──────────────────────────────────────
                        MainTab.KATALOG -> BookListScreen(
                            viewModel = viewModel,
                            onBookClick = { book ->
                                viewModel.selectBook(book)
                                currentScreen = Screen.BookDetail(book, MainTab.KATALOG)
                            },
                            onProfileClick = { activeTab = MainTab.PROFIL },
                            onAuthorClick = { authorName ->
                                currentScreen = Screen.AuthorBooks(authorName, MainTab.KATALOG)
                            }
                        )

                        // ── PENCARIAN ────────────────────────────────────
                        MainTab.SEARCH -> SearchScreen(
                            viewModel = viewModel,
                            onBookClick = { book ->
                                viewModel.selectBook(book)
                                currentScreen = Screen.BookDetail(book, MainTab.SEARCH)
                            }
                        )


                        // ── PROFIL ───────────────────────────────────────
                        MainTab.PROFIL -> ProfileScreen(
                            viewModel = viewModel,
                            onBookClick = { book ->
                                viewModel.selectBook(book)
                                currentScreen = Screen.BookDetail(book, MainTab.PROFIL)
                            },
                            onSeeAllSavedBooksClick = {
                                currentScreen = Screen.SavedBooks(MainTab.PROFIL)
                            },
                            onSeeAllHistoryClick = {
                                currentScreen = Screen.ReviewHistory(MainTab.PROFIL)
                            },
                            onLogoutClick = {
                                viewModel.logout()
                                currentScreen = Screen.Login
                            }
                        )
                    }
                }
            }
        }

        // ── BOOK DETAIL ────────────────────────────────────────────────────
        is Screen.BookDetail -> {
            BackHandler {
                viewModel.clearSelectedBook()
                currentScreen = Screen.Main(screen.returnTab)
            }
            BookDetailScreen(
                book = screen.book,
                viewModel = viewModel,
                onBackClick = {
                    viewModel.clearSelectedBook()
                    currentScreen = Screen.Main(screen.returnTab)
                },
                onWriteReviewClick = { bookId ->
                    // Teruskan bookId ke layar AddReview
                    currentScreen = Screen.AddReview(bookId, screen.returnTab)
                },
                onSeeAllReviewsClick = {
                    currentScreen = Screen.AllReviews(screen.book, screen.returnTab)
                }
            )
        }

        // ── ALL REVIEWS ────────────────────────────────────────────────────
        is Screen.AllReviews -> {
            BackHandler {
                currentScreen = Screen.BookDetail(screen.book, screen.returnTab)
            }
            AllReviewsScreen(
                book = screen.book,
                viewModel = viewModel,
                onBackClick = {
                    currentScreen = Screen.BookDetail(screen.book, screen.returnTab)
                }
            )
        }

        // ── ADD REVIEW ─────────────────────────────────────────────────────
        
        // ── SAVED BOOKS ────────────────────────────────────────────────────
        is Screen.SavedBooks -> {
            BackHandler {
                currentScreen = Screen.Main(screen.returnTab)
            }
            SavedBooksScreen(
                viewModel = viewModel,
                onBackClick = { currentScreen = Screen.Main(screen.returnTab) },
                onBookClick = { book ->
                    viewModel.selectBook(book)
                    currentScreen = Screen.BookDetail(book, screen.returnTab)
                }
            )
        }

        // ── REVIEW HISTORY ──────────────────────────────────────────────────
        is Screen.ReviewHistory -> {
            BackHandler {
                currentScreen = Screen.Main(screen.returnTab)
            }
            ReviewHistoryScreen(
                viewModel = viewModel,
                onBackClick = { currentScreen = Screen.Main(screen.returnTab) },
                onBookClick = { book ->
                    viewModel.selectBook(book)
                    currentScreen = Screen.BookDetail(book, screen.returnTab)
                }
            )
        }

        // AUTHOR BOOKS
        is Screen.AuthorBooks -> {
            BackHandler {
                currentScreen = Screen.Main(screen.returnTab)
            }
            AuthorBooksScreen(
                authorName = screen.authorName,
                viewModel = viewModel,
                onBackClick = { currentScreen = Screen.Main(screen.returnTab) },
                onBookClick = { book ->
                    viewModel.selectBook(book)
                    currentScreen = Screen.BookDetail(book, screen.returnTab)
                }
            )
        }

        is Screen.AddReview -> {
            BackHandler {
                // Kembali ke Detail buku yang sama
                val book = viewModel.selectedBook.value
                if (book != null) {
                    currentScreen = Screen.BookDetail(book, screen.returnTab)
                } else {
                    currentScreen = Screen.Main(screen.returnTab)
                }
            }
            AddReviewScreen(
                viewModel = viewModel,
                bookId = screen.bookId,
                onBackClick = {
                    val book = viewModel.selectedBook.value
                    if (book != null) {
                        currentScreen = Screen.BookDetail(book, screen.returnTab)
                    } else {
                        currentScreen = Screen.Main(screen.returnTab)
                    }
                },
                onSubmitSuccess = {
                    // Setelah submit, kembali ke detail buku
                    val book = viewModel.selectedBook.value
                    if (book != null) {
                        currentScreen = Screen.BookDetail(book, screen.returnTab)
                    } else {
                        currentScreen = Screen.Main(screen.returnTab)
                    }
                }
            )
        }
    }
}