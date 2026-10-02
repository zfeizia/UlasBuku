import re

path_main = r"app\src\main\java\com\pemmob\ulasbuku\MainActivity.kt"
with open(path_main, 'r', encoding='utf-8') as f:
    text = f.read()

# Add AuthorBooks to Screen sealed interface
if 'data class AuthorBooks' not in text:
    text = text.replace('data class BookDetail(val book: Book, val returnTab: MainTab = MainTab.KATALOG) : Screen',
                        'data class BookDetail(val book: Book, val returnTab: MainTab = MainTab.KATALOG) : Screen\n    data class AuthorBooks(val authorName: String, val returnTab: MainTab = MainTab.KATALOG) : Screen')

# Handle AuthorBooks in MainActivity navigation
new_route = """
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
"""
if 'is Screen.AuthorBooks' not in text:
    text = text.replace('is Screen.AddReview -> {', new_route + '\n        is Screen.AddReview -> {')

with open(path_main, 'w', encoding='utf-8') as f:
    f.write(text)
print("MainActivity modified")
