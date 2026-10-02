import re

path_main = r"app\src\main\java\com\pemmob\ulasbuku\MainActivity.kt"
with open(path_main, 'r', encoding='utf-8') as f:
    text = f.read()

# Update BookListScreen call in MainActivity
old_bl_call = """MainTab.KATALOG -> BookListScreen(
                            viewModel = viewModel,
                            onBookClick = { book ->
                                viewModel.selectBook(book)
                                currentScreen = Screen.BookDetail(book, MainTab.KATALOG)
                            },
                            onProfileClick = { activeTab = MainTab.PROFIL }
                        )"""
new_bl_call = """MainTab.KATALOG -> BookListScreen(
                            viewModel = viewModel,
                            onBookClick = { book ->
                                viewModel.selectBook(book)
                                currentScreen = Screen.BookDetail(book, MainTab.KATALOG)
                            },
                            onProfileClick = { activeTab = MainTab.PROFIL },
                            onAuthorClick = { authorName ->
                                currentScreen = Screen.AuthorBooks(authorName, MainTab.KATALOG)
                            }
                        )"""
text = text.replace(old_bl_call, new_bl_call)

with open(path_main, 'w', encoding='utf-8') as f:
    f.write(text)
print("MainActivity updated BookList call")
