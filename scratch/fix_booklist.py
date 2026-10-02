import re

path_bl = r"app\src\main\java\com\pemmob\ulasbuku\ui\screens\BookListScreen.kt"
with open(path_bl, 'r', encoding='utf-8') as f:
    text = f.read()

# Add onAuthorClick to signature
if 'onAuthorClick: (String) -> Unit' not in text:
    text = text.replace('onProfileClick: () -> Unit', 'onProfileClick: () -> Unit,\n    onAuthorClick: (String) -> Unit = {}')

# Wrap mostReviewed and Authors in if (selectedCategoryId == null)
# To do this safely, we will find "3. SECTION: BUKU PALING BANYAK DIULAS" 
# and put `if (selectedCategoryId == null) {` before it, and close it after the author section.
sec3_idx = text.find('// 3. SECTION: BUKU PALING BANYAK DIULAS')
sec5_idx = text.find('// 5. SECTION: KATEGORI REKOMENDASI')

if sec3_idx != -1 and sec5_idx != -1 and 'if (selectedCategoryId == null) {' not in text[sec3_idx-50:sec3_idx]:
    # Replace the AuthorAvatarCard with clickable one
    text = text.replace('AuthorAvatarCard(\n                            name = name,\n                            subtitle = subtitle,\n                            bgColor = getAuthorAvatarBg(colorIdx)\n                        )',
                        'AuthorAvatarCard(\n                            name = name,\n                            subtitle = subtitle,\n                            bgColor = getAuthorAvatarBg(colorIdx),\n                            onClick = { onAuthorClick(name) }\n                        )')
    # Also update AuthorAvatarCard definition
    text = text.replace('fun AuthorAvatarCard(name: String, subtitle: String, bgColor: Color)',
                        'fun AuthorAvatarCard(name: String, subtitle: String, bgColor: Color, onClick: () -> Unit = {})')
    text = text.replace('modifier = Modifier.width(90.dp)',
                        'modifier = Modifier.width(90.dp).clickable { onClick() }')

    # Add the if condition
    block_before = text[:sec3_idx]
    block_mid = text[sec3_idx:sec5_idx]
    block_after = text[sec5_idx:]

    block_mid = "            if (selectedCategoryId == null) {\n" + "\n".join(["    " + line for line in block_mid.split('\n')]) + "\n            }\n\n            "
    text = block_before + block_mid + block_after

# Now modify Section 5 to show grid if selectedCategoryId != null
# Find the LazyRow for categories
lazyrow_regex = r'LazyRow\([^)]*\)\s*\{\s*items\(\s*items = booksForCat,\s*key = [^{]*\{[^}]*\}\s*\)\s*\{\s*book ->\s*BookCardVertical[^}]*\}\s*\}'

replacement = """
                        if (selectedCategoryId == null) {
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
                        }
"""
text = re.sub(lazyrow_regex, replacement.strip(), text, flags=re.DOTALL)

with open(path_bl, 'w', encoding='utf-8') as f:
    f.write(text)
print("BookListScreen modified")
