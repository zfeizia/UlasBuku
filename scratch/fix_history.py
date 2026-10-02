import re

# 1. Update BookModels.kt
path_models = r"app\src\main\java\com\pemmob\ulasbuku\data\model\BookModels.kt"
with open(path_models, 'r', encoding='utf-8') as f:
    text_models = f.read()

history_sealed = """
sealed class UserHistoryItem {
    data class ReviewItem(val book: Book, val review: Review) : UserHistoryItem()
    data class ReplyItem(val book: Book, val originalReview: Review, val reply: Reply) : UserHistoryItem()
}
"""
if 'sealed class UserHistoryItem' not in text_models:
    text_models += history_sealed
    with open(path_models, 'w', encoding='utf-8') as f:
        f.write(text_models)

# 2. Update BookRepository.kt
path_repo = r"app\src\main\java\com\pemmob\ulasbuku\data\repository\BookRepository.kt"
with open(path_repo, 'r', encoding='utf-8') as f:
    text_repo = f.read()

if 'fun getUserHistory' not in text_repo:
    # We will add a new method getUserHistory
    history_method = """
    fun getUserHistory(userName: String): List<com.pemmob.ulasbuku.data.model.UserHistoryItem> {
        val results = mutableListOf<com.pemmob.ulasbuku.data.model.UserHistoryItem>()
        for (book in books) {
            for (review in book.reviews) {
                if (review.reviewerName.equals(userName, ignoreCase = true) ||
                    (currentUser != null && review.reviewerName.equals(currentUser!!.name, ignoreCase = true))
                ) {
                    results.add(com.pemmob.ulasbuku.data.model.UserHistoryItem.ReviewItem(book, review))
                }
                for (reply in review.replies) {
                    if (reply.replierName.equals(userName, ignoreCase = true) ||
                        (currentUser != null && reply.replierName.equals(currentUser!!.name, ignoreCase = true))
                    ) {
                        results.add(com.pemmob.ulasbuku.data.model.UserHistoryItem.ReplyItem(book, review, reply))
                    }
                }
            }
        }
        return results
    }
"""
    # Insert it before getAllCommunityReviews
    text_repo = text_repo.replace('fun getAllCommunityReviews', history_method + '\n    fun getAllCommunityReviews')
    with open(path_repo, 'w', encoding='utf-8') as f:
        f.write(text_repo)

print("Models and Repository updated for History")
