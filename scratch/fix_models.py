import re

path_models = r"app\src\main\java\com\pemmob\ulasbuku\data\model\BookModels.kt"
with open(path_models, 'r', encoding='utf-8') as f:
    text = f.read()

# Replace Book data class to compute rating and totalReviews dynamically
book_regex = r'data class Book\([^)]*\)\s*\{[^}]*\}'
target = """data class Book(
    val id: Int,
    val categoryId: Int,
    val title: String,
    val author: String,
    val synopsis: String,
    val isbn: String,
    val coverImg: String,
    val publisher: String? = "Gramedia Pustaka Utama",
    val releaseYear: String? = "2023",
    val reviews: MutableList<Review> = mutableListOf()
) {
    val rating: Double
        get() = if (reviews.isEmpty()) 0.0 else reviews.map { it.userRating.toDouble() }.average()

    val totalReviews: Int
        get() = reviews.size

    val displayPublisher: String
        get() = if (publisher.isNullOrBlank()) "Gramedia Pustaka Utama" else publisher

    val displayReleaseYear: String
        get() = if (releaseYear.isNullOrBlank()) "2023" else releaseYear
        
    val displayCoverImg: String
        get() = if (coverImg.isNullOrBlank()) "https://covers.openlibrary.org/b/isbn/$isbn-M.jpg" else coverImg
}"""

if 'val rating: Double,' in text:
    text = re.sub(r'data class Book\([\s\S]*?\}\n', target + '\n', text)
    with open(path_models, 'w', encoding='utf-8') as f:
        f.write(text)
    print("Models updated")
else:
    print("Already updated or not found")
