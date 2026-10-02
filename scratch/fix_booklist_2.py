import os

path_bl = r"app\src\main\java\com\pemmob\ulasbuku\ui\screens\BookListScreen.kt"
with open(path_bl, 'r', encoding='utf-8') as f:
    text = f.read()

# Manual string replacement for the Category row
target = """                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(264.dp),
                            contentPadding = PaddingValues(horizontal = 24.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(
                                items = booksForCat,
                                key = { book -> "cat_${cat.id}_${book.id}" }
                            ) { book ->
                                BookCardVertical(book = book, onClick = { onBookClick(book) })
                            }
                        }"""

replacement = """                        if (selectedCategoryId == null) {
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(264.dp),
                                contentPadding = PaddingValues(horizontal = 24.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(
                                    items = booksForCat,
                                    key = { book -> "cat_${cat.id}_${book.id}" }
                                ) { book ->
                                    BookCardVertical(book = book, onClick = { onBookClick(book) })
                                }
                            }
                        } else {
                            // GRID MODE 2 COLUMNS
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                val chunkedBooks = booksForCat.chunked(2)
                                chunkedBooks.forEach { rowBooks ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        rowBooks.forEach { book ->
                                            BookCardVertical(
                                                book = book,
                                                onClick = { onBookClick(book) },
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                        if (rowBooks.size == 1) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }"""

if target in text:
    text = text.replace(target, replacement)
    with open(path_bl, 'w', encoding='utf-8') as f:
        f.write(text)
    print("Success")
else:
    print("Target not found")
